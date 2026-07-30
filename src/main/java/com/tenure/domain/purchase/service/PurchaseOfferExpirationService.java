package com.tenure.domain.purchase.service;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.notification.service.NotificationFactory;
import com.tenure.domain.notification.service.NotificationService;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseOfferExpirationService {

    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;

    public boolean expireIfSentAndExpired(PurchaseOffer offer, LocalDateTime now) {
        if (!offer.isSent() || !offer.isExpiredAt(now)) {
            return false;
        }
        offer.expireAndReleaseAuthorization();
        saveExpiredNotifications(offer);
        return true;
    }

    private void saveExpiredNotifications(PurchaseOffer offer) {
        Item item = offer.getItem();
        notificationService.saveAll(List.of(
                notificationFactory.requestExpiredForRequester(offer.getProposer(), item, offer.getId(), false),
                notificationFactory.requestExpiredForOwner(offer.getOwner(), offer.getProposer(), item, offer.getId(), false)
        ));
    }
}
