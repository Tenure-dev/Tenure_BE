package com.tenure.domain.trade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.dto.TradeListItemResponse;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeAction;
import com.tenure.domain.trade.enums.TradeRole;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
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

    @Test
    void getTradeDetail_asBuyer_returnsBuyerView() {
        Trade trade = trade(100L, CURRENT_USER_ID, 2L, TradeStatus.PAID);
        when(tradeRepository.findById(100L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(100L, CURRENT_USER_ID);

        assertThat(response.viewerMode()).isEqualTo(TradeViewerMode.BUYER);
        assertThat(response.itemPrice()).isEqualTo(40000);
        assertThat(response.shippingFee()).isEqualTo(3000);
        assertThat(response.paymentAmount()).isEqualTo(43000);
        assertThat(response.settlementAmount()).isNull();
        assertThat(response.buyerServiceFee()).isEqualTo(0);
        assertThat(response.sellerServiceFee()).isNull();
    }

    @Test
    void getTradeDetail_asSeller_returnsSellerView() {
        Trade trade = trade(101L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        when(tradeRepository.findById(101L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(101L, CURRENT_USER_ID);

        assertThat(response.viewerMode()).isEqualTo(TradeViewerMode.SELLER);
        assertThat(response.itemPrice()).isEqualTo(40000);
        assertThat(response.shippingFee()).isEqualTo(3000);
        assertThat(response.settlementAmount()).isEqualTo(40600);
        assertThat(response.paymentAmount()).isNull();
        assertThat(response.sellerServiceFee()).isEqualTo(2400);
        assertThat(response.buyerServiceFee()).isNull();
    }

    @Test
    void getTradeDetail_asThirdParty_throwsTradeNotFound() {
        Trade trade = trade(102L, 2L, 3L, TradeStatus.PAID);
        when(tradeRepository.findById(102L)).thenReturn(Optional.of(trade));

        assertThatThrownBy(() -> tradeService.getTradeDetail(102L, CURRENT_USER_ID))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_NOT_FOUND);
    }

    @Test
    void getTradeDetail_tradeNotFound_throwsTradeNotFound() {
        when(tradeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tradeService.getTradeDetail(999L, CURRENT_USER_ID))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_NOT_FOUND);
    }

    @Test
    void getTradeDetail_paidStatusAsSeller_returnsRegisterShipmentAction() {
        Trade trade = trade(103L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        when(tradeRepository.findById(103L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(103L, CURRENT_USER_ID);

        assertThat(response.availableActions()).containsExactly(TradeAction.REGISTER_SHIPMENT);
    }

    @Test
    void getTradeDetail_paidStatusAsBuyer_returnsEmptyActions() {
        Trade trade = trade(104L, CURRENT_USER_ID, 2L, TradeStatus.PAID);
        when(tradeRepository.findById(104L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(104L, CURRENT_USER_ID);

        assertThat(response.availableActions()).isEmpty();
    }

    @Test
    void getTradeDetail_deliveredStatusAsBuyer_returnsConfirmPurchaseAction() {
        Trade trade = trade(105L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        when(tradeRepository.findById(105L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(105L, CURRENT_USER_ID);

        assertThat(response.availableActions()).containsExactly(TradeAction.CONFIRM_PURCHASE);
    }

    @Test
    void getTradeDetail_withProduct_returnsProductId() {
        Trade trade = tradeWithProduct(106L, CURRENT_USER_ID, 2L, TradeStatus.PAID, 5L);
        when(tradeRepository.findById(106L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(106L, CURRENT_USER_ID);

        assertThat(response.productId()).isEqualTo(5L);
    }

    private Trade trade(Long id, Long buyerId, Long sellerId, TradeStatus status) {
        Trade trade = instantiate(Trade.class);
        ReflectionTestUtils.setField(trade, "id", id);
        ReflectionTestUtils.setField(trade, "sourceType", TradeSourceType.PURCHASE_INTENT);
        ReflectionTestUtils.setField(trade, "sourceId", 1L);
        ReflectionTestUtils.setField(trade, "item", item(10L));
        ReflectionTestUtils.setField(trade, "buyer", user(buyerId));
        ReflectionTestUtils.setField(trade, "seller", user(sellerId));
        ReflectionTestUtils.setField(trade, "itemPrice", 40000);
        ReflectionTestUtils.setField(trade, "buyerShippingFee", 3000);
        ReflectionTestUtils.setField(trade, "buyerServiceFee", 0);
        ReflectionTestUtils.setField(trade, "sellerServiceFee", 2400);
        ReflectionTestUtils.setField(trade, "paymentAmount", 43000);
        ReflectionTestUtils.setField(trade, "settlementAmount", 40600);
        ReflectionTestUtils.setField(trade, "status", status);
        return trade;
    }

    private Trade tradeWithProduct(Long id, Long buyerId, Long sellerId, TradeStatus status, Long productId) {
        Trade trade = trade(id, buyerId, sellerId, status);
        ReflectionTestUtils.setField(trade, "product", product(productId));
        return trade;
    }

    private Item item(Long id) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }

    private Product product(Long id) {
        Product product = instantiate(Product.class);
        ReflectionTestUtils.setField(product, "id", id);
        return product;
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
