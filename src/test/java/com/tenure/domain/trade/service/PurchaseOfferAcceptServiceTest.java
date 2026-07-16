package com.tenure.domain.trade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.purchase.service.PurchaseOfferExpirationService;
import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PurchaseOfferAcceptServiceTest {

    private static final Long ITEM_ID = 10L;
    private static final Long PROPOSER_ID = 2L;
    private static final Long OWNER_ID = 1L;
    private static final Long ADDRESS_ID = 100L;
    private static final Long OFFER_ID = 123L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseOfferRepository purchaseOfferRepository;

    @Mock
    private TradeRepository tradeRepository;

    private PurchaseOfferAcceptService purchaseOfferAcceptService;

    private void setUpService() {
        purchaseOfferAcceptService = new PurchaseOfferAcceptService(
                itemRepository,
                purchaseOfferRepository,
                tradeRepository,
                new PurchaseOfferExpirationService()
        );
    }

    @Test
    void acceptPurchaseOffer_createsTradeAndCancelsCompetingOffers() {
        setUpService();
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = existingOffer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));
        PurchaseOffer competingOffer = existingOffer(456L, item, user(3L), owner, LocalDateTime.now().plusHours(1));

        givenOfferDetail(item, offer);
        when(tradeRepository.existsByItemIdAndStatusNotIn(eq(ITEM_ID), any())).thenReturn(false);
        when(purchaseOfferRepository.findSentByItemIdForUpdate(ITEM_ID, PurchaseOfferStatus.SENT))
                .thenReturn(List.of(offer, competingOffer));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            ReflectionTestUtils.setField(trade, "id", 900L);
            return trade;
        });

        TradeDetailResponse response = purchaseOfferAcceptService.acceptPurchaseOffer(OFFER_ID, OWNER_ID);

        assertThat(response.tradeId()).isEqualTo(900L);
        assertThat(response.status()).isEqualTo(TradeStatus.PAID);
        assertThat(response.viewerMode()).isEqualTo(TradeViewerMode.SELLER);
        assertThat(response.itemPrice()).isEqualTo(360000);
        assertThat(response.shippingFee()).isEqualTo(5000);
        // sellerServiceFee = (offerPrice + proposerShippingFee) - ownerSettlementAmount
        //                  = (360000 + 5000) - 365000 = 0 (파생식 기준, 하드코딩 아님)
        assertThat(response.sellerServiceFee()).isEqualTo(0);
        assertThat(response.settlementAmount()).isEqualTo(365000);
        assertThat(response.productId()).isNull();
        assertThat(response.sourceId()).isEqualTo(OFFER_ID);

        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.ACCEPTED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.CAPTURED);
        assertThat(competingOffer.getStatus()).isEqualTo(PurchaseOfferStatus.CANCELED);
        assertThat(competingOffer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    @Test
    void acceptPurchaseOffer_worksWithoutCompetingOffers() {
        setUpService();
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = existingOffer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));

        givenOfferDetail(item, offer);
        when(tradeRepository.existsByItemIdAndStatusNotIn(eq(ITEM_ID), any())).thenReturn(false);
        when(purchaseOfferRepository.findSentByItemIdForUpdate(ITEM_ID, PurchaseOfferStatus.SENT))
                .thenReturn(List.of(offer));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            ReflectionTestUtils.setField(trade, "id", 900L);
            return trade;
        });

        TradeDetailResponse response = purchaseOfferAcceptService.acceptPurchaseOffer(OFFER_ID, OWNER_ID);

        assertThat(response.tradeId()).isEqualTo(900L);
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.ACCEPTED);
    }

    @Test
    void acceptPurchaseOffer_expiresPastDeadlineOfferAndCommitsExpiration() {
        setUpService();
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = existingOffer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));

        givenOfferDetail(item, offer);

        assertThatThrownBy(() -> purchaseOfferAcceptService.acceptPurchaseOffer(OFFER_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_REQUEST_EXPIRED);

        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    @Test
    void acceptPurchaseOffer_rejectsNonOwner() {
        setUpService();
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = existingOffer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));

        givenOfferDetail(item, offer);

        assertThatThrownBy(() -> purchaseOfferAcceptService.acceptPurchaseOffer(OFFER_ID, PROPOSER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
    }

    @ParameterizedTest
    @EnumSource(value = PurchaseOfferStatus.class, names = {"REJECTED", "CANCELED", "ACCEPTED", "EXPIRED"})
    void acceptPurchaseOffer_rejectsNonSentStatus(PurchaseOfferStatus status) {
        setUpService();
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = existingOffer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));
        ReflectionTestUtils.setField(offer, "status", status);

        givenOfferDetail(item, offer);

        assertThatThrownBy(() -> purchaseOfferAcceptService.acceptPurchaseOffer(OFFER_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_SENT);
    }

    @Test
    void acceptPurchaseOffer_rejectsWhenActiveTradeExists() {
        // 이 가드는 "종결 상태가 아닌 거래"가 아니라 "아이템을 아직 풀어주지 않은 거래"를 막아야 한다.
        // 제외 대상이 TRANSFERRED 하나뿐이라는 것을 인자로 직접 검증해, COMPLETED 상태의 거래가
        // 이미 존재하는 경우에도(=TRANSFERRED로 아직 전이되지 않았으므로) 이 가드에 걸리는 것을 보장한다.
        setUpService();
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = existingOffer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));

        givenOfferDetail(item, offer);
        when(tradeRepository.existsByItemIdAndStatusNotIn(eq(ITEM_ID), any())).thenReturn(true);

        assertThatThrownBy(() -> purchaseOfferAcceptService.acceptPurchaseOffer(OFFER_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TradeErrorCode.TRADE_ALREADY_EXISTS_FOR_ITEM);

        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.SENT);

        ArgumentCaptor<Collection<TradeStatus>> statusesCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(tradeRepository).existsByItemIdAndStatusNotIn(eq(ITEM_ID), statusesCaptor.capture());
        assertThat(statusesCaptor.getValue()).containsExactly(TradeStatus.TRANSFERRED);
    }

    @Test
    void acceptPurchaseOffer_allowsAcceptWhenOnlyTransferredTradeExists() {
        // 같은 아이템에 이미 TRANSFERRED(소유권 이전 완료) 거래만 있는 경우는 아이템이 다시 풀린 상태이므로
        // 활성 거래 가드에 걸리지 않고 정상적으로 새 수락이 진행돼야 한다.
        setUpService();
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = existingOffer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));

        givenOfferDetail(item, offer);
        when(tradeRepository.existsByItemIdAndStatusNotIn(eq(ITEM_ID), any())).thenReturn(false);
        when(purchaseOfferRepository.findSentByItemIdForUpdate(ITEM_ID, PurchaseOfferStatus.SENT))
                .thenReturn(List.of(offer));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            ReflectionTestUtils.setField(trade, "id", 901L);
            return trade;
        });

        TradeDetailResponse response = purchaseOfferAcceptService.acceptPurchaseOffer(OFFER_ID, OWNER_ID);

        assertThat(response.tradeId()).isEqualTo(901L);
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.ACCEPTED);

        ArgumentCaptor<Collection<TradeStatus>> statusesCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(tradeRepository).existsByItemIdAndStatusNotIn(eq(ITEM_ID), statusesCaptor.capture());
        assertThat(statusesCaptor.getValue()).containsExactly(TradeStatus.TRANSFERRED);
    }

    @Test
    void acceptPurchaseOffer_excludesAcceptedOfferFromCompetingCancellation() {
        setUpService();
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = existingOffer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));

        givenOfferDetail(item, offer);
        when(tradeRepository.existsByItemIdAndStatusNotIn(eq(ITEM_ID), any())).thenReturn(false);
        when(purchaseOfferRepository.findSentByItemIdForUpdate(ITEM_ID, PurchaseOfferStatus.SENT))
                .thenReturn(List.of(offer));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            ReflectionTestUtils.setField(trade, "id", 900L);
            return trade;
        });

        purchaseOfferAcceptService.acceptPurchaseOffer(OFFER_ID, OWNER_ID);

        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.ACCEPTED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.CAPTURED);
    }

    private void givenOfferDetail(Item item, PurchaseOffer offer) {
        when(purchaseOfferRepository.findItemIdById(offer.getId())).thenReturn(Optional.of(ITEM_ID));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseOfferRepository.findByIdForUpdate(offer.getId())).thenReturn(Optional.of(offer));
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", UserGrade.BASIC);
        ReflectionTestUtils.setField(user, "username", "user" + id);
        ReflectionTestUtils.setField(user, "accountVisibility", AccountVisibility.PUBLIC);
        return user;
    }

    private Item item(Long id, User owner) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "owner", owner);
        ReflectionTestUtils.setField(item, "brandName", "Levis");
        ReflectionTestUtils.setField(item, "itemName", "LVC 1955 501");
        return item;
    }

    private DeliveryAddress address(Long id, User user) {
        DeliveryAddress address = instantiate(DeliveryAddress.class);
        ReflectionTestUtils.setField(address, "id", id);
        ReflectionTestUtils.setField(address, "user", user);
        ReflectionTestUtils.setField(address, "receiverName", "Proposer");
        ReflectionTestUtils.setField(address, "phone", "010-1234-5678");
        ReflectionTestUtils.setField(address, "addressLine1", "Seoul Gangnam");
        ReflectionTestUtils.setField(address, "addressLine2", "101");
        ReflectionTestUtils.setField(address, "postalCode", "12345");
        ReflectionTestUtils.setField(address, "requestNote", "Leave at door");
        return address;
    }

    private PurchaseOffer existingOffer(
            Long id,
            Item item,
            User proposer,
            User owner,
            LocalDateTime expiresAt
    ) {
        PurchaseOffer offer = PurchaseOffer.create(
                item,
                proposer,
                owner,
                address(ADDRESS_ID, proposer),
                360000,
                5000,
                21600,
                new BigDecimal("0.0600"),
                386600,
                365000,
                "mock_offer_auth_existing",
                "MOCK_CARD",
                expiresAt
        );
        ReflectionTestUtils.setField(offer, "id", id);
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
