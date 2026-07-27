package com.tenure.domain.notification.service;

import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.chat.enums.MessageType;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationType;
import com.tenure.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    // 채팅 메시지 수신 (오프라인 상태)
    public Notification chatMessage(User receiver, User sender, ChatRoom chatRoom,
                                    MessageType messageType, String content) {
        String body = messageType == MessageType.IMAGE ? "사진을 보냈어요." : content;
        Item item = chatRoom.getItem();
        return Notification.of(
                receiver, NotificationType.CHAT_MESSAGE_CREATED,
                body, chatRoom.getId(),
                sender.getUsername(), item.getBrandName(), item.getItemName(),
                sender.getProfileImageUrl()
        );
    }

    // 팔로우
    public Notification follow(User receiver, User follower) {
        String body = follower.getUsername() + "님이 회원님을 팔로우했어요.";
        return Notification.of(
                receiver, NotificationType.FOLLOW_CREATED,
                body, follower.getId(),
                follower.getUsername(), null, null,
                follower.getProfileImageUrl()
        );
    }

    // 관심 등록
    public Notification wishCreated(User receiver, User wisher, Item item) {
        String body = wisher.getUsername() + "님이 회원님의 아이템을 관심 등록했어요.";
        return Notification.of(
                receiver, NotificationType.WISH_CREATED,
                body, item.getId(),
                wisher.getUsername(), item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 거래 의사 수신 (판매자에게)
    public Notification purchaseIntentSent(User receiver, User sender, Item item, Long intentId) {
        String body = sender.getUsername() + "님이 거래 의사를 보냈어요.";
        return Notification.of(
                receiver, NotificationType.PURCHASE_INTENT_SENT,
                body, intentId,
                sender.getUsername(), item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 구매 제안 수신 (아이템 소유자에게)
    public Notification purchaseOfferSent(User receiver, User sender, Item item, Long offerId, int price) {
        String body = sender.getUsername() + "님이 " + price + "원에 구매 제안을 보냈어요.";
        return Notification.of(
                receiver, NotificationType.PURCHASE_OFFER_SENT,
                body, offerId,
                sender.getUsername(), item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 거래 의사 수락 (구매자에게)
    public Notification requestAcceptedForIntent(User receiver, Item item, Long tradeId) {
        return Notification.of(
                receiver, NotificationType.REQUEST_ACCEPTED,
                "거래 의사가 수락됐어요. 거래가 시작되었습니다.", tradeId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 구매 제안 수락 (제안자에게)
    public Notification requestAcceptedForOffer(User receiver, Item item, Long tradeId) {
        return Notification.of(
                receiver, NotificationType.REQUEST_ACCEPTED,
                "구매 제안이 수락됐어요. 거래가 시작되었습니다.", tradeId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 거래 의사 거절 (구매자에게)
    public Notification requestRejectedForIntent(User receiver, Item item, Long targetId) {
        return Notification.of(
                receiver, NotificationType.REQUEST_REJECTED,
                "거래 의사가 거절됐어요.", targetId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 구매 제안 거절 (제안자에게)
    public Notification requestRejectedForOffer(User receiver, Item item, Long targetId) {
        return Notification.of(
                receiver, NotificationType.REQUEST_REJECTED,
                "구매 제안이 거절됐어요.", targetId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 요청자 취소 (상대방에게) - isIntent=true: 거래 의사, false: 구매 제안
    public Notification requestCanceledByRequester(User receiver, User sender, Item item,
                                                   Long targetId, boolean isIntent) {
        String requestType = isIntent ? "거래 의사" : "구매 제안";
        String body = sender.getUsername() + "님이 " + requestType + "를 취소했어요.";
        return Notification.of(
                receiver, NotificationType.REQUEST_CANCELED_BY_REQUESTER,
                body, targetId,
                sender.getUsername(), item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 만료 — 요청자에게
    public Notification requestExpiredForRequester(User receiver, Item item,
                                                   Long targetId, boolean isIntent) {
        String requestType = isIntent ? "거래 의사" : "구매 제안";
        String body = "24시간 동안 응답이 없어 " + requestType + "가 만료됐어요.";
        return Notification.of(
                receiver, NotificationType.REQUEST_EXPIRED,
                body, targetId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 만료 — 소유자에게
    public Notification requestExpiredForOwner(User receiver, User sender, Item item,
                                               Long targetId, boolean isIntent) {
        String requestType = isIntent ? "거래 의사" : "구매 제안";
        String body = sender.getUsername() + "님의 " + requestType + "가 만료됐어요.";
        return Notification.of(
                receiver, NotificationType.REQUEST_EXPIRED,
                body, targetId,
                sender.getUsername(), item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 경쟁 수락으로 자동 취소 (요청자에게)
    public Notification requestCanceledByCompeting(User receiver, Item item, Long targetId) {
        return Notification.of(
                receiver, NotificationType.REQUEST_CANCELED_BY_COMPETING_ACCEPTANCE,
                "다른 요청이 먼저 수락되어 자동으로 취소됐어요.", targetId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 아이템 삭제로 취소 (요청자에게)
    public Notification requestCanceledByItemDelete(User receiver, Item item,
                                                    Long targetId, boolean isIntent) {
        String requestType = isIntent ? "거래 의사" : "구매 제안";
        String body = "아이템이 삭제되어 " + requestType + "가 취소됐어요.";
        return Notification.of(
                receiver, NotificationType.REQUEST_CANCELED_BY_ITEM_DELETE,
                body, targetId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 거래 취소 (상대방에게)
    public Notification tradeCanceled(User receiver, User canceler, Item item, Long tradeId) {
        String body = canceler.getUsername() + "님이 거래를 취소했어요.";
        return Notification.of(
                receiver, NotificationType.TRADE_CANCELED,
                body, tradeId,
                canceler.getUsername(), item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 발송 완료 (구매자에게)
    public Notification shipmentRegistered(User receiver, Item item, Long tradeId) {
        return Notification.of(
                receiver, NotificationType.SHIPMENT_REGISTERED,
                "판매자가 상품을 발송했어요.", tradeId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 배송 완료 (구매자에게)
    public Notification deliveryCompleted(User receiver, Item item, Long tradeId) {
        return Notification.of(
                receiver, NotificationType.DELIVERY_COMPLETED,
                "배송이 완료됐어요. 상품을 확인해 주세요.", tradeId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 구매 확정 (판매자에게)
    public Notification purchaseConfirmed(User receiver, Item item, Long tradeId) {
        return Notification.of(
                receiver, NotificationType.PURCHASE_CONFIRMED,
                "구매가 확정됐어요.", tradeId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 정산 완료 (판매자에게)
    public Notification settlementCompleted(User receiver, Item item, Long tradeId) {
        return Notification.of(
                receiver, NotificationType.SETTLEMENT_COMPLETED,
                "정산이 완료됐어요.", tradeId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 거래 완료 (구매자에게)
    public Notification tradeCompleted(User receiver, Item item, Long tradeId) {
        return Notification.of(
                receiver, NotificationType.TRADE_COMPLETED,
                "거래가 완료됐어요.", tradeId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 판매 전환 (관심 유저에게)
    public Notification productCreated(User receiver, Item item, Long productId) {
        return Notification.of(
                receiver, NotificationType.PRODUCT_CREATED,
                "관심 아이템이 판매를 시작했어요.", productId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 미판매 전환 (관심 유저에게)
    public Notification productReturnedToUnsold(User receiver, Item item, Long productId) {
        return Notification.of(
                receiver, NotificationType.PRODUCT_RETURNED_TO_UNSOLD,
                "관심 아이템이 미판매 상태로 변경됐어요.", productId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 판매 완료 (관심 유저에게)
    public Notification productSold(User receiver, Item item, Long productId) {
        return Notification.of(
                receiver, NotificationType.PRODUCT_SOLD,
                "관심 아이템이 판매 완료됐어요.", productId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }

    // 가격 변경 (관심 유저에게)
    public Notification productPriceChanged(User receiver, Item item, Long productId,
                                            int oldPrice, int newPrice) {
        String body = oldPrice + "원에서 " + newPrice + "원으로 변경됐어요.";
        return Notification.of(
                receiver, NotificationType.PRODUCT_PRICE_CHANGED,
                body, productId,
                null, item.getBrandName(), item.getItemName(),
                item.getRepresentativeImageUrl()
        );
    }
}
