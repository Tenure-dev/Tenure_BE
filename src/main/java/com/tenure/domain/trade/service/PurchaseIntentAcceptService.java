package com.tenure.domain.trade.service;

import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.product.service.ProductService;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.exception.PurchaseIntentErrorCode;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.domain.purchase.service.PurchaseIntentExpirationService;
import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.entity.TradeCreateCommand;
import com.tenure.domain.trade.enums.TradeAction;
import com.tenure.domain.trade.enums.TradeActor;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeTransition;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseIntentAcceptService {

    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;
    private final ProductService productService;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final TradeRepository tradeRepository;
    private final PurchaseIntentExpirationService purchaseIntentExpirationService;
    private final FollowRelationshipRepository followRelationshipRepository;

    @Transactional(noRollbackFor = CustomException.class)
    public TradeDetailResponse acceptPurchaseIntent(Long intentId, Long currentUserId) {
        Long productId = purchaseIntentRepository.findProductIdById(intentId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_FOUND));
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PRODUCT_NOT_FOUND));
        Item item = itemRepository.findByIdForUpdate(product.getItem().getId())
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.ITEM_NOT_FOUND));
        PurchaseIntent intent = purchaseIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_FOUND));

        validateSeller(intent, currentUserId);

        LocalDateTime now = LocalDateTime.now();
        if (purchaseIntentExpirationService.expireIfSentAndExpired(intent, now)) {
            throw new CustomException(PurchaseIntentErrorCode.PURCHASE_REQUEST_EXPIRED);
        }
        if (!intent.isSent()) {
            throw new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_SENT);
        }

        product.startTrading();
        intent.acceptAndCaptureAuthorization();

        cancelCompetingIntents(product.getId(), intent.getId());

        Trade trade = Trade.create(toTradeCreateCommand(intent));
        tradeRepository.save(trade);

        // 이 트랜잭션은 noRollbackFor = CustomException이라 중간에 CustomException이 나도 커밋된다.
        // 알림을 앞단에 두면 거래가 성사되지 않은 채 위시 유저에게 "거래 시작"만 나갈 수 있으므로,
        // CustomException을 던질 수 있는 구간을 모두 지난 뒤에 발송한다.
        productService.notifyWishersTradingStarted(item, intent.getBuyer().getId());

        TradeViewerMode viewerMode = TradeViewerMode.SELLER;
        List<TradeAction> availableActions = TradeTransition.resolveActions(trade.getStatus(), TradeActor.from(viewerMode));
        long counterpartFollowerCount = followRelationshipRepository.countByFollowing_IdAndStatus(
                trade.getBuyer().getId(),
                FollowStatus.ACCEPTED
        );
        return TradeDetailResponse.of(trade, viewerMode, availableActions, counterpartFollowerCount);
    }

    private void validateSeller(PurchaseIntent intent, Long currentUserId) {
        if (!intent.getSeller().getId().equals(currentUserId)) {
            throw new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_ACCESS_DENIED);
        }
    }

    private TradeCreateCommand toTradeCreateCommand(PurchaseIntent intent) {
        return new TradeCreateCommand(
                TradeSourceType.PURCHASE_INTENT,
                intent.getId(),
                intent.getProduct().getItem(),
                intent.getProduct(),
                intent.getBuyer(),
                intent.getSeller(),
                intent.getProductPrice(),
                intent.getTotalPaymentAmount(),
                intent.getBuyerShippingFee(),
                intent.getBuyerServiceFee(),
                intent.getSellerServiceFee(),
                intent.getSellerSettlementAmount(),
                intent.getPaymentMethodId(),
                intent.getPaymentAuthorizationId(),
                intent.getDeliveryReceiverName(),
                intent.getDeliveryPhone(),
                intent.getDeliveryAddressLine1(),
                intent.getDeliveryAddressLine2(),
                intent.getDeliveryPostalCode(),
                intent.getDeliveryRequestNote(),
                null
        );
    }

    private void cancelCompetingIntents(Long productId, Long acceptedIntentId) {
        List<PurchaseIntent> sentIntents = purchaseIntentRepository.findSentByProductIdForUpdate(
                productId,
                PurchaseIntentStatus.SENT
        );
        for (PurchaseIntent sentIntent : sentIntents) {
            if (sentIntent.getId().equals(acceptedIntentId)) {
                continue;
            }
            sentIntent.cancelAndReleaseAuthorization();
        }
    }
}
