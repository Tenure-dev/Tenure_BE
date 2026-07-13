package com.tenure.domain.trade.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.trade.service.TradeService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TradeAutoConfirmSchedulerTest {

    private static final long CONFIRM_HOURS = 72;

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private TradeService tradeService;

    private TradeAutoConfirmScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new TradeAutoConfirmScheduler(tradeRepository, tradeService, CONFIRM_HOURS);
    }

    @Test
    void confirmOverdueDeliveries_queriesWithConfiguredThresholdAndConfirmsEachTrade() {
        when(tradeRepository.findIdsByStatusAndDeliveredAtBefore(eq(TradeStatus.DELIVERED), any(LocalDateTime.class)))
                .thenReturn(List.of(1L, 2L));
        when(tradeService.confirmPurchaseBySystem(1L)).thenReturn(true);
        when(tradeService.confirmPurchaseBySystem(2L)).thenReturn(true);

        scheduler.confirmOverdueDeliveries();

        ArgumentCaptor<LocalDateTime> thresholdCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(tradeRepository).findIdsByStatusAndDeliveredAtBefore(eq(TradeStatus.DELIVERED), thresholdCaptor.capture());
        LocalDateTime expectedThreshold = LocalDateTime.now().minusHours(CONFIRM_HOURS);
        assertThat(thresholdCaptor.getValue()).isCloseTo(expectedThreshold, new TemporalUnitWithinOffset(2, ChronoUnit.SECONDS));

        verify(tradeService).confirmPurchaseBySystem(1L);
        verify(tradeService).confirmPurchaseBySystem(2L);
    }

    @Test
    void confirmOverdueDeliveries_noCandidatesUnderThreshold_confirmsNothing() {
        when(tradeRepository.findIdsByStatusAndDeliveredAtBefore(eq(TradeStatus.DELIVERED), any(LocalDateTime.class)))
                .thenReturn(List.of());

        scheduler.confirmOverdueDeliveries();

        verify(tradeService, never()).confirmPurchaseBySystem(anyLong());
    }

    @Test
    void confirmOverdueDeliveries_zeroRowsAffectedByConcurrentManualConfirm_skipsWithoutThrowing() {
        when(tradeRepository.findIdsByStatusAndDeliveredAtBefore(eq(TradeStatus.DELIVERED), any(LocalDateTime.class)))
                .thenReturn(List.of(3L));
        when(tradeService.confirmPurchaseBySystem(3L)).thenReturn(false);

        assertThatCode(() -> scheduler.confirmOverdueDeliveries()).doesNotThrowAnyException();

        verify(tradeService).confirmPurchaseBySystem(3L);
    }

    @Test
    void confirmOverdueDeliveries_oneTradeThrows_continuesProcessingRemainingTrades() {
        when(tradeRepository.findIdsByStatusAndDeliveredAtBefore(eq(TradeStatus.DELIVERED), any(LocalDateTime.class)))
                .thenReturn(List.of(4L, 5L));
        when(tradeService.confirmPurchaseBySystem(4L)).thenThrow(new RuntimeException("boom"));
        when(tradeService.confirmPurchaseBySystem(5L)).thenReturn(true);

        assertThatCode(() -> scheduler.confirmOverdueDeliveries()).doesNotThrowAnyException();

        verify(tradeService).confirmPurchaseBySystem(4L);
        verify(tradeService).confirmPurchaseBySystem(5L);
    }
}
