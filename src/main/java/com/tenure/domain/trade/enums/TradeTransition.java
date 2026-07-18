package com.tenure.domain.trade.enums;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.entity.ItemHistory;
import com.tenure.domain.item.enums.EndReason;
import com.tenure.domain.item.exception.ItemErrorCode;
import com.tenure.domain.item.repository.ItemHistoryRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.dto.TradeStatusChangeRequest;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.event.TradeStatusChangedEvent;
import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

/**
 * 거래 상태 전이 규칙의 단일 진실 소스. 행 하나가 (from, to, 허용 액터, 검증, 부수효과)를 정의한다.
 * SETTLED, COMPLETED, TRANSFERRED는 외부에서 요청 가능한 전이가 아니므로 여기에 행으로 존재하지 않고,
 * CONFIRM_PURCHASE 처리의 부수효과로만 도달한다.
 */
public enum TradeTransition {

    REGISTER_SHIPMENT(TradeStatus.PAID, TradeStatus.SHIPPED, EnumSet.of(TradeActor.SELLER), TradeAction.REGISTER_SHIPMENT) {
        @Override
        public void validate(TradeStatusChangeRequest request) {
            DeliveryCarrier carrier = request.deliveryCarrier();
            String trackingNumber = request.trackingNumber();
            if (carrier == null || !StringUtils.hasText(trackingNumber)) {
                throw new CustomException(TradeErrorCode.TRADE_TRACKING_REQUIRED);
            }
            if (carrier == DeliveryCarrier.OTHER) {
                if (!StringUtils.hasText(request.customDeliveryCarrierName())) {
                    throw new CustomException(TradeErrorCode.TRADE_TRACKING_REQUIRED);
                }
                return;
            }
            carrier.validateTrackingNumber(trackingNumber);
        }

        @Override
        public int applyUpdate(TradeRepository tradeRepository, Trade trade, TradeStatusChangeRequest request) {
            return tradeRepository.updateToShipped(
                    trade.getId(),
                    from,
                    to,
                    request.deliveryCarrier(),
                    request.customDeliveryCarrierName(),
                    request.trackingNumber(),
                    LocalDateTime.now()
            );
        }

        @Override
        public void applySideEffects(Context context) {
            context.eventPublisher().publishEvent(TradeStatusChangedEvent.of(context.trade(), from, to));
        }
    },

    MARK_DELIVERED(TradeStatus.SHIPPED, TradeStatus.DELIVERED, EnumSet.of(TradeActor.SELLER), TradeAction.MARK_DELIVERED) {
        @Override
        public void validate(TradeStatusChangeRequest request) {
            // 검증 없음
        }

        @Override
        public int applyUpdate(TradeRepository tradeRepository, Trade trade, TradeStatusChangeRequest request) {
            return tradeRepository.updateToDelivered(trade.getId(), from, to, LocalDateTime.now());
        }

        @Override
        public void applySideEffects(Context context) {
            context.eventPublisher().publishEvent(TradeStatusChangedEvent.of(context.trade(), from, to));
        }
    },

