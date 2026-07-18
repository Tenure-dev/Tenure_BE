package com.tenure.domain.item.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tenure.domain.item.enums.AcquisitionType;
import com.tenure.domain.item.enums.EndReason;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.user.entity.User;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ItemHistoryTest {

    @Test
    void ofFirstRegistration_createsOpenRowWithoutTrade() {
        Item item = instantiate(Item.class);
        User owner = instantiate(User.class);
        LocalDateTime startedAt = LocalDateTime.of(2025, 1, 1, 0, 0);

        ItemHistory history = ItemHistory.ofFirstRegistration(item, owner, startedAt);

        assertThat(history.getItem()).isEqualTo(item);
        assertThat(history.getOwner()).isEqualTo(owner);
        assertThat(history.getTrade()).isNull();
        assertThat(history.getAcquisitionType()).isEqualTo(AcquisitionType.FIRST_REGISTERED);
        assertThat(history.getStartedAt()).isEqualTo(startedAt);
        assertThat(history.getEndReason()).isNull();
        assertThat(history.getEndedAt()).isNull();
    }

    @Test
    void ofTenureTrade_createsOpenRowWithTrade() {
        Item item = instantiate(Item.class);
        User newOwner = instantiate(User.class);
        Trade trade = instantiate(Trade.class);
        LocalDateTime startedAt = LocalDateTime.of(2025, 6, 1, 0, 0);

        ItemHistory history = ItemHistory.ofTenureTrade(item, newOwner, trade, startedAt);

        assertThat(history.getOwner()).isEqualTo(newOwner);
        assertThat(history.getTrade()).isEqualTo(trade);
        assertThat(history.getAcquisitionType()).isEqualTo(AcquisitionType.TENURE_TRADE);
        assertThat(history.getStartedAt()).isEqualTo(startedAt);
        assertThat(history.getEndReason()).isNull();
        assertThat(history.getEndedAt()).isNull();
    }

    @Test
    void close_setsEndReasonAndEndedAt() {
        ItemHistory history = ItemHistory.ofFirstRegistration(
                instantiate(Item.class), instantiate(User.class), LocalDateTime.of(2025, 1, 1, 0, 0)
        );
        LocalDateTime endedAt = LocalDateTime.of(2025, 6, 1, 0, 0);

        history.close(EndReason.EXTERNAL_SALE, endedAt);

        assertThat(history.getEndReason()).isEqualTo(EndReason.EXTERNAL_SALE);
        assertThat(history.getEndedAt()).isEqualTo(endedAt);
    }

    @Test
    void close_alreadyClosed_throwsIllegalStateException() {
        ItemHistory history = ItemHistory.ofFirstRegistration(
                instantiate(Item.class), instantiate(User.class), LocalDateTime.of(2025, 1, 1, 0, 0)
        );
        history.close(EndReason.TENURE_TRADE, LocalDateTime.of(2025, 6, 1, 0, 0));

        assertThatThrownBy(() -> history.close(EndReason.EXTERNAL_SALE, LocalDateTime.of(2025, 7, 1, 0, 0)))
                .isInstanceOf(IllegalStateException.class);
    }

    private <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
