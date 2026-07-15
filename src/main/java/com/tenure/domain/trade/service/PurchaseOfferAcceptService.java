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

    private static final Set<TradeStatus> FINAL_TRADE_STATUSES =
            EnumSet.of(TradeStatus.SETTLED, TradeStatus.COMPLETED, TradeStatus.TRANSFERRED);

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
        if (tradeRepository.existsByItemIdAndStatusNotIn(itemId, FINAL_TRADE_STATUSES)) {
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
                0,
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
