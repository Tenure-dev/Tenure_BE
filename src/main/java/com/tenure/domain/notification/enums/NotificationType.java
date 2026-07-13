package com.tenure.domain.notification.enums;

public enum NotificationType {
    PRICE_CHANGED,              // 관심 상품의 가격이 변동됨
    SALE_STARTED,               // 관심 상품의 할인이 시작됨
    PURCHASE_INTENT_RECEIVED,   // 구매 의사(구매 요청)를 받음
    PURCHASE_OFFER_RECEIVED,    // 가격 제안(오퍼)을 받음
    PURCHASE_INTENT_ACCEPTED,   // 보낸 구매 의사가 수락됨
    PURCHASE_OFFER_ACCEPTED,    // 보낸 가격 제안이 수락됨
    PURCHASE_INTENT_REJECTED,   // 보낸 구매 의사가 거절됨
    PURCHASE_OFFER_REJECTED,    // 보낸 가격 제안이 거절됨
    PURCHASE_REQUEST_EXPIRED,   // 구매 요청이 기간 만료로 종료됨
    PURCHASE_REQUEST_SUPERSEDED,// 구매 요청이 다른 요청에 의해 대체됨
    ITEM_SOLD,                  // 상품이 판매 완료됨
    SHIPPING_STARTED,           // 배송이 시작됨
    TRADE_STATUS_CHANGED,       // 거래 상태가 변경됨
    FOLLOW_REQUEST_RECEIVED,    // 팔로우 요청을 받음
    FOLLOW_REQUEST_ACCEPTED,    // 보낸 팔로우 요청이 수락됨
    TAG_REVIEW_REQUIRED         // 태그에 대한 검토가 필요함
}
