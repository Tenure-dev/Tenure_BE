package com.tenure.domain.purchase.service;

import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOfferExpirationProcessor {

    private final ItemRepository itemRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final PurchaseOfferExpirationService purchaseOfferExpirationService;

    @Transactional
    public boolean expireOne(Long offerId, Long itemId, LocalDateTime now) {
        if (itemRepository.findByIdForUpdate(itemId).isEmpty()) {
            return false;
        }
        PurchaseOffer offer = purchaseOfferRepository.findByIdForUpdate(offerId)
                .orElse(null);
        if (offer == null) {
            return false;
        }
        return purchaseOfferExpirationService.expireIfSentAndExpired(offer, now);
    }
}
