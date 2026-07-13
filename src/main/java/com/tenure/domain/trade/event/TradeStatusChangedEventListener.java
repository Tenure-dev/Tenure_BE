package com.tenure.domain.trade.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class TradeStatusChangedEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TradeStatusChangedEvent event) {
        log.info(
                "거래 상태 변경 알림 대상: tradeId={}, from={}, to={}, buyerUserId={}, sellerUserId={}",
                event.tradeId(), event.from(), event.to(), event.buyerUserId(), event.sellerUserId()
        );
        // TODO: Notification 도메인 연동 후 event.to() 값에 따라 알림을 저장한다 (SHIPMENT_REGISTERED, DELIVERY_COMPLETED,
        //  PURCHASE_CONFIRMED, SETTLEMENT_COMPLETED). Notification 엔티티/저장 로직은 notification 도메인 담당.
    }
}
