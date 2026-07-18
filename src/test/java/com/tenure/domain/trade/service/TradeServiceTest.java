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
import com.tenure.domain.item.entity.ItemHistory;
import com.tenure.domain.item.enums.AcquisitionType;
import com.tenure.domain.item.enums.EndReason;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemHistoryRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.dto.ItemSummaryResponse;
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
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PurchaseOfferRepository purchaseOfferRepository;

    @Mock
    private ItemHistoryRepository itemHistoryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private TradeService tradeService;

    @BeforeEach
    void setUp() {
        tradeService = new TradeService(
                tradeRepository, productRepository, itemRepository, userRepository,
                purchaseOfferRepository, itemHistoryRepository, eventPublisher
        );
    }

    // COMPLETED -> TRANSFERRED 자동 연쇄(소유권 이전) 부수효과가 항상 거치는 스텁 묶음.
    // Item.owner는 seller(2L)로 시작해 buyer(CURRENT_USER_ID)로 이전된다고 가정하고,
    // seller 명의의 열린 이력 행이 이미 있다고 가정한다(정상 경로라면 createItem이나 이전 거래가 만들어둔 행).
    private void stubOwnershipTransfer(Long tradeId, Long itemId, Long buyerId, Long sellerId) {
        when(tradeRepository.updateStatus(tradeId, TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)).thenReturn(1);
        Item item = item(itemId);
        User seller = user(sellerId);
        ReflectionTestUtils.setField(item, "owner", seller);
        when(itemRepository.findByIdForUpdate(itemId)).thenReturn(Optional.of(item));
        when(userRepository.getReferenceById(buyerId)).thenReturn(user(buyerId));
        when(tradeRepository.getReferenceById(tradeId)).thenReturn(trade(tradeId, buyerId, sellerId, TradeStatus.TRANSFERRED));
        when(purchaseOfferRepository.findSentByItemIdForUpdate(itemId, PurchaseOfferStatus.SENT)).thenReturn(List.of());
        ItemHistory openHistory = ItemHistory.ofFirstRegistration(item, seller, LocalDateTime.now().minusDays(1));
        when(itemHistoryRepository.findByItemIdAndEndedAtIsNull(itemId)).thenReturn(Optional.of(openHistory));
    }

    @Test
    void getTradeList_withBuyerRole_queriesByBuyerWithCurrentUserId() {
        Trade trade = trade(100L, CURRENT_USER_ID, 2L, TradeStatus.PAID);
        Page<Trade> page = new PageImpl<>(List.of(trade));
        when(tradeRepository.findAllByBuyer(eq(CURRENT_USER_ID), eq(List.of(TradeStatus.PAID)), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, TradeRole.BUYER, List.of(TradeStatus.PAID), 0, 20);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).tradeId()).isEqualTo(100L);
        verify(tradeRepository).findAllByBuyer(eq(CURRENT_USER_ID), eq(List.of(TradeStatus.PAID)), any(Pageable.class));
    }

    @Test
    void getTradeList_withSellerRole_queriesBySellerWithCurrentUserId() {
        Trade trade = trade(101L, 2L, CURRENT_USER_ID, TradeStatus.SHIPPED);
        Page<Trade> page = new PageImpl<>(List.of(trade));
        when(tradeRepository.findAllBySeller(eq(CURRENT_USER_ID), eq(Arrays.asList(TradeStatus.values())), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, TradeRole.SELLER, null, 0, 20);

        assertThat(response.getContent()).hasSize(1);
        verify(tradeRepository).findAllBySeller(eq(CURRENT_USER_ID), eq(Arrays.asList(TradeStatus.values())), any(Pageable.class));
    }

    @Test
    void getTradeList_withoutRole_queriesByParticipantWithCurrentUserId() {
        Trade trade = trade(102L, CURRENT_USER_ID, 3L, TradeStatus.DELIVERED);
        Page<Trade> page = new PageImpl<>(List.of(trade));
        when(tradeRepository.findAllByParticipant(eq(CURRENT_USER_ID), eq(Arrays.asList(TradeStatus.values())), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, null, null, 0, 20);

        assertThat(response.getContent()).hasSize(1);
        verify(tradeRepository).findAllByParticipant(eq(CURRENT_USER_ID), eq(Arrays.asList(TradeStatus.values())), any(Pageable.class));
    }

    @Test
    void getTradeList_passesStatusFilterToRepository() {
        Page<Trade> page = new PageImpl<>(List.of());
        when(tradeRepository.findAllByParticipant(eq(CURRENT_USER_ID), eq(List.of(TradeStatus.SETTLED)), any(Pageable.class)))
                .thenReturn(page);

        tradeService.getTradeList(CURRENT_USER_ID, null, List.of(TradeStatus.SETTLED), 0, 20);

        verify(tradeRepository).findAllByParticipant(eq(CURRENT_USER_ID), eq(List.of(TradeStatus.SETTLED)), any(Pageable.class));
    }

    @Test
    void getTradeList_withCompletedFilter_expandsQueryToIncludeTransferred() {
        // 커밋된 거래는 실제로 TRANSFERRED까지 자동 전이되므로, COMPLETED 필터는 TRANSFERRED 행도 함께 조회해야 한다.
        Page<Trade> page = new PageImpl<>(List.of());
        when(tradeRepository.findAllByParticipant(
                eq(CURRENT_USER_ID), eq(List.of(TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)), any(Pageable.class)
        )).thenReturn(page);

        tradeService.getTradeList(CURRENT_USER_ID, null, List.of(TradeStatus.COMPLETED), 0, 20);

        verify(tradeRepository).findAllByParticipant(
                eq(CURRENT_USER_ID), eq(List.of(TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)), any(Pageable.class)
        );
    }

    @Test
    void getTradeList_withTransferredFilter_throwsStatusFilterNotAllowed() {
        // TRANSFERRED는 API 계약상 노출되지 않는 내부 상태이므로 필터로 직접 조회할 수 없다.
        assertThatThrownBy(() -> tradeService.getTradeList(CURRENT_USER_ID, null, List.of(TradeStatus.TRANSFERRED), 0, 20))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(TradeErrorCode.TRADE_STATUS_FILTER_NOT_ALLOWED);

        verify(tradeRepository, never()).findAllByParticipant(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void getTradeList_returnsCompletedStatusForTransferredTrade() {
        // 목록 응답도 상세 응답과 마찬가지로 TRANSFERRED 엔티티를 COMPLETED로 노출해야 한다.
        Trade transferredTrade = trade(120L, CURRENT_USER_ID, 2L, TradeStatus.TRANSFERRED);
        Page<Trade> page = new PageImpl<>(List.of(transferredTrade));
        when(tradeRepository.findAllByParticipant(
                eq(CURRENT_USER_ID), eq(List.of(TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)), any(Pageable.class)
        )).thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, null, List.of(TradeStatus.COMPLETED), 0, 20);

        assertThat(response.getContent()).extracting(TradeListItemResponse::status).containsExactly(TradeStatus.COMPLETED);
        assertThat(transferredTrade.getStatus()).isEqualTo(TradeStatus.TRANSFERRED);
    }

    @Test
    void getTradeList_withMultipleStatuses_returnsOnlyMatchingStatuses() {
        Trade paidTrade = trade(110L, CURRENT_USER_ID, 2L, TradeStatus.PAID);
        Trade shippedTrade = trade(111L, CURRENT_USER_ID, 2L, TradeStatus.SHIPPED);
        Page<Trade> page = new PageImpl<>(List.of(paidTrade, shippedTrade));
        List<TradeStatus> requestedStatuses = List.of(TradeStatus.PAID, TradeStatus.SHIPPED);
        when(tradeRepository.findAllByParticipant(eq(CURRENT_USER_ID), eq(requestedStatuses), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, null, requestedStatuses, 0, 20);

        assertThat(response.getContent())
                .extracting(TradeListItemResponse::status)
                .containsExactly(TradeStatus.PAID, TradeStatus.SHIPPED);
        verify(tradeRepository).findAllByParticipant(eq(CURRENT_USER_ID), eq(requestedStatuses), any(Pageable.class));
    }

    @Test
    void getTradeList_withoutStatus_queriesAllStatuses() {
        Page<Trade> page = new PageImpl<>(List.of());
        when(tradeRepository.findAllByParticipant(eq(CURRENT_USER_ID), eq(Arrays.asList(TradeStatus.values())), any(Pageable.class)))
                .thenReturn(page);

        tradeService.getTradeList(CURRENT_USER_ID, null, null, 0, 20);

        verify(tradeRepository).findAllByParticipant(eq(CURRENT_USER_ID), eq(Arrays.asList(TradeStatus.values())), any(Pageable.class));
    }

    @Test
    void getTradeList_mapsItemSummaryFields() {
        Trade trade = trade(104L, CURRENT_USER_ID, 2L, TradeStatus.PAID);
        Page<Trade> page = new PageImpl<>(List.of(trade));
        when(tradeRepository.findAllByBuyer(eq(CURRENT_USER_ID), eq(List.of(TradeStatus.PAID)), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(CURRENT_USER_ID, TradeRole.BUYER, List.of(TradeStatus.PAID), 0, 20);

        ItemSummaryResponse item = response.getContent().get(0).item();
        assertThat(item.itemId()).isEqualTo(10L);
        assertThat(item.itemName()).isEqualTo("Gray Hoodie");
        assertThat(item.brandName()).isEqualTo("Nike");
        assertThat(item.representativeImageUrl()).isEqualTo("https://image.url/item.jpg");
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
    void getTradeDetail_mapsTransitionTimestamps() {
        Trade trade = trade(107L, CURRENT_USER_ID, 2L, TradeStatus.SETTLED);
        LocalDateTime shippedAt = LocalDateTime.of(2026, 7, 11, 9, 0);
        LocalDateTime deliveredAt = LocalDateTime.of(2026, 7, 12, 15, 0);
        LocalDateTime confirmedAt = LocalDateTime.of(2026, 7, 15, 10, 0);
        LocalDateTime settledAt = LocalDateTime.of(2026, 7, 15, 10, 0, 1);
        ReflectionTestUtils.setField(trade, "shippedAt", shippedAt);
        ReflectionTestUtils.setField(trade, "deliveredAt", deliveredAt);
        ReflectionTestUtils.setField(trade, "confirmedAt", confirmedAt);
        ReflectionTestUtils.setField(trade, "settledAt", settledAt);
        when(tradeRepository.findById(107L)).thenReturn(Optional.of(trade));

        TradeDetailResponse response = tradeService.getTradeDetail(107L, CURRENT_USER_ID);

        assertThat(response.shippedAt()).isEqualTo(shippedAt);
        assertThat(response.deliveredAt()).isEqualTo(deliveredAt);
        assertThat(response.confirmedAt()).isEqualTo(confirmedAt);
        assertThat(response.settledAt()).isEqualTo(settledAt);
    }

    @Test
    void getTradeDetail_transferredTrade_displaysAsCompletedWhileEntityStatusStaysTransferred() {
        // TRANSFERRED는 API 계약상 COMPLETED로 노출되는 내부 상태다. 매핑은 응답 조립 시점에만 적용되고
        // 엔티티에 저장된 실제 상태(trade.getStatus())는 그대로 TRANSFERRED로 남아야 한다.
        Trade transferredTrade = trade(108L, CURRENT_USER_ID, 2L, TradeStatus.TRANSFERRED);
        when(tradeRepository.findById(108L)).thenReturn(Optional.of(transferredTrade));

        TradeDetailResponse response = tradeService.getTradeDetail(108L, CURRENT_USER_ID);

        assertThat(response.status()).isEqualTo(TradeStatus.COMPLETED);
        assertThat(transferredTrade.getStatus()).isEqualTo(TradeStatus.TRANSFERRED);
    }

    @Test
    void changeTradeStatus_paidAsSellerWithValidTracking_transitionsToShippedAndPublishesEvent() {
        Trade paidTrade = trade(200L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        Trade shippedTrade = trade(200L, 2L, CURRENT_USER_ID, TradeStatus.SHIPPED);
        when(tradeRepository.findById(200L)).thenReturn(Optional.of(paidTrade), Optional.of(shippedTrade));
        when(tradeRepository.updateToShipped(
                eq(200L), eq(TradeStatus.PAID), eq(TradeStatus.SHIPPED),
                eq(DeliveryCarrier.CJ_LOGISTICS), isNull(), eq("1234567890"), any(LocalDateTime.class)
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
    void changeTradeStatus_paidAsSeller_recordsShippedAtAndLeavesOtherTimestampsUntouched() {
        Trade paidTrade = trade(220L, 2L, CURRENT_USER_ID, TradeStatus.PAID);
        Trade shippedTrade = trade(220L, 2L, CURRENT_USER_ID, TradeStatus.SHIPPED);
        when(tradeRepository.findById(220L)).thenReturn(Optional.of(paidTrade), Optional.of(shippedTrade));
        when(tradeRepository.updateToShipped(
                eq(220L), eq(TradeStatus.PAID), eq(TradeStatus.SHIPPED),
                eq(DeliveryCarrier.CJ_LOGISTICS), isNull(), eq("1234567890"), any(LocalDateTime.class)
        )).thenReturn(1);

        TradeStatusChangeRequest request =
                new TradeStatusChangeRequest(TradeStatus.SHIPPED, DeliveryCarrier.CJ_LOGISTICS, "1234567890", null);

        tradeService.changeTradeStatus(220L, CURRENT_USER_ID, request);

        verify(tradeRepository).updateToShipped(
                eq(220L), eq(TradeStatus.PAID), eq(TradeStatus.SHIPPED),
                eq(DeliveryCarrier.CJ_LOGISTICS), isNull(), eq("1234567890"), any(LocalDateTime.class)
        );
        verify(tradeRepository, never()).updateToDelivered(anyLong(), any(), any(), any());
        verify(tradeRepository, never()).updateToConfirmed(anyLong(), any(), any(), any());
        verify(tradeRepository, never()).updateToSettled(anyLong(), any(), any(), any());
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
    void changeTradeStatus_deliveredAsBuyer_chainsToTransferredAndMarksProductSold() {
        Trade deliveredTrade = tradeWithProduct(205L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED, 10L, ProductStatus.TRADING);
        Trade confirmedTrade = tradeWithProduct(205L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED, 10L, ProductStatus.TRADING);
        Trade transferredTrade = tradeWithProduct(205L, CURRENT_USER_ID, 2L, TradeStatus.TRANSFERRED, 10L, ProductStatus.SOLD);
        when(tradeRepository.findById(205L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(transferredTrade)
        );
        when(tradeRepository.updateToConfirmed(eq(205L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(205L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(205L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        stubOwnershipTransfer(205L, 10L, CURRENT_USER_ID, 2L);

        Product product = product(10L, ProductStatus.TRADING);
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));

        TradeStatusChangeRequest request = TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED);
        TradeDetailResponse response = tradeService.changeTradeStatus(205L, CURRENT_USER_ID, request);

        // 엔티티 상태는 TRANSFERRED까지 연쇄됐지만(stubOwnershipTransfer가 이를 검증), API 응답은 COMPLETED로 노출된다.
        assertThat(response.status()).isEqualTo(TradeStatus.COMPLETED);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.SOLD);

        ArgumentCaptor<TradeStatusChangedEvent> captor = ArgumentCaptor.forClass(TradeStatusChangedEvent.class);
        verify(eventPublisher, times(2)).publishEvent(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(TradeStatusChangedEvent::to)
                .containsExactly(TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED);
    }

    @Test
    void changeTradeStatus_deliveredAsBuyer_transfersItemOwnershipAndSavesHistory() {
        // 소유권 이전 부수효과: Item owner가 buyer로 바뀌고, 상태는 OWNED로 되돌아가며(재판매 가능),
        // seller의 열린 이력 행은 TENURE_TRADE로 마감되고, buyer의 새 열린 행이 저장된다.
        Long itemId = 10L;
        Trade deliveredTrade = trade(220L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(220L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        Trade transferredTrade = trade(220L, CURRENT_USER_ID, 2L, TradeStatus.TRANSFERRED);
        when(tradeRepository.findById(220L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(transferredTrade)
        );
        when(tradeRepository.updateToConfirmed(eq(220L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(220L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(220L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        when(tradeRepository.updateStatus(220L, TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)).thenReturn(1);

        Item item = item(itemId);
        User seller = user(2L);
        User buyer = user(CURRENT_USER_ID);
        ReflectionTestUtils.setField(item, "owner", seller);
        when(itemRepository.findByIdForUpdate(itemId)).thenReturn(Optional.of(item));
        when(userRepository.getReferenceById(CURRENT_USER_ID)).thenReturn(buyer);
        when(tradeRepository.getReferenceById(220L)).thenReturn(transferredTrade);
        when(purchaseOfferRepository.findSentByItemIdForUpdate(itemId, PurchaseOfferStatus.SENT)).thenReturn(List.of());
        ItemHistory openHistory = ItemHistory.ofFirstRegistration(item, seller, LocalDateTime.now().minusDays(10));
        when(itemHistoryRepository.findByItemIdAndEndedAtIsNull(itemId)).thenReturn(Optional.of(openHistory));

        tradeService.changeTradeStatus(220L, CURRENT_USER_ID, TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED));

        assertThat(item.getOwner()).isEqualTo(buyer);
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.OWNED);

        assertThat(openHistory.getEndReason()).isEqualTo(EndReason.TENURE_TRADE);
        assertThat(openHistory.getEndedAt()).isNotNull();

        // close()의 지연 UPDATE가 save()의 즉시 INSERT보다 먼저 flush되는지 확인한다.
        // (부분 유니크 인덱스 위반을 막기 위한 순서 보장 회귀 테스트)
        InOrder historyOrder = inOrder(itemHistoryRepository);
        historyOrder.verify(itemHistoryRepository).findByItemIdAndEndedAtIsNull(itemId);
        historyOrder.verify(itemHistoryRepository).flush();
        historyOrder.verify(itemHistoryRepository).save(any(ItemHistory.class));

        ArgumentCaptor<ItemHistory> historyCaptor = ArgumentCaptor.forClass(ItemHistory.class);
        verify(itemHistoryRepository).save(historyCaptor.capture());
        ItemHistory savedHistory = historyCaptor.getValue();
        assertThat(savedHistory.getItem()).isEqualTo(item);
        assertThat(savedHistory.getOwner()).isEqualTo(buyer);
        assertThat(savedHistory.getAcquisitionType()).isEqualTo(AcquisitionType.TENURE_TRADE);
        assertThat(savedHistory.getTrade()).isEqualTo(transferredTrade);
        assertThat(savedHistory.getStartedAt()).isEqualTo(openHistory.getEndedAt());
        assertThat(savedHistory.getEndReason()).isNull();
        assertThat(savedHistory.getEndedAt()).isNull();
    }

    @Test
    void changeTradeStatus_deliveredAsBuyer_noOpenHistoryRow_throwsIllegalStateException() {
        // createItem이 정상적으로 FIRST_REGISTERED 행을 남겼다면 열린 행이 없는 상황은 발생하지 않는다.
        // 발생했다면 정합성 위반이므로 예외로 롤백시킨다.
        Long itemId = 10L;
        Trade deliveredTrade = trade(223L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(223L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        when(tradeRepository.findById(223L)).thenReturn(Optional.of(deliveredTrade), Optional.of(confirmedTrade));
        when(tradeRepository.updateToConfirmed(eq(223L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(223L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(223L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        when(tradeRepository.updateStatus(223L, TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)).thenReturn(1);

        Item item = item(itemId);
        ReflectionTestUtils.setField(item, "owner", user(2L));
        when(itemRepository.findByIdForUpdate(itemId)).thenReturn(Optional.of(item));
        when(userRepository.getReferenceById(CURRENT_USER_ID)).thenReturn(user(CURRENT_USER_ID));
        when(tradeRepository.getReferenceById(223L)).thenReturn(trade(223L, CURRENT_USER_ID, 2L, TradeStatus.TRANSFERRED));
        when(itemHistoryRepository.findByItemIdAndEndedAtIsNull(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tradeService.changeTradeStatus(
                223L, CURRENT_USER_ID, TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED)
        )).isInstanceOf(IllegalStateException.class);

        verify(itemHistoryRepository, never()).save(any(ItemHistory.class));
    }

    @Test
    void changeTradeStatus_deliveredAsBuyer_cancelsRemainingSentOffersForItem() {
        Long itemId = 10L;
        Trade deliveredTrade = trade(221L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(221L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        Trade transferredTrade = trade(221L, CURRENT_USER_ID, 2L, TradeStatus.TRANSFERRED);
        when(tradeRepository.findById(221L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(transferredTrade)
        );
        when(tradeRepository.updateToConfirmed(eq(221L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(221L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(221L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        when(tradeRepository.updateStatus(221L, TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)).thenReturn(1);

        Item item = item(itemId);
        User seller = user(2L);
        ReflectionTestUtils.setField(item, "owner", seller);
        when(itemRepository.findByIdForUpdate(itemId)).thenReturn(Optional.of(item));
        when(userRepository.getReferenceById(CURRENT_USER_ID)).thenReturn(user(CURRENT_USER_ID));
        when(tradeRepository.getReferenceById(221L)).thenReturn(transferredTrade);
        when(itemHistoryRepository.findByItemIdAndEndedAtIsNull(itemId)).thenReturn(
                Optional.of(ItemHistory.ofFirstRegistration(item, seller, LocalDateTime.now().minusDays(1)))
        );

        PurchaseOffer sentOffer1 = purchaseOffer(501L, PurchaseOfferStatus.SENT, PaymentAuthorizationStatus.AUTHORIZED);
        PurchaseOffer sentOffer2 = purchaseOffer(502L, PurchaseOfferStatus.SENT, PaymentAuthorizationStatus.AUTHORIZED);
        when(purchaseOfferRepository.findSentByItemIdForUpdate(itemId, PurchaseOfferStatus.SENT))
                .thenReturn(List.of(sentOffer1, sentOffer2));

        tradeService.changeTradeStatus(221L, CURRENT_USER_ID, TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED));

        assertThat(sentOffer1.getStatus()).isEqualTo(PurchaseOfferStatus.CANCELED);
        assertThat(sentOffer1.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(sentOffer2.getStatus()).isEqualTo(PurchaseOfferStatus.CANCELED);
        assertThat(sentOffer2.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    @Test
    void changeTradeStatus_transferredChainAffectsZeroRows_throwsInsteadOfSilentlySkipping() {
        // COMPLETED -> TRANSFERRED는 직전 단계가 같은 트랜잭션에서 만든 상태를 대상으로 하므로,
        // 영향 행 0건은 외부 경합이 아니라 불변식 위반이다 (기존 chain() 동작과 동일).
        Trade deliveredTrade = trade(222L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(222L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        when(tradeRepository.findById(222L)).thenReturn(Optional.of(deliveredTrade), Optional.of(confirmedTrade));
        when(tradeRepository.updateToConfirmed(eq(222L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(222L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(222L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        when(tradeRepository.updateStatus(222L, TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)).thenReturn(0);

        assertThatThrownBy(() -> tradeService.changeTradeStatus(222L, CURRENT_USER_ID, TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED)))
                .isInstanceOf(IllegalStateException.class);

        verify(itemRepository, never()).findByIdForUpdate(anyLong());
    }

    @Test
    void changeTradeStatus_deliveredAsBuyer_recordsConfirmedAtAndChainedSettledAt() {
        Trade deliveredTrade = trade(212L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(212L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        Trade completedTrade = trade(212L, CURRENT_USER_ID, 2L, TradeStatus.COMPLETED);
        when(tradeRepository.findById(212L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(completedTrade)
        );
        when(tradeRepository.updateToConfirmed(eq(212L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(212L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(212L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        stubOwnershipTransfer(212L, 10L, CURRENT_USER_ID, 2L);

        tradeService.changeTradeStatus(212L, CURRENT_USER_ID, TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED));

        verify(tradeRepository).updateToConfirmed(eq(212L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class));
        verify(tradeRepository).updateToSettled(eq(212L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class));
        verify(tradeRepository, never()).updateStatus(212L, TradeStatus.DELIVERED, TradeStatus.PURCHASE_CONFIRMED);
        verify(tradeRepository, never()).updateStatus(212L, TradeStatus.PURCHASE_CONFIRMED, TradeStatus.SETTLED);
    }

    @Test
    void changeTradeStatus_deliveredAsBuyerWithNullProduct_confirmsWithoutNpe() {
        Trade deliveredTrade = trade(206L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(206L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        Trade transferredTrade = trade(206L, CURRENT_USER_ID, 2L, TradeStatus.TRANSFERRED);
        when(tradeRepository.findById(206L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(transferredTrade)
        );
        when(tradeRepository.updateToConfirmed(eq(206L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(206L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(206L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        stubOwnershipTransfer(206L, 10L, CURRENT_USER_ID, 2L);

        TradeStatusChangeRequest request = TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED);
        TradeDetailResponse response = tradeService.changeTradeStatus(206L, CURRENT_USER_ID, request);

        // 엔티티 상태는 TRANSFERRED까지 연쇄됐지만(stubOwnershipTransfer가 이를 검증), API 응답은 COMPLETED로 노출된다.
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
                Arguments.of(TradeStatus.DELIVERED, TradeStatus.SETTLED),
                // TRANSFERRED는 SETTLED/COMPLETED와 마찬가지로 자동 연쇄 전용 상태이며 외부에서 직접 요청할 수 없다.
                Arguments.of(TradeStatus.COMPLETED, TradeStatus.TRANSFERRED)
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
                eq(DeliveryCarrier.CJ_LOGISTICS), isNull(), eq("1234567890"), any(LocalDateTime.class)
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
        // 스케줄러 경로(SYSTEM 액터)도 changeTradeStatus와 동일한 applyTransition을 타므로
        // COMPLETED -> TRANSFERRED 연쇄와 소유권 이전이 동일하게 일어나야 한다.
        Trade deliveredTrade = trade(300L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(300L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        Trade transferredTrade = trade(300L, CURRENT_USER_ID, 2L, TradeStatus.TRANSFERRED);
        when(tradeRepository.findById(300L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(transferredTrade)
        );
        when(tradeRepository.updateToConfirmed(eq(300L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(300L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(300L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        stubOwnershipTransfer(300L, 10L, CURRENT_USER_ID, 2L);

        boolean confirmed = tradeService.confirmPurchaseBySystem(300L);

        assertThat(confirmed).isTrue();
        verify(tradeRepository).updateToConfirmed(eq(300L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class));
        verify(tradeRepository).updateToSettled(eq(300L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class));
        verify(tradeRepository).updateStatus(300L, TradeStatus.COMPLETED, TradeStatus.TRANSFERRED);
        verify(itemHistoryRepository).save(any(ItemHistory.class));
    }

    @Test
    void changeTradeStatus_marksProductSoldBeforeTheUpdatesThatClearPersistenceContext() {
        Trade deliveredTrade = tradeWithProduct(211L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED, 11L, ProductStatus.TRADING);
        Trade confirmedTrade = tradeWithProduct(211L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED, 11L, ProductStatus.TRADING);
        Trade completedTrade = tradeWithProduct(211L, CURRENT_USER_ID, 2L, TradeStatus.COMPLETED, 11L, ProductStatus.SOLD);
        when(tradeRepository.findById(211L)).thenReturn(
                Optional.of(deliveredTrade), Optional.of(confirmedTrade), Optional.of(completedTrade)
        );
        when(tradeRepository.updateToConfirmed(eq(211L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(211L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateStatus(211L, TradeStatus.SETTLED, TradeStatus.COMPLETED)).thenReturn(1);
        when(productRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(product(11L, ProductStatus.TRADING)));
        stubOwnershipTransfer(211L, 10L, CURRENT_USER_ID, 2L);

        tradeService.changeTradeStatus(211L, CURRENT_USER_ID, TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED));

        // Product 변경이 clearAutomatically를 유발하는 벌크 UPDATE보다 뒤로 밀리면 dirty checking이 유실되므로 순서를 고정한다.
        InOrder inOrder = inOrder(productRepository, tradeRepository);
        inOrder.verify(productRepository).findByIdForUpdate(11L);
        inOrder.verify(tradeRepository).updateToSettled(eq(211L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class));
    }

    @Test
    void confirmPurchaseBySystem_chainUpdateAffectsZeroRows_throwsInsteadOfSilentlySkipping() {
        Trade deliveredTrade = trade(302L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        Trade confirmedTrade = trade(302L, CURRENT_USER_ID, 2L, TradeStatus.PURCHASE_CONFIRMED);
        when(tradeRepository.findById(302L)).thenReturn(Optional.of(deliveredTrade), Optional.of(confirmedTrade));
        when(tradeRepository.updateToConfirmed(eq(302L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(1);
        when(tradeRepository.updateToSettled(eq(302L), eq(TradeStatus.PURCHASE_CONFIRMED), eq(TradeStatus.SETTLED), any(LocalDateTime.class))).thenReturn(0);

        // 진입 전이의 0행은 정상 경합이지만, 연쇄 구간의 0행은 불변식 위반이므로 삼키지 않고 롤백되어야 한다.
        assertThatThrownBy(() -> tradeService.confirmPurchaseBySystem(302L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void confirmPurchaseBySystem_zeroRowsAffected_returnsFalseWithoutThrowing() {
        Trade deliveredTrade = trade(301L, CURRENT_USER_ID, 2L, TradeStatus.DELIVERED);
        when(tradeRepository.findById(301L)).thenReturn(Optional.of(deliveredTrade));
        when(tradeRepository.updateToConfirmed(eq(301L), eq(TradeStatus.DELIVERED), eq(TradeStatus.PURCHASE_CONFIRMED), any(LocalDateTime.class))).thenReturn(0);

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
        ReflectionTestUtils.setField(item, "itemName", "Gray Hoodie");
        ReflectionTestUtils.setField(item, "brandName", "Nike");
        ReflectionTestUtils.setField(item, "representativeImageUrl", "https://image.url/item.jpg");
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

    private PurchaseOffer purchaseOffer(Long id, PurchaseOfferStatus status, PaymentAuthorizationStatus authStatus) {
        PurchaseOffer offer = instantiate(PurchaseOffer.class);
        ReflectionTestUtils.setField(offer, "id", id);
        ReflectionTestUtils.setField(offer, "status", status);
        ReflectionTestUtils.setField(offer, "paymentAuthorizationStatus", authStatus);
        return offer;
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
