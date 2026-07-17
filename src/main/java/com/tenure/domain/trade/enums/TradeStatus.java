package com.tenure.domain.trade.enums;

public enum TradeStatus {
    PAID,
    SHIPPED,
    DELIVERED,
    PURCHASE_CONFIRMED,
    SETTLED,
    COMPLETED,
    TRANSFERRED;

    // TRANSFERRED는 소유권 이전이 끝난 뒤 도달하는 내부 상태로, API 계약상으로는 COMPLETED로 노출된다.
    // 엔티티에 저장된 실제 상태(TRANSFERRED)는 그대로 두고 응답 조립 시점에만 이 메서드로 변환해서 쓴다.
    public TradeStatus displayStatus() {
        return this == TRANSFERRED ? COMPLETED : this;
    }
}
