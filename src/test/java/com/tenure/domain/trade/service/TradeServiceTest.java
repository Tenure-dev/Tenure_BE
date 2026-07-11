package com.tenure.domain.trade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.trade.dto.TradeListItemResponse;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeRole;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.response.PageResponse;
import java.lang.reflect.Constructor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    private static final Long CURRENT_USER_ID = 1L;

    @Mock
    private TradeRepository tradeRepository;

    private TradeService tradeService;

    @BeforeEach
    void setUp() {
        tradeService = new TradeService(tradeRepository);
    }

    @Test
    void getTradeList_withBuyerRole_queriesByBuyerWithCurrentUserId() {
        Trade trade = trade(100L, CURRENT_USER_ID, 2L, TradeStatus.PAID);
        Page<Trade> page = new PageImpl<>(List.of(trade));
        when(tradeRepository.findAllByBuyer(eq(CURRENT_USER_ID), eq(TradeStatus.PAID), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, TradeRole.BUYER, TradeStatus.PAID, 0, 20);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).tradeId()).isEqualTo(100L);
        verify(tradeRepository).findAllByBuyer(eq(CURRENT_USER_ID), eq(TradeStatus.PAID), any(Pageable.class));
    }

    @Test
    void getTradeList_withSellerRole_queriesBySellerWithCurrentUserId() {
        Trade trade = trade(101L, 2L, CURRENT_USER_ID, TradeStatus.SHIPPED);
        Page<Trade> page = new PageImpl<>(List.of(trade));
        when(tradeRepository.findAllBySeller(eq(CURRENT_USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, TradeRole.SELLER, null, 0, 20);

        assertThat(response.getContent()).hasSize(1);
        verify(tradeRepository).findAllBySeller(eq(CURRENT_USER_ID), isNull(), any(Pageable.class));
    }

    @Test
    void getTradeList_withoutRole_queriesByParticipantWithCurrentUserId() {
        Trade trade = trade(102L, CURRENT_USER_ID, 3L, TradeStatus.DELIVERED);
        Page<Trade> page = new PageImpl<>(List.of(trade));
        when(tradeRepository.findAllByParticipant(eq(CURRENT_USER_ID), isNull(), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, null, null, 0, 20);

        assertThat(response.getContent()).hasSize(1);
        verify(tradeRepository).findAllByParticipant(eq(CURRENT_USER_ID), isNull(), any(Pageable.class));
    }

    @Test
    void getTradeList_passesStatusFilterToRepository() {
        Page<Trade> page = new PageImpl<>(List.of());
        when(tradeRepository.findAllByParticipant(eq(CURRENT_USER_ID), eq(TradeStatus.SETTLED), any(Pageable.class)))
                .thenReturn(page);

        tradeService.getTradeList(CURRENT_USER_ID, null, TradeStatus.SETTLED, 0, 20);

        verify(tradeRepository).findAllByParticipant(eq(CURRENT_USER_ID), eq(TradeStatus.SETTLED), any(Pageable.class));
    }

    private Trade trade(Long id, Long buyerId, Long sellerId, TradeStatus status) {
        Trade trade = instantiate(Trade.class);
        ReflectionTestUtils.setField(trade, "id", id);
        ReflectionTestUtils.setField(trade, "sourceType", TradeSourceType.PURCHASE_INTENT);
        ReflectionTestUtils.setField(trade, "sourceId", 1L);
        ReflectionTestUtils.setField(trade, "item", item(10L));
        ReflectionTestUtils.setField(trade, "buyer", user(buyerId));
        ReflectionTestUtils.setField(trade, "seller", user(sellerId));
        ReflectionTestUtils.setField(trade, "paymentAmount", 50000);
        ReflectionTestUtils.setField(trade, "settlementAmount", 48000);
        ReflectionTestUtils.setField(trade, "status", status);
        return trade;
    }

    private Item item(Long id) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
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
