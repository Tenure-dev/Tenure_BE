package com.tenure.domain.trade.service;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.purchase.service.PurchaseOfferExpirationService;
import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.entity.TradeCreateCommand;
import com.tenure.domain.trade.enums.TradeAction;
import com.tenure.domain.trade.enums.TradeActor;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeTransition;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOfferAcceptService {

    // 이 가드의 기준은 "거래가 FSM상 종결됐는가"가 아니라 "아이템이 새 거래 가능 상태로 풀려났는가"다.
    // 아이템이 풀리는 시점은 소유권 이전(TRANSFERRED)뿐이다. SETTLED/COMPLETED까지 제외하면 구매확정 자동
    // 연쇄(PURCHASE_CONFIRMED -> SETTLED -> COMPLETED) 이후에도 Item 상태 전이가 없어 아이템이 여전히
    // OWNED로 남고, 그 상태에서 새 offer 생성·수락이 통과해 같은 아이템에 Trade가 중복 생성될 수 있다.
    private static final Set<TradeStatus> ITEM_RELEASING_STATUSES = EnumSet.of(TradeStatus.TRANSFERRED);

    private final ItemRepository itemRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final TradeRepository tradeRepository;
    private final PurchaseOfferExpirationService purchaseOfferExpirationService;

    @Transactional(noRollbackFor = CustomException.class)
    public TradeDetailResponse acceptPurchaseOffer(Long offerId, Long currentUserId) {
        Long itemId = purchaseOfferRepository.findItemIdById(offerId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_FOUND));
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.ITEM_NOT_FOUND));
        PurchaseOffer offer = purchaseOfferRepository.findByIdForUpdate(offerId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_FOUND));

        validateOwner(offer, currentUserId);

        LocalDateTime now = LocalDateTime.now();
        if (purchaseOfferExpirationService.expireIfSentAndExpired(offer, now)) {
            throw new CustomException(PurchaseOfferErrorCode.PURCHASE_REQUEST_EXPIRED);
        }
        if (!offer.isSent()) {
            throw new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_SENT);
        }

        validateNoActiveTrade(item.getId());

        offer.acceptAndCaptureAuthorization();

        cancelCompetingOffers(item.getId(), offer.getId());

        Trade trade = Trade.create(toTradeCreateCommand(item, offer));
        tradeRepository.save(trade);

        TradeViewerMode viewerMode = TradeViewerMode.SELLER;
        List<TradeAction> availableActions = TradeTransition.resolveActions(trade.getStatus(), TradeActor.from(viewerMode));
        return TradeDetailResponse.of(trade, viewerMode, availableActions);
    }

    private void validateOwner(PurchaseOffer offer, Long currentUserId) {
        if (!offer.getOwner().getId().equals(currentUserId)) {
            throw new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
        }
    }

    private void validateNoActiveTrade(Long itemId) {
        if (tradeRepository.existsByItemIdAndStatusNotIn(itemId, ITEM_RELEASING_STATUSES)) {
            throw new CustomException(TradeErrorCode.TRADE_ALREADY_EXISTS_FOR_ITEM);
        }
    }

    private TradeCreateCommand toTradeCreateCommand(Item item, PurchaseOffer offer) {
        return new TradeCreateCommand(
                TradeSourceType.PURCHASE_OFFER,
                offer.getId(),
                item,
                null,
                offer.getProposer(),
                offer.getOwner(),
                offer.getOfferPrice(),
                offer.getTotalPaymentAmount(),
                offer.getProposerShippingFee(),
                offer.getProposerServiceFee(),
                // 현재 수수료 모델(제안자 전액 부담)에서는 이 식이 항상 0이지만, 스냅샷 간 항등식이지
                // 정책 재계산이 아니다. 수수료 모델이 owner 차감형으로 바뀌어도 생성 시점 스냅샷만으로
                // 자동으로 올바른 값이 되므로, 0 하드코딩과 달리 그 경우 조용히 틀리지 않는다.
                (offer.getOfferPrice() + offer.getProposerShippingFee()) - offer.getOwnerSettlementAmount(),
                offer.getOwnerSettlementAmount(),
                offer.getPaymentMethodId(),
                offer.getPaymentAuthorizationId(),
                offer.getDeliveryReceiverName(),
                offer.getDeliveryPhone(),
                offer.getDeliveryAddressLine1(),
                offer.getDeliveryAddressLine2(),
                offer.getDeliveryPostalCode(),
                offer.getDeliveryRequestNote()
        );
    }

    private void cancelCompetingOffers(Long itemId, Long acceptedOfferId) {
        List<PurchaseOffer> sentOffers = purchaseOfferRepository.findSentByItemIdForUpdate(
                itemId,
                PurchaseOfferStatus.SENT
        );
        for (PurchaseOffer sentOffer : sentOffers) {
            if (sentOffer.getId().equals(acceptedOfferId)) {
                continue;
            }
            sentOffer.cancelAndReleaseAuthorization();
        }
    }
}