    CONFIRM_PURCHASE(
            TradeStatus.DELIVERED,
            TradeStatus.PURCHASE_CONFIRMED,
            EnumSet.of(TradeActor.BUYER, TradeActor.SYSTEM),
            TradeAction.CONFIRM_PURCHASE
    ) {
        @Override
        public void validate(TradeStatusChangeRequest request) {
            // 검증 없음
        }

        @Override
        public int applyUpdate(TradeRepository tradeRepository, Trade trade, TradeStatusChangeRequest request) {
            return tradeRepository.updateToConfirmed(trade.getId(), from, to, LocalDateTime.now());
        }

        @Override
        public void applySideEffects(Context context) {
            Trade trade = context.trade();
            // 연쇄 구간의 벌크 UPDATE가 영속성 컨텍스트를 clear해 trade를 detach시키므로,
            // 이벤트 페이로드는 아직 managed인 지금 확정해둔다.
            Long tradeId = trade.getId();
            Long itemId = trade.getItem().getId();
            Long buyerUserId = trade.getBuyer().getId();
            Long sellerUserId = trade.getSeller().getId();

            context.eventPublisher().publishEvent(new TradeStatusChangedEvent(
                    tradeId, TradeStatus.DELIVERED, TradeStatus.PURCHASE_CONFIRMED, buyerUserId, sellerUserId));

            // Product 조회/변경은 뒤따르는 벌크 UPDATE가 영속성 컨텍스트를 clear하기 전에 수행하며,
            // 해당 UPDATE의 flushAutomatically가 변경을 먼저 DB로 내보내 dirty checking 유실을 막는다.
            markProductSoldIfPresent(context);

            // 정산 확인 로직은 PG 도입 시 SETTLED 전이 트리거 교체로 추가 가능한 구조. 현재는 구매확정 직후 자동 정산 처리한다.
            chainToSettled(context, tradeId);
            context.eventPublisher().publishEvent(new TradeStatusChangedEvent(
                    tradeId, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED, buyerUserId, sellerUserId));

            // SETTLED -> COMPLETED와 마찬가지로 이벤트를 발행하지 않는다(기존 COMPLETED 전이도 이벤트가 없음).
            chain(context, tradeId, TradeStatus.SETTLED, TradeStatus.COMPLETED);

            // TRANSFERRED는 시각 컬럼이 없는 내부 상태이므로 chainToSettled 같은 전용 메서드 없이 범용 chain()을 재사용한다.
            chain(context, tradeId, TradeStatus.COMPLETED, TradeStatus.TRANSFERRED);
            transferItemOwnership(context, tradeId, itemId, buyerUserId);
        }

        private void markProductSoldIfPresent(Context context) {
            Trade trade = context.trade();
            if (trade.getProduct() == null) {
                return;
            }
            Product product = context.productRepository().findByIdForUpdate(trade.getProduct().getId())
                    .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
            product.markSold();
        }

        // 락 순서 컨벤션(Product -> Item -> Intent/Offer)에 따라 Item 락을 먼저 획득한다.
        // Product는 이 단계에서 변경하지 않으므로 락을 잡지 않는다.
        private void transferItemOwnership(Context context, Long tradeId, Long itemId, Long buyerUserId) {
            Item item = context.itemRepository().findByIdForUpdate(itemId)
                    .orElseThrow(() -> new CustomException(ItemErrorCode.ITEM_NOT_FOUND));
            User buyer = context.userRepository().getReferenceById(buyerUserId);
            Trade tradeRef = context.tradeRepository().getReferenceById(tradeId);
            LocalDateTime transferredAt = LocalDateTime.now();

            item.transferOwnership(buyer);

            closeOpenHistory(context, itemId, transferredAt);
            // close()의 UPDATE는 dirty checking이라 커밋 시 지연되지만, 뒤이은 save()는 IDENTITY라 즉시 INSERT되므로
            // flush 없이는 같은 item_id에 열린 행이 순간적으로 2개가 되어 부분 유니크 인덱스를 위반한다.
            context.itemHistoryRepository().flush();
            context.itemHistoryRepository().save(ItemHistory.ofTenureTrade(item, buyer, tradeRef, transferredAt));

            cancelSentOffers(context, itemId);
        }

        // 아이템 등록 시 createItem이 이미 FIRST_REGISTERED 열린 행을 만들어두므로, 여기서 열린 행이
        // 없는 건 정합성 위반이다(마이그레이션 백필로 기존 아이템도 모두 열린 행을 갖도록 보장했다).
        private void closeOpenHistory(Context context, Long itemId, LocalDateTime endedAt) {
            ItemHistory openHistory = context.itemHistoryRepository().findByItemIdAndEndedAtIsNull(itemId)
                    .orElseThrow(() -> new IllegalStateException(
                            "소유 이력 불변식 위반: 열린 행이 없습니다. itemId=%d".formatted(itemId)
                    ));
            openHistory.close(EndReason.TENURE_TRADE, endedAt);
        }

        // Item 락을 이미 잡은 뒤이므로 락 순서 규약 위반이 아니다.
        private void cancelSentOffers(Context context, Long itemId) {
            List<PurchaseOffer> sentOffers = context.purchaseOfferRepository().findSentByItemIdForUpdate(
                    itemId, PurchaseOfferStatus.SENT
            );
            for (PurchaseOffer sentOffer : sentOffers) {
                sentOffer.cancelAndReleaseAuthorization();
            }
        }
    };

    /**
     * 자동 연쇄 전이. 직전 단계에서 같은 트랜잭션이 방금 만든 상태를 대상으로 하므로 영향 행이 0이면
     * 외부 경합이 아니라 불변식 위반이다. TRADE_INVALID_TRANSITION으로 던지면 스케줄러가 이를 정상 경합으로
     * 보고 삼켜 거래가 중간 상태로 커밋되므로, 구분되는 예외로 롤백시킨다.
     */
    private static void chain(Context context, Long tradeId, TradeStatus from, TradeStatus to) {
        int rows = context.tradeRepository().updateStatus(tradeId, from, to);
        if (rows == 0) {
            throw new IllegalStateException(
                    "거래 자동 연쇄 전이 실패: tradeId=%d, %s -> %s".formatted(tradeId, from, to)
            );
        }
    }

    /**
     * PURCHASE_CONFIRMED -> SETTLED 연쇄 전이 전용. settledAt을 status와 같은 조건부 UPDATE로 원자적으로 기록해야 해서
     * status만 바꾸는 범용 chain()을 쓸 수 없다.
     */
    private static void chainToSettled(Context context, Long tradeId) {
        int rows = context.tradeRepository().updateToSettled(
                tradeId, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED, LocalDateTime.now());
        if (rows == 0) {
            throw new IllegalStateException(
                    "거래 자동 연쇄 전이 실패: tradeId=%d, %s -> %s".formatted(
                            tradeId, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED)
            );
        }
    }

    final TradeStatus from;
    final TradeStatus to;
    private final Set<TradeActor> allowedActors;
    private final TradeAction action;

    TradeTransition(TradeStatus from, TradeStatus to, Set<TradeActor> allowedActors, TradeAction action) {
        this.from = from;
        this.to = to;
        this.allowedActors = allowedActors;
        this.action = action;
    }

    public abstract void validate(TradeStatusChangeRequest request);

    public abstract int applyUpdate(TradeRepository tradeRepository, Trade trade, TradeStatusChangeRequest request);

    public abstract void applySideEffects(Context context);

    public boolean isAllowed(TradeActor actor) {
        return allowedActors.contains(actor);
    }

    public static Optional<TradeTransition> find(TradeStatus from, TradeStatus to) {
        return Arrays.stream(values())
                .filter(transition -> transition.from == from && transition.to == to)
                .findFirst();
    }

    public static List<TradeAction> resolveActions(TradeStatus from, TradeActor actor) {
        return Arrays.stream(values())
                .filter(transition -> transition.from == from && transition.allowedActors.contains(actor))
                .map(transition -> transition.action)
                .toList();
    }

    public record Context(
            TradeRepository tradeRepository,
            ProductRepository productRepository,
            ItemRepository itemRepository,
            UserRepository userRepository,
            PurchaseOfferRepository purchaseOfferRepository,
            ItemHistoryRepository itemHistoryRepository,
            ApplicationEventPublisher eventPublisher,
            Trade trade
    ) {
    }
}
