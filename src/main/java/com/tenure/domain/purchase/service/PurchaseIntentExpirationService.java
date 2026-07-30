package com.tenure.domain.purchase.service;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.notification.service.NotificationFactory;
import com.tenure.domain.notification.service.NotificationService;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseIntentExpirationService {

    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;

    public boolean expireIfSentAndExpired(PurchaseIntent intent, LocalDateTime now) {
        if (!intent.isSent() || !intent.isExpiredAt(now)) {
            return false;
        }
        intent.expireAndReleaseAuthorization();
        saveExpiredNotifications(intent);
        return true;
    }

    private void saveExpiredNotifications(PurchaseIntent intent) {
        Item item = intent.getProduct().getItem();
        notificationService.saveAll(List.of(
                notificationFactory.requestExpiredForRequester(intent.getBuyer(), item, intent.getId(), true),
                notificationFactory.requestExpiredForOwner(intent.getSeller(), intent.getBuyer(), item, intent.getId(), true)
        ));
    }
}
