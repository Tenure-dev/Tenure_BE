package com.tenure.domain.trade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.dto.TradeListItemResponse;
import com.tenure.domain.trade.dto.TradeStatusChangeRequest;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.DeliveryCarrier;
import com.tenure.domain.trade.enums.TradeAction;
import com.tenure.domain.trade.enums.TradeRole;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.trade.event.TradeStatusChangedEvent;
import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    private static final Long CURRENT_USER_ID = 1L;

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private TradeService tradeService;

    @BeforeEach
    void setUp() {
        tradeService = new TradeService(tradeRepository, productRepository, eventPublisher);
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
        assertThat(response.deliveryReceiverName()).isEqualTo("Buyer");
        assertThat(response.deliveryPhone()).isEqualTo("010-1234-5678");
        assertThat(response.deliveryAddressLine1()).isEqualTo("Seoul Gangnam");
        assertThat(response.deliveryAddressLine2()).isEqualTo("101");
        assertThat(response.deliveryPostalCode()).isEqualTo("12345");
        assertThat(response.deliveryRequestNote()).isEqualTo("Leave at door");
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
        assertThat(response.deliveryReceiverName()).isEqualTo("Buyer");
        assertThat(response.deliveryAddressLine1()).isEqualTo("Seoul Gangnam");
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
    void getTradeDetail_shippedStatusAsSeller_returnsMarkDeliveredAction() {
        Trade trade = trade(1050L, 2L, CURRENT_USER_ID, TradeStatus.SHIPPED);
        when(tradeRepository.findById(1050L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(1050L, CURRENT_USER_ID);

        assertThat(response.availableActions()).containsExactly(TradeAction.MARK_DELIVERED);
    }

    @Test
    void getTradeDetail_withProduct_returnsProductId() {
        Trade trade = tradeWithProduct(106L, CURRENT_USER_ID, 2L, TradeStatus.PAID, 5L, ProductStatus.ON_SALE);
        when(tradeRepository.findById(106L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(106L, CURRENT_USER_ID);

        assertThat(response.productId()).isEqualTo(5L);
    }

    @Test
    void changeTradeStatus_paidAsSellerWithValidTracking_transitionsToShippedAndPublishesEvent() {
        Trade paidTrade = trade(200L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        Trade shippedTrade = trade(200L, 2L, CURRENT_USER_ID, TradeStatus.SHIPPED);
        when(tradeRepository.findById(200L)).thenReturn(Optional.of(paidTrade), Optional.of(shippedTrade));
        when(tradeRepository.updateToShipped(
                eq(200L), eq(TradeStatus.PAID), eq(TradeStatus.SHIPPED),
                eq(DeliveryCarrier.CJ_LOGISTICS), isNull(), eq("1234567890")
        )).thenReturn(1);

        TradeStatusChangeRequest request =
                new TradeStatusChangeRequest(TradeStatus.SHIPPED, DeliveryCarrier.CJ_LOGISTICS, "1234567890", null);

        TradeDetailResponse response = tradeService.changeTradeStatus(200L, CURRENT_USER_ID, request);

        assertThat(response.status()).isEqualTo(TradeStatus.SHIPPED);
        ArgumentCaptor<TradeStatusChangedEvent> captor = ArgumentCaptor.forClass(TradeStatusChangedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().from()).isEqualTo(TradeStatus.PAID);
        assertThat(captor.getValue().to()).isEqualTo(TradeStatus.SHIPPED);
    }

    @Test
    void changeTradeStatus_paidAsBuyer_throwsForbiddenTransition() {
        Trade trade = trade(201L, CURRENT_USER_ID, 2L, TradeStatus.PAID);
        when(tradeRepository.findById(201L)).thenReturn(Optional.of(trade));

        TradeStatusChangeRequest request =
                new TradeStatusChangeRequest(TradeStatus.SHIPPED, DeliveryCarrier.CJ_LOGISTICS, "1234567890", null);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(201L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_FORBIDDEN_TRANSITION);
    }

    @Test
    void changeTradeStatus_missingTracking_throwsTrackingRequired() {
        Trade trade = trade(202L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        when(tradeRepository.findById(202L)).thenReturn(Optional.of(trade));

        TradeStatusChangeRequest request = new TradeStatusChangeRequest(TradeStatus.SHIPPED, null, null, null);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(202L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_TRACKING_REQUIRED);
    }

    @Test
    void changeTradeStatus_otherCarrierWithoutCustomName_throwsTrackingRequired() {
        Trade trade = trade(2025L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        when(tradeRepository.findById(2025L)).thenReturn(Optional.of(trade));

        TradeStatusChangeRequest request =
                new TradeStatusChangeRequest(TradeStatus.SHIPPED, DeliveryCarrier.OTHER, "1234567890", null);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(2025L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_TRACKING_REQUIRED);
    }

    @ParameterizedTest
    @MethodSource("invalidTrackingNumbers")
    void changeTradeStatus_invalidTrackingFormat_throwsInvalidTracking(DeliveryCarrier carrier, String trackingNumber) {
        Trade trade = trade(203L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        when(tradeRepository.findById(203L)).thenReturn(Optional.of(trade));

        TradeStatusChangeRequest request = new TradeStatusChangeRequest(TradeStatus.SHIPPED, carrier, trackingNumber, null);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(203L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_INVALID_TRACKING);
    }

    static Stream<Arguments> invalidTrackingNumbers() {
        return Stream.of(
                Arguments.of(DeliveryCarrier.CJ_LOGISTICS, "123"),
                Arguments.of(DeliveryCarrier.CJ_LOGISTICS, "12345678901"),
                Arguments.of(DeliveryCarrier.KOREA_POST, "123456789012"),
                Arguments.of(DeliveryCarrier.GS_POSTBOX, "123456789"),
                Arguments.of(DeliveryCarrier.CU_POST, "1234567890123")
        );
    }

    @Test
    void changeTradeStatus_shippedAsSeller_transitionsToDeliveredAndRecordsDeliveredAt() {
        Trade shippedTrade = trade(204L, 2L, CURRENT_USER_ID, TradeStatus.SHIPPED);
        Trade deliveredTrade = trade(204L, 2L, CURRENT_USER_ID, TradeStatus.DELIVERED);
        when(tradeRepository.findById(204L)).thenReturn(Optional.of(shippedTrade), Optional.of(deliveredTrade));
        when(tradeRepository.updateToDelivered(eq(204L), eq(TradeStatus.SHIPPED), eq(TradeStatus.DELIVERED), any(LocalDateTime.class)))
                .thenReturn(1);

        TradeStatusChangeRequest request = TradeStatusChangeRequest.empty(TradeStatus.DELIVERED);
        TradeDetailResponse response = tradeService.changeTradeStatus(204L, CURRENT_USER_ID, request);

        assertThat(response.status()).isEqualTo(TradeStatus.DELIVERED);
        verify(tradeRepository).updateToDelivered(eq(204L), eq(TradeStatus.SHIPPED), eq(TradeStatus.DELIVERED), any(LocalDateTime.class));
    }

    @Test
    void changeTradeStatus_deliveredAsBuyer_chainsToCompletedAndMarksProductSold() {
        Trade deliveredTrade = tradeWithProduct(205L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED, 10L, ProductStatus.TRADING);
        Trade confirmedTrade = tradeWithProduct(205L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED, 10L, ProductStatus.TRADING);
        Trade completedTrade = tradeWithProduct(205L, CURRENT_USER_ID, 2L, TradeStatus.COMPLETED, 10L, ProductStatus.SOLD);
        when(tradeRepository.findById(205L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(completedTrade)
        );
        when(tradeRepository.updateStatus(205L, TradeStatus.DELIVERED, TradeStatus.PURCHASE_CONFIRMED)).thenReturn(1);
        when(tradeRepository.updateStatus(205L, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED)).thenReturn(1);
        when(tradeRepository.updateStatus(205L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);

        Product product = product(10L, ProductStatus.TRADING);
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));

        TradeStatusChangeRequest request = TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED);
        TradeDetailResponse response = tradeService.changeTradeStatus(205L, CURRENT_USER_ID, request);

        assertThat(response.status()).isEqualTo(TradeStatus.COMPLETED);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.SOLD);

        ArgumentCaptor<TradeStatusChangedEvent> captor = ArgumentCaptor.forClass(TradeStatusChangedEvent.class);
        verify(eventPublisher, times(2)).publishEvent(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(TradeStatusChangedEvent::to)
                .containsExactly(TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED);
    }

    @Test
    void changeTradeStatus_deliveredAsBuyerWithNullProduct_confirmsWithoutNpe() {
        Trade deliveredTrade = trade(206L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(206L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        Trade completedTrade = trade(206L, CURRENT_USER_ID, 2L, TradeStatus.COMPLETED);
        when(tradeRepository.findById(206L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(completedTrade)
        );
        when(tradeRepository.updateStatus(206L, TradeStatus.DELIVERED, TradeStatus.PURCHASE_CONFIRMED)).thenReturn(1);
        when(tradeRepository.updateStatus(206L, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED)).thenReturn(1);
        when(tradeRepository.updateStatus(206L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);

        TradeStatusChangeRequest request = TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED);
        TradeDetailResponse response = tradeService.changeTradeStatus(206L, CURRENT_USER_ID, request);

        assertThat(response.status()).isEqualTo(TradeStatus.COMPLETED);
        verify(productRepository, never()).findByIdForUpdate(anyLong());
    }

    @Test
    void changeTradeStatus_deliveredAsSeller_throwsForbiddenTransition() {
        Trade trade = trade(207L, 2L, CURRENT_USER_ID, TradeStatus.DELIVERED);
        when(tradeRepository.findById(207L)).thenReturn(Optional.of(trade));

        TradeStatusChangeRequest request = TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(207L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_FORBIDDEN_TRANSITION);
    }

    @ParameterizedTest
    @MethodSource("disallowedTransitions")
    void changeTradeStatus_disallowedTransition_throwsInvalidTransition(TradeStatus from, TradeStatus to) {
        Trade trade = trade(208L, 2L, CURRENT_USER_ID, from);
        when(tradeRepository.findById(208L)).thenReturn(Optional.of(trade));

        TradeStatusChangeRequest request = TradeStatusChangeRequest.empty(to);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(208L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_INVALID_TRANSITION);
    }

    static Stream<Arguments> disallowedTransitions() {
        return Stream.of(
                Arguments.of(TradeStatus.PAID, TradeStatus.DELIVERED),
                Arguments.of(TradeStatus.SHIPPED, TradeStatus.PAID),
                Arguments.of(TradeStatus.COMPLETED, TradeStatus.SHIPPED),
                Arguments.of(TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED),
                Arguments.of(TradeStatus.DELIVERED, TradeStatus.SETTLED)
        );
    }

    @Test
    void changeTradeStatus_thirdParty_throwsTradeNotFound() {
        Trade trade = trade(209L, 2L, 3L, TradeStatus.PAID);
        when(tradeRepository.findById(209L)).thenReturn(Optional.of(trade));

        TradeStatusChangeRequest request =
                new TradeStatusChangeRequest(TradeStatus.SHIPPED, DeliveryCarrier.CJ_LOGISTICS, "1234567890", null);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(209L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_NOT_FOUND);
    }

    @Test
    void changeTradeStatus_zeroRowsAffectedByConcurrentUpdate_throwsInvalidTransition() {
        Trade trade = trade(210L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        when(tradeRepository.findById(210L)).thenReturn(Optional.of(trade));
        when(tradeRepository.updateToShipped(
                eq(210L), eq(TradeStatus.PAID), eq(TradeStatus.SHIPPED),
                eq(DeliveryCarrier.CJ_LOGISTICS), isNull(), eq("1234567890")
        )).thenReturn(0);

        TradeStatusChangeRequest request =
                new TradeStatusChangeRequest(TradeStatus.SHIPPED, DeliveryCarrier.CJ_LOGISTICS, "1234567890", null);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(210L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_INVALID_TRANSITION);
    }

    @Test
    void confirmPurchaseBySystem_deliveredTrade_confirmsAndChains() {
        Trade deliveredTrade = trade(300L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(300L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        Trade completedTrade = trade(300L, CURRENT_USER_ID, 2L, TradeStatus.COMPLETED);
        when(tradeRepository.findById(300L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(completedTrade)
        );
        when(tradeRepository.updateStatus(300L, TradeStatus.DELIVERED, TradeStatus.PURCHASE_CONFIRMED)).thenReturn(1);
        when(tradeRepository.updateStatus(300L, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED)).thenReturn(1);
        when(tradeRepository.updateStatus(300L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);

        boolean confirmed = tradeService.confirmPurchaseBySystem(300L);

        assertThat(confirmed).isTrue();
    }

    @Test
    void changeTradeStatus_marksProductSoldBeforeTheUpdatesThatClearPersistenceContext() {
        Trade deliveredTrade = tradeWithProduct(211L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED, 11L, ProductStatus.TRADING);
        Trade confirmedTrade = tradeWithProduct(211L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED, 11L, ProductStatus.TRADING);
        Trade completedTrade = tradeWithProduct(211L, CURRENT_USER_ID, 2L, TradeStatus.COMPLETED, 11L, ProductStatus.SOLD);
        when(tradeRepository.findById(211L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(completedTrade)
        );
        when(tradeRepository.updateStatus(211L, TradeStatus.DELIVERED, TradeStatus.PURCHASE_CONFIRMED)).thenReturn(1);
        when(tradeRepository.updateStatus(211L, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED)).thenReturn(1);
        when(tradeRepository.updateStatus(211L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        when(productRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(product(11L, ProductStatus.TRADING)));

        tradeService.changeTradeStatus(211L, CURRENT_USER_ID, TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED));

        // Product 변경이 clearAutomatically를 유발하는 벌크 UPDATE보다 뒤로 밀리면 dirty checking이 유실되므로 순서를 고정한다.
        InOrder inOrder = inOrder(productRepository, tradeRepository);
        inOrder.verify(productRepository).findByIdForUpdate(11L);
        inOrder.verify(tradeRepository).updateStatus(211L, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED);
    }

    @Test
    void confirmPurchaseBySystem_chainUpdateAffectsZeroRows_throwsInsteadOfSilentlySkipping() {
        Trade deliveredTrade = trade(302L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(302L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        when(tradeRepository.findById(302L)).thenReturn(Optional.of(deliveredTrade), Optional.of(confirmedTrade));
        when(tradeRepository.updateStatus(302L, TradeStatus.DELIVERED, TradeStatus.PURCHASE_CONFIRMED)).thenReturn(1);
        when(tradeRepository.updateStatus(302L, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED)).thenReturn(0);

        // 진입 전이의 0행은 정상 경합이지만, 연쇄 구간의 0행은 불변식 위반이므로 삼키지 않고 롤백되어야 한다.
        assertThatThrownBy(() -> tradeService.confirmPurchaseBySystem(302L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void confirmPurchaseBySystem_zeroRowsAffected_returnsFalseWithoutThrowing() {
        Trade deliveredTrade = trade(301L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        when(tradeRepository.findById(301L)).thenReturn(Optional.of(deliveredTrade));
        when(tradeRepository.updateStatus(301L, TradeStatus.DELIVERED, TradeStatus.PURCHASE_CONFIRMED)).thenReturn(0);

        boolean confirmed = tradeService.confirmPurchaseBySystem(301L);

        assertThat(confirmed).isFalse();
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
        ReflectionTestUtils.setField(trade, "deliveryReceiverName", "Buyer");
        ReflectionTestUtils.setField(trade, "deliveryPhone", "010-1234-5678");
        ReflectionTestUtils.setField(trade, "deliveryAddressLine1", "Seoul Gangnam");
        ReflectionTestUtils.setField(trade, "deliveryAddressLine2", "101");
        ReflectionTestUtils.setField(trade, "deliveryPostalCode", "12345");
        ReflectionTestUtils.setField(trade, "deliveryRequestNote", "Leave at door");
        return trade;
    }

    private Trade tradeWithProduct(
            Long id, Long buyerId, Long sellerId, TradeStatus status, Long productId, ProductStatus productStatus
    ) {
        Trade trade = trade(id, buyerId, sellerId, status);
        ReflectionTestUtils.setField(trade, "product", product(productId, productStatus));
        return trade;
    }

    private Item item(Long id) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }

    private Product product(Long id, ProductStatus status) {
        Product product = instantiate(Product.class);
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "productStatus", status);
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
