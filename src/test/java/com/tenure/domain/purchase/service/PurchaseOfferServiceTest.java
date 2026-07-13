package com.tenure.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.dto.PurchaseOfferRejectResponse;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.global.exception.CustomException;
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
class PurchaseOfferServiceTest {

    private static final Long OFFER_ID = 123L;
    private static final Long ITEM_ID = 10L;
    private static final Long OWNER_ID = 1L;
    private static final Long PROPOSER_ID = 2L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseOfferRepository purchaseOfferRepository;

    private PurchaseOfferService purchaseOfferService;
    private PurchaseOfferExpirationService purchaseOfferExpirationService;

    @BeforeEach
    void setUp() {
        purchaseOfferExpirationService = new PurchaseOfferExpirationService();
        purchaseOfferService = new PurchaseOfferService(
                itemRepository,
                purchaseOfferRepository,
                purchaseOfferExpirationService
        );
    }

    @Test
    void rejectPurchaseOffer_rejectsSentOfferAndReleasesAuthorization() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));

        givenRejectTarget(item, offer);

        PurchaseOfferRejectResponse response = purchaseOfferService.rejectPurchaseOffer(OFFER_ID, OWNER_ID);

        assertThat(response.offerId()).isEqualTo(OFFER_ID);
        assertThat(response.status()).isEqualTo(PurchaseOfferStatus.REJECTED);
        assertThat(response.paymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(response.serverTime()).isNotNull();
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.REJECTED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);

        InOrder lockOrder = inOrder(itemRepository, purchaseOfferRepository);
        lockOrder.verify(purchaseOfferRepository).findItemIdById(OFFER_ID);
        lockOrder.verify(itemRepository).findByIdForUpdate(ITEM_ID);
        lockOrder.verify(purchaseOfferRepository).findByIdForUpdate(OFFER_ID);
    }

    @Test
    void rejectPurchaseOffer_rejectsNonOwner() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));

        givenRejectTarget(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.rejectPurchaseOffer(OFFER_ID, PROPOSER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
    }

    @Test
    void rejectPurchaseOffer_rejectsNonSentStatus() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(offer, "status", PurchaseOfferStatus.ACCEPTED);

        givenRejectTarget(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.rejectPurchaseOffer(OFFER_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_SENT);
    }

    @Test
    void rejectPurchaseOffer_expiresPastDeadlineSentOfferBeforeRejecting() {
        User owner = user(OWNER_ID, UserGrade.BASIC);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));

        givenRejectTarget(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.rejectPurchaseOffer(OFFER_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_REQUEST_EXPIRED);
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    private void givenRejectTarget(Item item, PurchaseOffer offer) {
        when(purchaseOfferRepository.findItemIdById(OFFER_ID)).thenReturn(Optional.of(ITEM_ID));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseOfferRepository.findByIdForUpdate(OFFER_ID)).thenReturn(Optional.of(offer));
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
