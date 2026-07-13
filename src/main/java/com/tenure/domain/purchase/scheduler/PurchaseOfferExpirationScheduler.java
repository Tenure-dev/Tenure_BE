package com.tenure.domain.purchase.scheduler;

import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository.ExpiredPurchaseOfferTarget;
import com.tenure.domain.purchase.service.PurchaseOfferExpirationProcessor;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseOfferExpirationScheduler {

    private final PurchaseOfferRepository purchaseOfferRepository;
    private final PurchaseOfferExpirationProcessor purchaseOfferExpirationProcessor;

    @Scheduled(fixedDelayString = "${tenure.scheduler.purchase-offer-expiration-delay-ms:60000}")
    public void expireSentPurchaseOffers() {
        LocalDateTime now = LocalDateTime.now();
        List<ExpiredPurchaseOfferTarget> targets = purchaseOfferRepository.findExpiredSentTargets(
                PurchaseOfferStatus.SENT,
                now
        );
        for (ExpiredPurchaseOfferTarget target : targets) {
            purchaseOfferExpirationProcessor.expireOne(target.getOfferId(), target.getItemId(), now);
        }
    }
}
