package com.tenure.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
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
class PurchaseIntentExpirationProcessorTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long ITEM_ID = 10L;
    private static final Long INTENT_ID = 100L;
    private static final Long BUYER_ID = 2L;
    private static final Long SELLER_ID = 1L;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseIntentRepository purchaseIntentRepository;

    private PurchaseIntentExpirationProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new PurchaseIntentExpirationProcessor(
                productRepository,
                itemRepository,
                purchaseIntentRepository,
                new PurchaseIntentExpirationService()
        );
    }

    @Test
    void expire_locksProductItemIntentInOrderAndExpiresSentIntent() {
        User seller = user(SELLER_ID);
        User buyer = user(BUYER_ID);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller);
        PurchaseIntent intent = intent(INTENT_ID, product, buyer, seller, LocalDateTime.now().minusMinutes(1));
        LocalDateTime now = LocalDateTime.now();

        when(purchaseIntentRepository.findProductIdById(INTENT_ID)).thenReturn(Optional.of(PRODUCT_ID));
        when(productRepository.findByIdForUpdate(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseIntentRepository.findByIdForUpdate(INTENT_ID)).thenReturn(Optional.of(intent));

        boolean expired = processor.expire(INTENT_ID, now);

        assertThat(expired).isTrue();
        assertThat(intent.getStatus()).isEqualTo(PurchaseIntentStatus.EXPIRED);
        assertThat(intent.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);

        InOrder lockOrder = inOrder(productRepository, itemRepository, purchaseIntentRepository);
        lockOrder.verify(productRepository).findByIdForUpdate(PRODUCT_ID);
        lockOrder.verify(itemRepository).findByIdForUpdate(ITEM_ID);
        lockOrder.verify(purchaseIntentRepository).findByIdForUpdate(INTENT_ID);
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
        return item;
    }

    private Product product(Long id, Item item, User seller) {
        Product product = Product.create(
                item,
                seller,
                360000,
                5000,
                FeePolicy.SELLER_PAYS,
                new BigDecimal("0.0600"),
                "https://image.url/product.jpg",
                null,
                null,
                "sale description"
        );
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private PurchaseIntent intent(
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
                address(buyer),
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

    private DeliveryAddress address(User user) {
        DeliveryAddress address = instantiate(DeliveryAddress.class);
        ReflectionTestUtils.setField(address, "id", 100L);
        ReflectionTestUtils.setField(address, "user", user);
        ReflectionTestUtils.setField(address, "receiverName", "Buyer");
        ReflectionTestUtils.setField(address, "phone", "010-1234-5678");
        ReflectionTestUtils.setField(address, "addressLine1", "Seoul Gangnam");
        ReflectionTestUtils.setField(address, "addressLine2", "101");
        return address;
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
