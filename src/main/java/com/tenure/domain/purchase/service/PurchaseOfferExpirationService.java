package com.tenure.domain.purchase.service;

import com.tenure.domain.purchase.entity.PurchaseOffer;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOfferExpirationService {

    public boolean expireIfSentAndExpired(PurchaseOffer offer, LocalDateTime now) {
        if (!offer.isSent() || !offer.isExpiredAt(now)) {
            return false;
        }
        offer.expireAndReleaseAuthorization();
        return true;
    }
}
