package com.tenure.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.purchase.dto.PurchaseOfferReceivedListResponse;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PurchaseOfferServiceTest {

    private static final Long OWNER_ID = 1L;
    private static final Long PROPOSER_ID = 2L;
    private static final Long ITEM_ID = 10L;

    @Mock
    private PurchaseOfferRepository purchaseOfferRepository;

    @Mock
    private TradeRepository tradeRepository;

    private PurchaseOfferService purchaseOfferService;
    private PurchaseOfferExpirationService purchaseOfferExpirationService;

    @BeforeEach
    void setUp() {
        purchaseOfferExpirationService = new PurchaseOfferExpirationService();
        purchaseOfferService = new PurchaseOfferService(
                purchaseOfferRepository,
                tradeRepository,
                purchaseOfferExpirationService
        );
    }

    @Test
    void getReceivedPurchaseOffers_returnsReceivedListWithProposerAndOwnerAmounts() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(123L, item, proposer, owner, LocalDateTime.now().plusHours(2));
        ReflectionTestUtils.setField(offer, "createdAt", LocalDateTime.now().minusMinutes(10));

        givenReceivedList(List.of(), List.of(offer));

        PurchaseOfferReceivedListResponse response = purchaseOfferService.getReceivedPurchaseOffers(
                OWNER_ID,
                List.of(PurchaseOfferStatus.SENT),
                null,
                null,
                20
        );

        assertThat(response.content()).hasSize(1);
        PurchaseOfferReceivedListResponse.Item itemResponse = response.content().get(0);
        assertThat(itemResponse.offerId()).isEqualTo(123L);
        assertThat(itemResponse.status()).isEqualTo(PurchaseOfferStatus.SENT);
        assertThat(itemResponse.itemId()).isEqualTo(ITEM_ID);
        assertThat(itemResponse.brandName()).isEqualTo("Levis");
        assertThat(itemResponse.itemName()).isEqualTo("LVC 1955 501");
        assertThat(itemResponse.imageUrl()).isEqualTo("https://image.url/item.jpg");
        assertThat(itemResponse.proposerId()).isEqualTo(PROPOSER_ID);
        assertThat(itemResponse.proposerUsername()).isEqualTo("user" + PROPOSER_ID);
        assertThat(itemResponse.offerAmount()).isEqualTo(45000);
        assertThat(itemResponse.shippingFee()).isEqualTo(3000);
        assertThat(itemResponse.proposerServiceFee()).isEqualTo(2700);
        assertThat(itemResponse.totalPaymentAmount()).isEqualTo(50700);
        assertThat(itemResponse.ownerSettlementAmount()).isEqualTo(48000);
        assertThat(itemResponse.paymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.AUTHORIZED);
        assertThat(itemResponse.remainingSeconds()).isPositive();
        assertThat(itemResponse.canAccept()).isTrue();
        assertThat(itemResponse.canReject()).isTrue();
        assertThat(itemResponse.tradeId()).isNull();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    void getReceivedPurchaseOffers_expiresSentBeforeQueryingList() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer expiredOffer = offer(123L, item, proposer, owner, LocalDateTime.now().minusMinutes(1));
        ReflectionTestUtils.setField(expiredOffer, "createdAt", LocalDateTime.now().minusMinutes(10));

        givenReceivedList(List.of(expiredOffer), List.of(expiredOffer));

        PurchaseOfferReceivedListResponse response = purchaseOfferService.getReceivedPurchaseOffers(
                OWNER_ID,
                List.of(PurchaseOfferStatus.EXPIRED),
                null,
                null,
                20
        );

        assertThat(expiredOffer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(expiredOffer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).remainingSeconds()).isNull();
        assertThat(response.content().get(0).canAccept()).isFalse();
        assertThat(response.content().get(0).canReject()).isFalse();
    }

    @Test
    void getReceivedPurchaseOffers_mapsTradeIdForAcceptedOffer() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer acceptedOffer = offer(123L, item, proposer, owner, LocalDateTime.now().plusHours(2));
        ReflectionTestUtils.setField(acceptedOffer, "status", PurchaseOfferStatus.ACCEPTED);
        ReflectionTestUtils.setField(acceptedOffer, "createdAt", LocalDateTime.now().minusMinutes(10));
        Trade trade = trade(900L, acceptedOffer.getId());

        givenReceivedList(List.of(), List.of(acceptedOffer));
        when(tradeRepository.findAllBySourceTypeAndSourceIdIn(
                TradeSourceType.PURCHASE_OFFER,
                List.of(acceptedOffer.getId())
        )).thenReturn(List.of(trade));

        PurchaseOfferReceivedListResponse response = purchaseOfferService.getReceivedPurchaseOffers(
                OWNER_ID,
                List.of(PurchaseOfferStatus.ACCEPTED),
                null,
                null,
                20
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).tradeId()).isEqualTo(900L);
        assertThat(response.content().get(0).remainingSeconds()).isNull();
        assertThat(response.content().get(0).canAccept()).isFalse();
        assertThat(response.content().get(0).canReject()).isFalse();
    }

    private void givenReceivedList(List<PurchaseOffer> expiredOffers, List<PurchaseOffer> fetchedOffers) {
        when(purchaseOfferRepository.findExpiredSentByOwnerIdForUpdate(
                eq(OWNER_ID),
                eq(PurchaseOfferStatus.SENT),
                any(LocalDateTime.class)
        )).thenReturn(expiredOffers);
        when(purchaseOfferRepository.findReceivedListByOwnerWithCursor(
                eq(OWNER_ID),
                any(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(fetchedOffers);
    }

    private User user(Long id, UserGrade grade) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", grade);
        ReflectionTestUtils.setField(user, "username", "user" + id);
        ReflectionTestUtils.setField(user, "profileImageUrl", "https://image.url/profile-" + id + ".jpg");
        return user;
    }

    private Item item(Long id, User owner) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "owner", owner);
        ReflectionTestUtils.setField(item, "brandName", "Levis");
        ReflectionTestUtils.setField(item, "itemName", "LVC 1955 501");
        ReflectionTestUtils.setField(item, "representativeImageUrl", "https://image.url/item.jpg");
        return item;
    }

    private PurchaseOffer offer(
            Long id,
            Item item,
            User proposer,
            User owner,
            LocalDateTime expiresAt
    ) {
        PurchaseOffer offer = instantiate(PurchaseOffer.class);
        ReflectionTestUtils.setField(offer, "id", id);
        ReflectionTestUtils.setField(offer, "item", item);
        ReflectionTestUtils.setField(offer, "proposer", proposer);
        ReflectionTestUtils.setField(offer, "owner", owner);
        ReflectionTestUtils.setField(offer, "offerPrice", 45000);
        ReflectionTestUtils.setField(offer, "proposerShippingFee", 3000);
        ReflectionTestUtils.setField(offer, "proposerServiceFee", 2700);
        ReflectionTestUtils.setField(offer, "feeRateSnapshot", new BigDecimal("0.0600"));
        ReflectionTestUtils.setField(offer, "totalPaymentAmount", 50700);
        ReflectionTestUtils.setField(offer, "ownerSettlementAmount", 48000);
        ReflectionTestUtils.setField(offer, "paymentAuthorizationId", "mock_auth_offer");
        ReflectionTestUtils.setField(offer, "paymentAuthorizationStatus", PaymentAuthorizationStatus.AUTHORIZED);
        ReflectionTestUtils.setField(offer, "paymentMethodId", "MOCK_CARD");
        ReflectionTestUtils.setField(offer, "status", PurchaseOfferStatus.SENT);
        ReflectionTestUtils.setField(offer, "expiresAt", expiresAt);
        return offer;
    }

    private Trade trade(Long id, Long sourceId) {
        Trade trade = instantiate(Trade.class);
        ReflectionTestUtils.setField(trade, "id", id);
        ReflectionTestUtils.setField(trade, "sourceType", TradeSourceType.PURCHASE_OFFER);
        ReflectionTestUtils.setField(trade, "sourceId", sourceId);
        return trade;
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
