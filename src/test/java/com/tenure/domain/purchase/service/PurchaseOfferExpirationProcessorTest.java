package com.tenure.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PurchaseOfferExpirationProcessorTest {

    private static final Long OFFER_ID = 123L;
    private static final Long ITEM_ID = 10L;
    private static final Long OWNER_ID = 1L;
    private static final Long PROPOSER_ID = 2L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseOfferRepository purchaseOfferRepository;

    private PurchaseOfferExpirationProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new PurchaseOfferExpirationProcessor(
                itemRepository,
                purchaseOfferRepository,
                new PurchaseOfferExpirationService()
        );
    }

    @Test
    void expireOne_expiresSentOfferAndReleasesAuthorization() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseOfferRepository.findByIdForUpdate(OFFER_ID)).thenReturn(Optional.of(offer));

        boolean expired = processor.expireOne(OFFER_ID, ITEM_ID, LocalDateTime.now());

        assertThat(expired).isTrue();
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);

        InOrder lockOrder = inOrder(itemRepository, purchaseOfferRepository);
        lockOrder.verify(itemRepository).findByIdForUpdate(ITEM_ID);
        lockOrder.verify(purchaseOfferRepository).findByIdForUpdate(OFFER_ID);
    }

    @Test
    void expireOne_doesNotChangeOfferBeforeDeadline() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusMinutes(1));

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseOfferRepository.findByIdForUpdate(OFFER_ID)).thenReturn(Optional.of(offer));

        boolean expired = processor.expireOne(OFFER_ID, ITEM_ID, LocalDateTime.now());

        assertThat(expired).isFalse();
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.SENT);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.AUTHORIZED);
    }

    @Test
    void expireOne_doesNotChangeClosedOffer() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));
        ReflectionTestUtils.setField(offer, "status", PurchaseOfferStatus.REJECTED);

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseOfferRepository.findByIdForUpdate(OFFER_ID)).thenReturn(Optional.of(offer));

        boolean expired = processor.expireOne(OFFER_ID, ITEM_ID, LocalDateTime.now());

        assertThat(expired).isFalse();
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.REJECTED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.AUTHORIZED);
    }

    private User user(Long id, UserGrade grade) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", grade);
        ReflectionTestUtils.setField(user, "username", "user" + id);
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
