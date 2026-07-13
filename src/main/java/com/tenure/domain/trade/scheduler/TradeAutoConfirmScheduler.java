package com.tenure.domain.trade.scheduler;

import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.trade.service.TradeService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 배송 완료(DELIVERED) 후 일정 시간이 지난 거래를 구매확정 처리한다.
 * 조회는 트랜잭션 없이 수행하고, 건별 확정 처리는 별도 빈인 TradeService의 REQUIRES_NEW 트랜잭션에서
 * 수행해 self-invocation으로 인해 트랜잭션 전파가 무시되는 것을 방지한다.
 */
@Slf4j
@Component
public class TradeAutoConfirmScheduler {

    private final TradeRepository tradeRepository;
    private final TradeService tradeService;
    private final long confirmHours;

    public TradeAutoConfirmScheduler(
            TradeRepository tradeRepository,
            TradeService tradeService,
            @Value("${tenure.trade.auto-confirm.confirm-hours:72}") long confirmHours
    ) {
        this.tradeRepository = tradeRepository;
        this.tradeService = tradeService;
        this.confirmHours = confirmHours;
    }

    @Scheduled(fixedDelayString = "${tenure.trade.auto-confirm.interval-minutes:10}", timeUnit = TimeUnit.MINUTES)
    public void confirmOverdueDeliveries() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(confirmHours);
        List<Long> tradeIds = tradeRepository.findIdsByStatusAndDeliveredAtBefore(TradeStatus.DELIVERED, threshold);

        for (Long tradeId : tradeIds) {
            try {
                boolean confirmed = tradeService.confirmPurchaseBySystem(tradeId);
                if (!confirmed) {
                    log.info("자동 구매확정 대상에서 제외됨 (이미 처리됨): tradeId={}", tradeId);
                }
            } catch (Exception e) {
                log.error("자동 구매확정 처리 실패: tradeId={}", tradeId, e);
            }
        }
    }
}
