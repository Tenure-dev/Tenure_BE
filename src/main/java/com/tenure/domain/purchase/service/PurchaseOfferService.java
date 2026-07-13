package com.tenure.domain.purchase.service;

import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse.ViewerRole;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.global.exception.CustomException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOfferService {

    private final ItemRepository itemRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final PurchaseOfferExpirationService purchaseOfferExpirationService;

    @Transactional
    public PurchaseOfferDetailResponse getPurchaseOfferDetail(Long offerId, Long currentUserId) {
        Long itemId = purchaseOfferRepository.findItemIdById(offerId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_FOUND));
        itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.ITEM_NOT_FOUND));
        PurchaseOffer offer = purchaseOfferRepository.findByIdForUpdate(offerId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_FOUND));

        ViewerRole viewerRole = resolveViewerRole(offer, currentUserId);
        LocalDateTime now = LocalDateTime.now();
        purchaseOfferExpirationService.expireIfSentAndExpired(offer, now);
        return PurchaseOfferDetailResponse.from(offer, viewerRole, now);
    }

    private ViewerRole resolveViewerRole(PurchaseOffer offer, Long currentUserId) {
        if (offer.getProposer().getId().equals(currentUserId)) {
            return ViewerRole.PROPOSER;
        }
        if (offer.getOwner().getId().equals(currentUserId)) {
            return ViewerRole.OWNER;
        }
        throw new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
    }
}
