package com.tenure.domain.trade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.exception.PurchaseIntentErrorCode;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.domain.purchase.service.PurchaseIntentExpirationService;
import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PurchaseIntentAcceptServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long ITEM_ID = 10L;
    private static final Long BUYER_ID = 2L;
    private static final Long SELLER_ID = 1L;
    private static final Long ADDRESS_ID = 100L;
    private static final Long INTENT_ID = 123L;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseIntentRepository purchaseIntentRepository;

    @Mock
    private TradeRepository tradeRepository;

    private PurchaseIntentAcceptService purchaseIntentAcceptService;

    private void setUpService() {
        purchaseIntentAcceptService = new PurchaseIntentAcceptService(
                productRepository,
                itemRepository,
                purchaseIntentRepository,
                tradeRepository,
                new PurchaseIntentExpirationService()
        );
    }

    @Test
    void acceptPurchaseIntent_createsTradeAndCancelsCompetingIntents() {
        setUpService();
        User seller = user(SELLER_ID);
        User buyer = user(BUYER_ID);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller);
        PurchaseIntent intent = existingIntent(INTENT_ID, product, buyer, seller, LocalDateTime.now().plusHours(2));
        PurchaseIntent competingIntent = existingIntent(456L, product, user(3L), seller, LocalDateTime.now().plusHours(1));

        givenIntentDetail(product, item, intent);
        when(purchaseIntentRepository.findSentByProductIdForUpdate(PRODUCT_ID, PurchaseIntentStatus.SENT))
                .thenReturn(List.of(competingIntent));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            ReflectionTestUtils.setField(trade, "id", 900L);
            return trade;
        });

        TradeDetailResponse response = purchaseIntentAcceptService.acceptPurchaseIntent(INTENT_ID, SELLER_ID);

        assertThat(response.tradeId()).isEqualTo(900L);
        assertThat(response.status()).isEqualTo(TradeStatus.PAID);
        assertThat(response.viewerMode()).isEqualTo(TradeViewerMode.SELLER);
        assertThat(response.itemPrice()).isEqualTo(360000);
        assertThat(response.shippingFee()).isEqualTo(5000);
        assertThat(response.sourceId()).isEqualTo(INTENT_ID);

        assertThat(intent.getStatus()).isEqualTo(PurchaseIntentStatus.ACCEPTED);
        assertThat(intent.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.CAPTURED);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.TRADING);
        assertThat(competingIntent.getStatus()).isEqualTo(PurchaseIntentStatus.CANCELED);
        assertThat(competingIntent.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    @Test
    void acceptPurchaseIntent_worksWithoutCompetingIntents() {
        setUpService();
        User seller = user(SELLER_ID);
        User buyer = user(BUYER_ID);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller);
        PurchaseIntent intent = existingIntent(INTENT_ID, product, buyer, seller, LocalDateTime.now().plusHours(2));

        givenIntentDetail(product, item, intent);
        when(purchaseIntentRepository.findSentByProductIdForUpdate(PRODUCT_ID, PurchaseIntentStatus.SENT))
                .thenReturn(List.of());
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            ReflectionTestUtils.setField(trade, "id", 900L);
            return trade;
        });

        TradeDetailResponse response = purchaseIntentAcceptService.acceptPurchaseIntent(INTENT_ID, SELLER_ID);

        assertThat(response.tradeId()).isEqualTo(900L);
        assertThat(intent.getStatus()).isEqualTo(PurchaseIntentStatus.ACCEPTED);
    }

    @Test
    void acceptPurchaseIntent_expiresPastDeadlineIntentAndCommitsExpiration() {
        setUpService();
        User seller = user(SELLER_ID);
        User buyer = user(BUYER_ID);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller);
        PurchaseIntent intent = existingIntent(INTENT_ID, product, buyer, seller, LocalDateTime.now().minusMinutes(1));

        givenIntentDetail(product, item, intent);

        assertThatThrownBy(() -> purchaseIntentAcceptService.acceptPurchaseIntent(INTENT_ID, SELLER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseIntentErrorCode.PURCHASE_REQUEST_EXPIRED);

        assertThat(intent.getStatus()).isEqualTo(PurchaseIntentStatus.EXPIRED);
        assertThat(intent.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    @Test
    void acceptPurchaseIntent_rejectsNonSeller() {
        setUpService();
        User seller = user(SELLER_ID);
        User buyer = user(BUYER_ID);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller);
        PurchaseIntent intent = existingIntent(INTENT_ID, product, buyer, seller, LocalDateTime.now().plusHours(2));

        givenIntentDetail(product, item, intent);

        assertThatThrownBy(() -> purchaseIntentAcceptService.acceptPurchaseIntent(INTENT_ID, BUYER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseIntentErrorCode.PURCHASE_INTENT_ACCESS_DENIED);
    }

    @ParameterizedTest
    @EnumSource(value = PurchaseIntentStatus.class, names = {"REJECTED", "CANCELED", "ACCEPTED"})
    void acceptPurchaseIntent_rejectsNonSentStatus(PurchaseIntentStatus status) {
        setUpService();
        User seller = user(SELLER_ID);
        User buyer = user(BUYER_ID);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller);
        PurchaseIntent intent = existingIntent(INTENT_ID, product, buyer, seller, LocalDateTime.now().plusHours(2));
        ReflectionTestUtils.setField(intent, "status", status);

        givenIntentDetail(product, item, intent);

        assertThatThrownBy(() -> purchaseIntentAcceptService.acceptPurchaseIntent(INTENT_ID, SELLER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_SENT);
    }

    @Test
    void acceptPurchaseIntent_rejectsWhenProductNotOnSale() {
        setUpService();
        User seller = user(SELLER_ID);
        User buyer = user(BUYER_ID);
        Item item = item(ITEM_ID, seller);
        Product product = product(PRODUCT_ID, item, seller);
        ReflectionTestUtils.setField(product, "productStatus", ProductStatus.SOLD);
        PurchaseIntent intent = existingIntent(INTENT_ID, product, buyer, seller, LocalDateTime.now().plusHours(2));

        givenIntentDetail(product, item, intent);

        assertThatThrownBy(() -> purchaseIntentAcceptService.acceptPurchaseIntent(INTENT_ID, SELLER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_NOT_ON_SALE);

        assertThat(intent.getStatus()).isEqualTo(PurchaseIntentStatus.SENT);
    }

    private void givenIntentDetail(Product product, Item item, PurchaseIntent intent) {
        when(purchaseIntentRepository.findProductIdById(intent.getId())).thenReturn(Optional.of(PRODUCT_ID));
        when(productRepository.findByIdForUpdate(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseIntentRepository.findByIdForUpdate(intent.getId())).thenReturn(Optional.of(intent));
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
