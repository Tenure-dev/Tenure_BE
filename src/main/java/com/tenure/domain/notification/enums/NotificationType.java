package com.tenure.domain.notification.enums;

public enum NotificationType {
    // 거래 요청
    PURCHASE_INTENT_SENT,                       // 판매중 상품의 거래 의사 도착
    PURCHASE_OFFER_SENT,                        // 미판매 아이템의 구매 제안 도착
    REQUEST_ACCEPTED,                           // 거래 의사 또는 구매 제안 수락됨
    REQUEST_REJECTED,                           // 거래 의사 또는 구매 제안 거절됨
    REQUEST_CANCELED_BY_REQUESTER,              // 요청자가 수락 전 취소
    REQUEST_EXPIRED,                            // 24시간 응답 없어 만료됨
    REQUEST_CANCELED_BY_COMPETING_ACCEPTANCE,   // 다른 요청이 먼저 수락되어 취소됨
    REQUEST_CANCELED_BY_ITEM_DELETE,            // 아이템 삭제로 취소됨

    // 거래 상태
    TRADE_CANCELED,                             // 발송 전 거래 취소
    SHIPMENT_REGISTERED,                        // 운송장 등록 완료 (상품 발송)
    DELIVERY_COMPLETED,                         // 배송 완료됨
    PURCHASE_CONFIRMED,                         // 구매 확정됨
    SETTLEMENT_COMPLETED,                       // 판매자 정산 완료
    TRADE_COMPLETED,                            // 구매자 거래 완료

    // 관심 아이템
    WISH_CREATED,                               // 타 사용자가 내 아이템을 관심 등록함
    PRODUCT_CREATED,                            // 관심 아이템 판매 전환됨
    PRODUCT_RETURNED_TO_UNSOLD,                 // 관심 아이템 미판매 전환됨
    PRODUCT_SOLD,                               // 관심 아이템 판매 완료됨
    PRODUCT_PRICE_CHANGED,                      // 관심 아이템 가격 변경됨

    // 채팅
    CHAT_MESSAGE_CREATED,                       // 새로운 채팅 메시지 수신

    // 팔로우
    FOLLOW_CREATED                              // 새로운 사용자가 나를 팔로우함
}
