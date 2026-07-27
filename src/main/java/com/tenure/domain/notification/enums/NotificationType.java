package com.tenure.domain.notification.enums;

import static com.tenure.domain.notification.enums.NotificationCategory.*;
import static com.tenure.domain.notification.enums.TargetType.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    // 거래 요청
    PURCHASE_INTENT_SENT(NEEDS_ACTION, PURCHASE_INTENT),           // 판매중 상품의 거래 의사 도착
    PURCHASE_OFFER_SENT(NEEDS_ACTION, PURCHASE_OFFER),             // 미판매 아이템의 구매 제안 도착
    REQUEST_ACCEPTED(TRADE_STATUS, TRADE),                         // 거래 의사 또는 구매 제안 수락됨
    REQUEST_REJECTED(TRADE_STATUS, TRADE),                         // 거래 의사 또는 구매 제안 거절됨
    REQUEST_CANCELED_BY_REQUESTER(TRADE_STATUS, TRADE),            // 요청자가 수락 전 취소
    REQUEST_EXPIRED(TRADE_STATUS, TRADE),                          // 24시간 응답 없어 만료됨
    REQUEST_CANCELED_BY_COMPETING_ACCEPTANCE(TRADE_STATUS, TRADE), // 다른 요청이 먼저 수락되어 취소됨
    REQUEST_CANCELED_BY_ITEM_DELETE(TRADE_STATUS, TRADE),          // 아이템 삭제로 취소됨

    // 거래 상태
    TRADE_CANCELED(TRADE_STATUS, TRADE),                           // 발송 전 거래 취소
    SHIPMENT_REGISTERED(TRADE_STATUS, TRADE),                      // 운송장 등록 완료 (상품 발송)
    DELIVERY_COMPLETED(TRADE_STATUS, TRADE),                       // 배송 완료됨
    PURCHASE_CONFIRMED(TRADE_STATUS, TRADE),                       // 구매 확정됨
    SETTLEMENT_COMPLETED(TRADE_STATUS, TRADE),                     // 판매자 정산 완료
    TRADE_COMPLETED(TRADE_STATUS, TRADE),                          // 구매자 거래 완료

    // 관심 아이템
    WISH_CREATED(INTEREST, ITEM),                                  // 타 사용자가 내 아이템을 관심 등록함
    PRODUCT_CREATED(ITEM_NEWS, ITEM),                              // 관심 아이템 판매 전환됨
    PRODUCT_RETURNED_TO_UNSOLD(ITEM_NEWS, ITEM),                   // 관심 아이템 미판매 전환됨
    PRODUCT_SOLD(ITEM_NEWS, ITEM),                                 // 관심 아이템 판매 완료됨
    PRODUCT_PRICE_CHANGED(ITEM_NEWS, ITEM),                        // 관심 아이템 가격 변경됨

    // 채팅
    CHAT_MESSAGE_CREATED(NEEDS_ACTION, CHAT),                      // 새로운 채팅 메시지 수신

    // 팔로우
    FOLLOW_CREATED(INTEREST, USER);                                // 새로운 사용자가 나를 팔로우함

    private final NotificationCategory category;
    private final TargetType targetType;
}
