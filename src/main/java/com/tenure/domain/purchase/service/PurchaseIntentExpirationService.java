package com.tenure.domain.purchase.service;

import com.tenure.domain.purchase.entity.PurchaseIntent;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class PurchaseIntentExpirationService {

    public boolean expireIfSentAndExpired(PurchaseIntent intent, LocalDateTime now) {
        if (!intent.isSent() || !intent.isExpiredAt(now)) {
            return false;
        }
        intent.expireAndReleaseAuthorization();
        return true;
    }
}
