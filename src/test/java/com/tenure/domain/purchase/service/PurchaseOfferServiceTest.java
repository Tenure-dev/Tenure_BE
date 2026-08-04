package com.tenure.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.notification.service.NotificationFactory;
import com.tenure.domain.notification.service.NotificationService;
import com.tenure.domain.purchase.dto.PurchaseOfferCancelResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateResponse;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private static final Long ADDRESS_ID = 100L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseOfferRepository purchaseOfferRepository;

    @Mock
    private DeliveryAddressRepository deliveryAddressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private NotificationService notificationService;

    private PurchaseOfferService purchaseOfferService;

    @BeforeEach
    void setUp() {
        NotificationFactory notificationFactory = new NotificationFactory();
        purchaseOfferService = new PurchaseOfferService(
                itemRepository,
                purchaseOfferRepository,
                new PurchaseOfferExpirationService(notificationFactory, notificationService),
                deliveryAddressRepository,
                userRepository,
                tradeRepository,
                notificationFactory,
                notificationService
        );
    }

    @Test
    void createPurchaseOffer_savesTradeRequestNote() {
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        DeliveryAddress address = address(proposer);
        PurchaseOfferCreateRequest request = new PurchaseOfferCreateRequest(
                360000,
                ADDRESS_ID,
                "MOCK_CARD",
                "상품 상태가 사진과 동일하다면 바로 거래 진행하고 싶어요.",
                true
        );

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(PROPOSER_ID)).thenReturn(Optional.of(proposer));
        when(purchaseOfferRepository.findByItemIdAndProposerIdForUpdate(ITEM_ID, PROPOSER_ID))
                .thenReturn(Optional.empty());
        when(deliveryAddressRepository.findByIdAndUser_Id(ADDRESS_ID, PROPOSER_ID))
                .thenReturn(Optional.of(address));

        PurchaseOfferCreateResponse response = purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request
        );

        ArgumentCaptor<PurchaseOffer> offerCaptor = ArgumentCaptor.forClass(PurchaseOffer.class);
        verify(purchaseOfferRepository).save(offerCaptor.capture());
        PurchaseOffer savedOffer = offerCaptor.getValue();
        assertThat(savedOffer.getTradeRequestNote()).isEqualTo(request.tradeRequestNote());
        assertThat(response.status()).isEqualTo(PurchaseOfferStatus.SENT);
    }

    @Test
    void cancelPurchaseOffer_cancelsSentOfferAndReleasesAuthorization() {
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));

        givenOfferTarget(item, offer);

        PurchaseOfferCancelResponse response = purchaseOfferService.cancelPurchaseOffer(OFFER_ID, PROPOSER_ID);

        assertThat(response.offerId()).isEqualTo(OFFER_ID);
        assertThat(response.status()).isEqualTo(PurchaseOfferStatus.CANCELED);
        assertThat(response.paymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(response.serverTime()).isNotNull();
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.CANCELED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);

        InOrder lockOrder = inOrder(itemRepository, purchaseOfferRepository);
        lockOrder.verify(itemRepository).findByIdForUpdate(ITEM_ID);
        lockOrder.verify(purchaseOfferRepository).findByIdForUpdate(OFFER_ID);
    }

    @Test
    void cancelPurchaseOffer_rejectsNonProposer() {
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));

        givenOfferTarget(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.cancelPurchaseOffer(OFFER_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
    }

    @Test
    void cancelPurchaseOffer_rejectsNonSentStatus() {
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(offer, "status", PurchaseOfferStatus.REJECTED);

        givenOfferTarget(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.cancelPurchaseOffer(OFFER_ID, PROPOSER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_SENT);
    }

    @Test
    void cancelPurchaseOffer_expiresPastDeadlineSentOfferBeforeCanceling() {
        User owner = user(OWNER_ID);
        User proposer = user(PROPOSER_ID);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));

        givenOfferTarget(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.cancelPurchaseOffer(OFFER_ID, PROPOSER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_REQUEST_EXPIRED);
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    private void givenOfferTarget(Item item, PurchaseOffer offer) {
        when(purchaseOfferRepository.findItemIdById(OFFER_ID)).thenReturn(Optional.of(ITEM_ID));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseOfferRepository.findByIdForUpdate(OFFER_ID)).thenReturn(Optional.of(offer));
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", UserGrade.BASIC);
        ReflectionTestUtils.setField(user, "username", "user" + id);
        return user;
    }

    private Item item(Long id, User owner) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "owner", owner);
        ReflectionTestUtils.setField(item, "brandName", "Levis");
        ReflectionTestUtils.setField(item, "itemName", "LVC 1955 501");
        ReflectionTestUtils.setField(item, "itemStatus", ItemStatus.OWNED);
        ReflectionTestUtils.setField(item, "purchaseOfferEnabled", true);
        return item;
    }

    private DeliveryAddress address(User user) {
        DeliveryAddress address = instantiate(DeliveryAddress.class);
        ReflectionTestUtils.setField(address, "id", ADDRESS_ID);
        ReflectionTestUtils.setField(address, "user", user);
        ReflectionTestUtils.setField(address, "receiverName", "Proposer");
        ReflectionTestUtils.setField(address, "phone", "010-1234-5678");
        ReflectionTestUtils.setField(address, "addressLine1", "Seoul Gangnam");
        ReflectionTestUtils.setField(address, "addressLine2", "101");
        ReflectionTestUtils.setField(address, "postalCode", "12345");
        ReflectionTestUtils.setField(address, "requestNote", "Leave at door");
        return address;
    }

    private PurchaseOffer offer(
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
                address(proposer),
                360000,
                5000,
                21600,
                new BigDecimal("0.0600"),
                386600,
                365000,
                "mock_offer_auth_existing",
                "MOCK_CARD",
                "상품 상태가 사진과 동일하다면 바로 거래 진행하고 싶어요.",
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
