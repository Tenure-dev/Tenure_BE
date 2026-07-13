package com.tenure.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateResponse;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PurchaseOfferServiceTest {

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

    private PurchaseOfferService purchaseOfferService;

    @BeforeEach
    void setUp() {
        purchaseOfferService = new PurchaseOfferService(
                itemRepository,
                purchaseOfferRepository,
                deliveryAddressRepository,
                userRepository
        );
    }

    @Test
    void createPurchaseOffer_createsAuthorizedOfferWithOwnerGradeFeeAndShippingFee() {
        User owner = user(OWNER_ID, UserGrade.BASIC, 5000);
        User proposer = user(PROPOSER_ID, UserGrade.RECORD, null);
        Item item = item(ITEM_ID, owner);
        DeliveryAddress address = address(ADDRESS_ID, proposer);

        givenOfferableItem(item, proposer, Optional.empty());
        when(deliveryAddressRepository.findByIdAndUser_Id(ADDRESS_ID, PROPOSER_ID)).thenReturn(Optional.of(address));
        when(purchaseOfferRepository.save(any(PurchaseOffer.class))).thenAnswer(invocation -> {
            PurchaseOffer offer = invocation.getArgument(0);
            ReflectionTestUtils.setField(offer, "id", 123L);
            return offer;
        });

        PurchaseOfferCreateResponse response = purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(360000, true)
        );

        assertThat(response.offerId()).isEqualTo(123L);
        assertThat(response.status()).isEqualTo(PurchaseOfferStatus.SENT);
        assertThat(response.paymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.AUTHORIZED);
        assertThat(response.remainingSeconds()).isPositive();
        assertThat(response.amounts().offerAmount()).isEqualTo(360000);
        assertThat(response.amounts().shippingFee()).isEqualTo(5000);
        assertThat(response.amounts().proposerServiceFee()).isEqualTo(21600);
        assertThat(response.amounts().totalPaymentAmount()).isEqualTo(386600);
        assertThat(response.amounts().ownerSettlementAmount()).isEqualTo(365000);

        ArgumentCaptor<PurchaseOffer> captor = ArgumentCaptor.forClass(PurchaseOffer.class);
        verify(purchaseOfferRepository).save(captor.capture());
        PurchaseOffer savedOffer = captor.getValue();
        assertThat(savedOffer.getPaymentAuthorizationId()).startsWith("mock_offer_auth_");
        assertThat(savedOffer.getFeeRateSnapshot()).isEqualByComparingTo("0.0600");
        assertThat(savedOffer.getDeliveryReceiverName()).isEqualTo("Proposer");
    }

    @Test
    void createPurchaseOffer_rejectsWhenOfferPriceIsLowerThanMinimum() {
        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(999, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.OFFER_PRICE_TOO_LOW);
    }

    @Test
    void createPurchaseOffer_rejectsWhenAgreementIsFalse() {
        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(1000, false)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.AGREEMENT_REQUIRED);
    }

    @Test
    void createPurchaseOffer_rejectsSelfOffer() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                OWNER_ID,
                request(1000, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.SELF_OFFER_NOT_ALLOWED);
    }

    @Test
    void createPurchaseOffer_rejectsWhenItemIsNotOwned() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        ReflectionTestUtils.setField(item, "itemStatus", ItemStatus.ON_SALE);

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(PROPOSER_ID)).thenReturn(Optional.of(proposer));

        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(1000, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.ITEM_NOT_OWNED);
    }

    @Test
    void createPurchaseOffer_rejectsWhenPurchaseOfferDisabled() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        ReflectionTestUtils.setField(item, "purchaseOfferEnabled", false);

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(PROPOSER_ID)).thenReturn(Optional.of(proposer));

        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(1000, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_DISABLED);
    }

    @Test
    void createPurchaseOffer_rejectsWhenOfferAlreadyExistsForSameUserAndItem() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer existingOffer = existingOffer(777L, item, proposer, owner);

        givenOfferableItem(item, proposer, Optional.of(existingOffer));

        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(1000, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_ALREADY_USED);
    }

    private void givenOfferableItem(Item item, User proposer, Optional<PurchaseOffer> existingOffer) {
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(PROPOSER_ID)).thenReturn(Optional.of(proposer));
        when(purchaseOfferRepository.findByItemIdAndProposerIdForUpdate(ITEM_ID, PROPOSER_ID))
                .thenReturn(existingOffer);
    }

    private PurchaseOfferCreateRequest request(Integer offerPrice, Boolean agreement) {
        return new PurchaseOfferCreateRequest(offerPrice, ADDRESS_ID, "MOCK_CARD", agreement);
    }

    private User user(Long id, UserGrade grade, Integer defaultShippingFee) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", grade);
        ReflectionTestUtils.setField(user, "username", "user" + id);
        ReflectionTestUtils.setField(user, "defaultShippingFee", defaultShippingFee);
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

    private PurchaseOffer existingOffer(Long id, Item item, User proposer, User owner) {
        PurchaseOffer offer = PurchaseOffer.create(
                item,
                proposer,
                owner,
                address(ADDRESS_ID, proposer),
                1000,
                0,
                60,
                java.math.BigDecimal.valueOf(0.0600),
                1060,
                1000,
                "mock_offer_auth_existing",
                "MOCK_CARD",
                LocalDateTime.now().plusHours(1)
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
