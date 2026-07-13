package com.tenure.domain.purchase.service;

import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PurchaseIntentExpirationScheduler {

    private final PurchaseIntentRepository purchaseIntentRepository;
    private final PurchaseIntentExpirationProcessor purchaseIntentExpirationProcessor;

    @Scheduled(fixedDelayString = "${tenure.scheduler.purchase-intent-expiration.fixed-delay:60000}")
    public void expirePurchaseIntents() {
        LocalDateTime now = LocalDateTime.now();
        List<Long> expiredIntentIds = purchaseIntentRepository.findExpiredSentIds(
                PurchaseIntentStatus.SENT,
                now
        );

        for (Long intentId : expiredIntentIds) {
            try {
                purchaseIntentExpirationProcessor.expire(intentId, now);
            } catch (RuntimeException e) {
                log.warn("Failed to expire purchase intent. intentId={}", intentId, e);
            }
        }
    }
}
