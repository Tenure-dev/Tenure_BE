package com.tenure.domain.trade.service;

import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.repository.ProductRepository;
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
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final TradeRepository tradeRepository;
    private final PurchaseIntentExpirationService purchaseIntentExpirationService;

    @Transactional(noRollbackFor = CustomException.class)
    public TradeDetailResponse acceptPurchaseIntent(Long intentId, Long currentUserId) {
        Long productId = purchaseIntentRepository.findProductIdById(intentId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_FOUND));
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PRODUCT_NOT_FOUND));
        itemRepository.findByIdForUpdate(product.getItem().getId())
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

        TradeViewerMode viewerMode = TradeViewerMode.SELLER;
        List<TradeAction> availableActions = TradeTransition.resolveActions(trade.getStatus(), TradeActor.from(viewerMode));
        return TradeDetailResponse.of(trade, viewerMode, availableActions);
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
                intent.getDeliveryRequestNote()
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
