package com.tenure.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.dto.PurchaseIntentCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseIntentCreateResponse;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.exception.PurchaseIntentErrorCode;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PurchaseIntentServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long ITEM_ID = 10L;
    private static final Long BUYER_ID = 2L;
    private static final Long SELLER_ID = 1L;
    private static final Long ADDRESS_ID = 100L;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseIntentRepository purchaseIntentRepository;

    @Mock
    private DeliveryAddressRepository deliveryAddressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRelationshipRepository followRelationshipRepository;

    private PurchaseIntentService purchaseIntentService;

    @BeforeEach
    void setUp() {
        purchaseIntentService = new PurchaseIntentService(
                productRepository,
                itemRepository,
                purchaseIntentRepository,
                deliveryAddressRepository,
                userRepository,
                followRelationshipRepository
        );
    }

    @Test
    void createPurchaseIntent_createsAuthorizedIntentWithSellerPaysFee() {
        User seller = user(SELLER_ID, UserGrade.BASIC);
        User buyer = user(BUYER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller, FeePolicy.SELLER_PAYS, new BigDecimal("0.0600"));
        DeliveryAddress address = address(ADDRESS_ID, buyer);

        givenPurchasableProduct(product, item, buyer, List.of());
        when(deliveryAddressRepository.findByIdAndUserId(ADDRESS_ID, BUYER_ID)).thenReturn(Optional.of(address));
        when(purchaseIntentRepository.save(any(PurchaseIntent.class))).thenAnswer(invocation -> {
            PurchaseIntent intent = invocation.getArgument(0);
            ReflectionTestUtils.setField(intent, "id", 123L);
            return intent;
        });

        PurchaseIntentCreateResponse response = purchaseIntentService.createPurchaseIntent(
                PRODUCT_ID,
                BUYER_ID,
                request(true)
        );

        assertThat(response.intentId()).isEqualTo(123L);
        assertThat(response.status()).isEqualTo(PurchaseIntentStatus.SENT);
        assertThat(response.amounts().productAmount()).isEqualTo(360000);
        assertThat(response.amounts().shippingFee()).isEqualTo(5000);
        assertThat(response.amounts().buyerServiceFee()).isZero();
        assertThat(response.amounts().sellerServiceFee()).isEqualTo(21600);
        assertThat(response.amounts().buyerPaymentAmount()).isEqualTo(365000);
        assertThat(response.amounts().sellerSettlementAmount()).isEqualTo(343400);

        ArgumentCaptor<PurchaseIntent> captor = ArgumentCaptor.forClass(PurchaseIntent.class);
        verify(purchaseIntentRepository).save(captor.capture());
        PurchaseIntent savedIntent = captor.getValue();
        assertThat(savedIntent.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.AUTHORIZED);
        assertThat(savedIntent.getPaymentAuthorizationId()).startsWith("mock_auth_");
        assertThat(savedIntent.getFeeRateSnapshot()).isEqualByComparingTo("0.0600");
        assertThat(savedIntent.getDeliveryReceiverName()).isEqualTo("Buyer");
        assertThat(savedIntent.getDeliveryAddressLine1()).isEqualTo("Seoul Gangnam");
    }

    @Test
    void createPurchaseIntent_rejectsWhenAgreementIsFalse() {
        assertThatThrownBy(() -> purchaseIntentService.createPurchaseIntent(
                PRODUCT_ID,
                BUYER_ID,
                request(false)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseIntentErrorCode.AGREEMENT_REQUIRED);
    }

    @Test
    void createPurchaseIntent_rejectsActiveSentDuplicate() {
        User seller = user(SELLER_ID, UserGrade.BASIC);
        User buyer = user(BUYER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller, FeePolicy.SELLER_PAYS, new BigDecimal("0.0600"));
        PurchaseIntent existingIntent = existingIntent(777L, product, buyer, seller, LocalDateTime.now().plusHours(1));

        givenPurchasableProduct(product, item, buyer, List.of(existingIntent));

        assertThatThrownBy(() -> purchaseIntentService.createPurchaseIntent(
                PRODUCT_ID,
                BUYER_ID,
                request(true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseIntentErrorCode.ACTIVE_INTENT_EXISTS);
    }

    @Test
    void createPurchaseIntent_expiresOldSentIntentAndAllowsNewRequest() {
        User seller = user(SELLER_ID, UserGrade.BASIC);
        User buyer = user(BUYER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller, FeePolicy.BUYER_PAYS, new BigDecimal("0.0300"));
        DeliveryAddress address = address(ADDRESS_ID, buyer);
        PurchaseIntent expiredIntent = existingIntent(777L, product, buyer, seller, LocalDateTime.now().minusMinutes(1));

        givenPurchasableProduct(product, item, buyer, List.of(expiredIntent));
        when(deliveryAddressRepository.findByIdAndUserId(ADDRESS_ID, BUYER_ID)).thenReturn(Optional.of(address));
        when(purchaseIntentRepository.save(any(PurchaseIntent.class))).thenAnswer(invocation -> {
            PurchaseIntent intent = invocation.getArgument(0);
            ReflectionTestUtils.setField(intent, "id", 124L);
            return intent;
        });

        PurchaseIntentCreateResponse response = purchaseIntentService.createPurchaseIntent(
                PRODUCT_ID,
                BUYER_ID,
                request(true)
        );

        assertThat(expiredIntent.getStatus()).isEqualTo(PurchaseIntentStatus.EXPIRED);
        assertThat(expiredIntent.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(response.amounts().buyerServiceFee()).isEqualTo(10800);
        assertThat(response.amounts().sellerServiceFee()).isZero();
        assertThat(response.amounts().buyerPaymentAmount()).isEqualTo(375800);
        assertThat(response.amounts().sellerSettlementAmount()).isEqualTo(365000);
    }

    @Test
    void createPurchaseIntent_splitFeeMakesSellerPayOddWon() {
        User seller = user(SELLER_ID, UserGrade.RECORD);
        User buyer = user(BUYER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller, FeePolicy.SPLIT, new BigDecimal("0.0300"));
        ReflectionTestUtils.setField(product, "price", 33368);
        ReflectionTestUtils.setField(product, "shippingFee", 0);
        DeliveryAddress address = address(ADDRESS_ID, buyer);

        givenPurchasableProduct(product, item, buyer, List.of());
        when(deliveryAddressRepository.findByIdAndUserId(ADDRESS_ID, BUYER_ID)).thenReturn(Optional.of(address));
        when(purchaseIntentRepository.save(any(PurchaseIntent.class))).thenAnswer(invocation -> {
            PurchaseIntent intent = invocation.getArgument(0);
            ReflectionTestUtils.setField(intent, "id", 125L);
            return intent;
        });

        PurchaseIntentCreateResponse response = purchaseIntentService.createPurchaseIntent(
                PRODUCT_ID,
                BUYER_ID,
                request(true)
        );

        assertThat(response.amounts().buyerServiceFee()).isEqualTo(500);
        assertThat(response.amounts().sellerServiceFee()).isEqualTo(501);
        assertThat(response.amounts().buyerPaymentAmount()).isEqualTo(33868);
        assertThat(response.amounts().sellerSettlementAmount()).isEqualTo(32867);
    }

    private void givenPurchasableProduct(
            Product product,
            Item item,
            User buyer,
            List<PurchaseIntent> sentIntents
    ) {
        when(productRepository.findByIdForUpdate(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(BUYER_ID)).thenReturn(Optional.of(buyer));
        when(purchaseIntentRepository.findSentByProductIdAndBuyerIdForUpdate(
                PRODUCT_ID,
                BUYER_ID,
                PurchaseIntentStatus.SENT
        )).thenReturn(sentIntents);
    }

    private PurchaseIntentCreateRequest request(Boolean agreement) {
        return new PurchaseIntentCreateRequest(ADDRESS_ID, "MOCK_CARD", agreement);
    }

    private User user(Long id, UserGrade grade) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", grade);
        ReflectionTestUtils.setField(user, "username", "user" + id);
        ReflectionTestUtils.setField(user, "accountVisibility", AccountVisibility.PUBLIC);
        return user;
    }

    private Item item(Long id, User owner) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "owner", owner);
        return item;
    }

    private Product product(Long id, Item item, User seller, FeePolicy feePolicy, BigDecimal feeRate) {
        Product product = Product.create(
                item,
                seller,
                360000,
                5000,
                feePolicy,
                feeRate,
                "https://image.url/product.jpg",
                null,
                null,
                "sale description"
        );
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "productStatus", ProductStatus.ON_SALE);
        return product;
    }

    private DeliveryAddress address(Long id, User user) {
        DeliveryAddress address = instantiate(DeliveryAddress.class);
        ReflectionTestUtils.setField(address, "id", id);
        ReflectionTestUtils.setField(address, "user", user);
        ReflectionTestUtils.setField(address, "receiverName", "Buyer");
        ReflectionTestUtils.setField(address, "phone", "010-1234-5678");
        ReflectionTestUtils.setField(address, "addressLine1", "Seoul Gangnam");
        ReflectionTestUtils.setField(address, "addressLine2", "101");
        ReflectionTestUtils.setField(address, "postalCode", "12345");
        ReflectionTestUtils.setField(address, "requestNote", "Leave at door");
        return address;
    }

    private PurchaseIntent existingIntent(
            Long id,
            Product product,
            User buyer,
            User seller,
            LocalDateTime expiresAt
    ) {
        PurchaseIntent intent = PurchaseIntent.create(
                product,
                buyer,
                seller,
                address(ADDRESS_ID, buyer),
                360000,
                product.getFeePolicy(),
                product.getFeeRate(),
                5000,
                0,
                21600,
                365000,
                343400,
                "mock_auth_existing",
                "MOCK_CARD",
                expiresAt
        );
        ReflectionTestUtils.setField(intent, "id", id);
        return intent;
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
