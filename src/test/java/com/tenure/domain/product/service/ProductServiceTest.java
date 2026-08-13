package com.tenure.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.entity.ItemHistory;
import com.tenure.domain.item.enums.EndReason;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.enums.WearingTarget;
import com.tenure.domain.item.repository.CategoryRepository;
import com.tenure.domain.item.repository.ItemHistoryRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationType;
import com.tenure.domain.notification.service.NotificationFactory;
import com.tenure.domain.notification.service.NotificationService;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.product.dto.ProductCreateRequest;
import com.tenure.domain.product.dto.ProductConditionFlags;
import com.tenure.domain.product.dto.ProductCreateResponse;
import com.tenure.domain.product.dto.ProductDeleteResponse;
import com.tenure.domain.product.dto.ProductDetailResponse;
import com.tenure.domain.product.dto.ProductExternalCompleteResponse;
import com.tenure.domain.product.dto.ProductMeasurements;
import com.tenure.domain.product.dto.ProductUpdateRequest;
import com.tenure.domain.product.dto.ProductUpdateResponse;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.entity.ProductAttachedOotd;
import com.tenure.domain.product.enums.ProductAction;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.enums.ProductViewerMode;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductAttachedOotdRepository;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.product.repository.ProductReportRepository;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.domain.wish.repository.WishRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
class ProductServiceTest {

    private static final Long CURRENT_USER_ID = 1L;
    private static final Long ITEM_ID = 10L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemHistoryRepository itemHistoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductAttachedOotdRepository productAttachedOotdRepository;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private OotdTagRepository ootdTagRepository;

    @Mock
    private FollowRelationshipRepository followRelationshipRepository;

    @Mock
    private PurchaseIntentRepository purchaseIntentRepository;

    @Mock
    private PurchaseOfferRepository purchaseOfferRepository;

    @Mock
    private WishRepository wishRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductReportRepository productReportRepository;

    @Mock
    private ImageStorageService imageStorageService;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                itemRepository,
                itemHistoryRepository,
                categoryRepository,
                productRepository,
                productAttachedOotdRepository,
                ootdRepository,
                ootdTagRepository,
                followRelationshipRepository,
                purchaseIntentRepository,
                purchaseOfferRepository,
                wishRepository,
                new NotificationFactory(),
                notificationService,
                new ObjectMapper(),
                userRepository,
                productReportRepository,
                imageStorageService
        );
    }

    @Test
    void createProduct_changesOwnedItemToOnSale() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.OWNED);
        ProductCreateRequest request = request(FeePolicy.SELLER_PAYS, 0, List.of(100L, 101L));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        stubCategoryLookup();
        when(ootdTagRepository.countValidProductAttachedOotds(
                eq(ITEM_ID),
                eq(CURRENT_USER_ID),
                anyCollection(),
                eq(OotdPublicationStatus.ACTIVE),
                eq(TagStatus.CONFIRMED)
        )).thenReturn(2L);
        when(ootdRepository.findAllById(request.attachedOotdIds()))
                .thenReturn(List.of(ootd(100L), ootd(101L)));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            ReflectionTestUtils.setField(product, "id", 200L);
            return product;
        });

        ProductCreateResponse response = productService.createProduct(ITEM_ID, CURRENT_USER_ID, request);

        assertThat(response.productId()).isEqualTo(200L);
        assertThat(response.itemStatus()).isEqualTo(ItemStatus.ON_SALE);
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.ON_SALE);
        assertThat(item.getBrandName()).isEqualTo("Levis");
        assertThat(item.getItemName()).isEqualTo("LVC 1955 501");
        assertThat(item.getWearingTarget()).isEqualTo(WearingTarget.UNISEX);
        assertThat(item.getFirstOwnedAt()).isEqualTo(LocalDate.of(2025, 10, 1));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ProductAttachedOotd>> captor = ArgumentCaptor.forClass(List.class);
        verify(productAttachedOotdRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void createProduct_rejectsBasicUserNonSellerPaysFeePolicy() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.OWNED);
        ProductCreateRequest request = request(FeePolicy.BUYER_PAYS, 0, List.of(100L));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> productService.createProduct(ITEM_ID, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.BASIC_USER_FEE_POLICY_INVALID);
    }

    @Test
    void createProduct_rejectsBasicUserShippingFee() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.OWNED);
        ProductCreateRequest request = request(FeePolicy.SELLER_PAYS, 3000, List.of(100L));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> productService.createProduct(ITEM_ID, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.BASIC_USER_SHIPPING_FEE_INVALID);
    }

    @Test
    void createProduct_rejectsNonOwnerItem() {
        User seller = user(2L, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.OWNED);
        ProductCreateRequest request = request(FeePolicy.SELLER_PAYS, 0, List.of(100L));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> productService.createProduct(ITEM_ID, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_OWNER_ONLY);
    }

    @Test
    void createProduct_rejectsInvalidAttachedOotd() {
        User seller = user(CURRENT_USER_ID, UserGrade.RECORD);
        Item item = item(ITEM_ID, seller, ItemStatus.OWNED);
        ProductCreateRequest request = request(FeePolicy.BUYER_PAYS, 3000, List.of(100L, 101L));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        stubCategoryLookup();
        when(ootdTagRepository.countValidProductAttachedOotds(
                eq(ITEM_ID),
                eq(CURRENT_USER_ID),
                anyCollection(),
                eq(OotdPublicationStatus.ACTIVE),
                eq(TagStatus.CONFIRMED)
        )).thenReturn(1L);

        assertThatThrownBy(() -> productService.createProduct(ITEM_ID, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.ATTACHED_OOTD_INVALID);
    }

    @Test
    void createProduct_rejectsMeasurementsForOtherCategory() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.OWNED);
        ProductCreateRequest request = requestWithMeasurements(
                FeePolicy.SELLER_PAYS,
                0,
                List.of(100L),
                topMeasurements()
        );

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        stubCategoryLookup();

        assertThatThrownBy(() -> productService.createProduct(ITEM_ID, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_MEASUREMENTS_INVALID);
    }

    @Test
    void getProductDetail_returnsSellerModeAndSellerActionsForOwner() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        ReflectionTestUtils.setField(seller, "accountVisibility", AccountVisibility.PRIVATE);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        ReflectionTestUtils.setField(item, "category", category(1L, "블루종", category(2L, "아우터", null)));
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);

        when(productRepository.findDetailById(200L)).thenReturn(Optional.of(product));
        when(productAttachedOotdRepository.findActiveByProductIdOrderByOotdCreatedAtDesc(
                200L,
                OotdPublicationStatus.ACTIVE
        )).thenReturn(List.of(attachedOotd(product, ootd(100L))));

        ProductDetailResponse response = productService.getProductDetail(200L, CURRENT_USER_ID);

        assertThat(response.viewerMode()).isEqualTo(ProductViewerMode.SELLER);
        assertThat(response.availableActions()).containsExactly(
                ProductAction.EDIT,
                ProductAction.DELETE,
                ProductAction.MARK_SOLD
        );
        assertThat(response.item().categoryLarge()).isEqualTo("아우터");
        assertThat(response.item().categorySmall()).isEqualTo("블루종");
        assertThat(response.representativeOotds()).hasSize(1);
    }

    @Test
    void getProductDetail_returnsBuyerModeAndBuyerActionsForPublicSeller() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        ReflectionTestUtils.setField(seller, "accountVisibility", AccountVisibility.PUBLIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        ReflectionTestUtils.setField(item, "category", category(1L, "아우터", null));
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);

        when(productRepository.findDetailById(200L)).thenReturn(Optional.of(product));
        when(productAttachedOotdRepository.findActiveByProductIdOrderByOotdCreatedAtDesc(
                200L,
                OotdPublicationStatus.ACTIVE
        )).thenReturn(List.of());
        when(wishRepository.existsByUserIdAndItemId(999L, ITEM_ID)).thenReturn(true);

        ProductDetailResponse response = productService.getProductDetail(200L, 999L);

        assertThat(response.viewerMode()).isEqualTo(ProductViewerMode.BUYER);
        assertThat(response.availableActions()).containsExactly(
                ProductAction.CHAT,
                ProductAction.PURCHASE,
                ProductAction.SHARE,
                ProductAction.REPORT
        );
        assertThat(response.item().wished()).isTrue();
    }

    @Test
    void getProductDetail_rejectsPrivateSellerForNotAcceptedFollower() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        ReflectionTestUtils.setField(seller, "accountVisibility", AccountVisibility.PRIVATE);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        ReflectionTestUtils.setField(item, "category", category(1L, "아우터", null));
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);

        when(productRepository.findDetailById(200L)).thenReturn(Optional.of(product));
        when(followRelationshipRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                999L,
                CURRENT_USER_ID,
                FollowStatus.ACCEPTED
        )).thenReturn(false);

        assertThatThrownBy(() -> productService.getProductDetail(200L, 999L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRIVATE_PRODUCT_ACCESS_DENIED);
    }

    @Test
    void updateProduct_updatesOnSaleProductAndReplacesAttachedOotds() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);
        ProductUpdateRequest request = updateRequest(FeePolicy.SELLER_PAYS, 0, List.of(100L, 101L));

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));
        when(ootdTagRepository.countValidProductAttachedOotds(
                eq(ITEM_ID),
                eq(CURRENT_USER_ID),
                anyCollection(),
                eq(OotdPublicationStatus.ACTIVE),
                eq(TagStatus.CONFIRMED)
        )).thenReturn(2L);
        when(ootdRepository.findAllById(request.attachedOotdIds()))
                .thenReturn(List.of(ootd(100L), ootd(101L)));

        ProductUpdateResponse response = productService.updateProduct(200L, CURRENT_USER_ID, request);

        assertThat(response.productId()).isEqualTo(200L);
        assertThat(response.status()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(response.price()).isEqualTo(52000);
        assertThat(response.shippingFee()).isZero();
        assertThat(response.feePolicy()).isEqualTo(FeePolicy.SELLER_PAYS);
        assertThat(response.conditionFlags().stain()).isTrue();
        assertThat(response.conditionFlags().tear()).isFalse();
        assertThat(response.attachedOotdIds()).containsExactly(100L, 101L);
        assertThat(product.getPrice()).isEqualTo(52000);
        assertThat(product.getSellerDescription()).isEqualTo("updated description");

        verify(productAttachedOotdRepository).deleteByProductId(200L);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ProductAttachedOotd>> captor = ArgumentCaptor.forClass(List.class);
        verify(productAttachedOotdRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void updateProduct_updatesItemInfoWhenItemFieldsAreProvided() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);
        String originalSellerDescription = product.getSellerDescription();
        ProductUpdateRequest request = updateRequestWithItemInfo();

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));
        stubCategoryLookup();
        when(productAttachedOotdRepository.findByProductIdOrderByOotdCreatedAtDesc(200L))
                .thenReturn(List.of());

        ProductUpdateResponse response = productService.updateProduct(200L, CURRENT_USER_ID, request);

        assertThat(response.productId()).isEqualTo(200L);
        assertThat(item.getBrandName()).isEqualTo("Levis");
        assertThat(item.getItemName()).isEqualTo("LVC 1955 501");
        assertThat(item.getWearingTarget()).isEqualTo(WearingTarget.UNISEX);
        assertThat(item.getSizeValue()).isEqualTo("L");
        assertThat(item.getFirstOwnedAt()).isEqualTo(LocalDate.of(2025, 10, 1));
        assertThat(response.price()).isEqualTo(50000);
        assertThat(response.shippingFee()).isZero();
        assertThat(response.feePolicy()).isEqualTo(FeePolicy.SELLER_PAYS);
        assertThat(product.getPrice()).isEqualTo(50000);
        assertThat(product.getShippingFee()).isZero();
        assertThat(product.getSellerDescription()).isEqualTo(originalSellerDescription);
    }

    @Test
    void updateProduct_rejectsSingleCategoryField() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(
                200L,
                CURRENT_USER_ID,
                updateRequestWithOnlyCategoryLarge()
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);
    }

    @Test
    void updateProduct_rejectsNonSeller() {
        User seller = user(2L, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(
                200L,
                CURRENT_USER_ID,
                updateRequest(FeePolicy.SELLER_PAYS, 0, List.of(100L))
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_OWNER_ONLY);
    }

    @Test
    void updateProduct_rejectsNonOnSaleProduct() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.SOLD);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(
                200L,
                CURRENT_USER_ID,
                updateRequest(FeePolicy.SELLER_PAYS, 0, List.of(100L))
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_ITEM_STATUS_INVALID);
    }

    @Test
    void updateProduct_rejectsInvalidAttachedOotd() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);
        ProductUpdateRequest request = updateRequest(FeePolicy.SELLER_PAYS, 0, List.of(100L, 101L));

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));
        when(ootdTagRepository.countValidProductAttachedOotds(
                eq(ITEM_ID),
                eq(CURRENT_USER_ID),
                anyCollection(),
                eq(OotdPublicationStatus.ACTIVE),
                eq(TagStatus.CONFIRMED)
        )).thenReturn(1L);

        assertThatThrownBy(() -> productService.updateProduct(200L, CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.ATTACHED_OOTD_INVALID);
    }

    @Test
    void completeExternalProduct_marksProductAndItemSoldAndCancelsPendingRequests() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);
        PurchaseIntent intent = purchaseIntent(300L);
        PurchaseOffer offer = purchaseOffer(400L);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseIntentRepository.findSentByProductIdForUpdate(200L, PurchaseIntentStatus.SENT))
                .thenReturn(List.of(intent));
        when(purchaseOfferRepository.findSentByItemIdForUpdate(ITEM_ID, PurchaseOfferStatus.SENT))
                .thenReturn(List.of(offer));
        ItemHistory openHistory = ItemHistory.ofFirstRegistration(item, seller, LocalDateTime.now().minusDays(10));
        when(itemHistoryRepository.findByItemIdAndEndedAtIsNull(ITEM_ID)).thenReturn(Optional.of(openHistory));

        ProductExternalCompleteResponse response = productService.completeExternalProduct(200L, CURRENT_USER_ID);

        assertThat(response.productId()).isEqualTo(200L);
        assertThat(response.itemId()).isEqualTo(ITEM_ID);
        assertThat(response.productStatus()).isEqualTo(ProductStatus.SOLD);
        assertThat(response.itemStatus()).isEqualTo(ItemStatus.SOLD);
        assertThat(response.canceledIntentCount()).isOne();
        assertThat(response.canceledOfferCount()).isOne();
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.SOLD);
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.SOLD);
        assertThat(intent.getStatus()).isEqualTo(PurchaseIntentStatus.CANCELED);
        assertThat(intent.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.CANCELED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        // SOLD 아이템은 열린 행 0개가 정상이다: 기존 열린 행을 닫기만 하고 새 열린 행은 만들지 않는다.
        assertThat(openHistory.getEndReason()).isEqualTo(EndReason.EXTERNAL_SALE);
        assertThat(openHistory.getEndedAt()).isNotNull();
        verify(itemHistoryRepository, never()).save(any(ItemHistory.class));

        InOrder inOrder = inOrder(
                productRepository,
                itemRepository,
                purchaseIntentRepository,
                purchaseOfferRepository
        );
        inOrder.verify(productRepository).findByIdForUpdate(200L);
        inOrder.verify(itemRepository).findByIdForUpdate(ITEM_ID);
        inOrder.verify(purchaseIntentRepository).findSentByProductIdForUpdate(200L, PurchaseIntentStatus.SENT);
        inOrder.verify(purchaseOfferRepository).findSentByItemIdForUpdate(ITEM_ID, PurchaseOfferStatus.SENT);
    }

    @Test
    void completeExternalProduct_rejectsNonSeller() {
        User seller = user(2L, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> productService.completeExternalProduct(200L, CURRENT_USER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_OWNER_ONLY);
    }

    @Test
    void completeExternalProduct_rejectsNonOnSaleProduct() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.SOLD);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> productService.completeExternalProduct(200L, CURRENT_USER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_ITEM_STATUS_INVALID);
    }

    @Test
    void deleteProduct_hidesOnSaleProductAndRevertsItemToOwned() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));

        ProductDeleteResponse response = productService.deleteProduct(200L, CURRENT_USER_ID);

        assertThat(response.productId()).isEqualTo(200L);
        assertThat(response.itemId()).isEqualTo(ITEM_ID);
        assertThat(response.productStatus()).isEqualTo(ProductStatus.HIDDEN);
        assertThat(response.itemStatus()).isEqualTo(ItemStatus.OWNED);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.HIDDEN);
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.OWNED);
    }

    @Test
    void deleteProduct_rejectsNonSeller() {
        User seller = user(2L, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.ON_SALE);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.deleteProduct(200L, CURRENT_USER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_OWNER_ONLY);
    }

    @Test
    void deleteProduct_rejectsNonOnSaleProduct() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        Product product = product(200L, item, seller, ProductStatus.SOLD);

        when(productRepository.findByIdForUpdate(200L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.deleteProduct(200L, CURRENT_USER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ProductErrorCode.PRODUCT_ITEM_STATUS_INVALID);
    }

    @Test
    void notifyWishersTradingStarted_savesTradingStartedNotificationsExcludingBuyer() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);
        User wisher1 = user(50L, UserGrade.BASIC);
        User wisher2 = user(60L, UserGrade.BASIC);
        User buyer = user(70L, UserGrade.BASIC);

        when(wishRepository.findNotificationReceiversByItemId(ITEM_ID))
                .thenReturn(List.of(wisher1, wisher2, buyer));

        productService.notifyWishersTradingStarted(item, 70L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationService).saveAll(captor.capture());
        List<Notification> notifications = captor.getValue();
        assertThat(notifications).hasSize(2);
        assertThat(notifications)
                .extracting(notification -> notification.getReceiver().getId())
                .containsExactlyInAnyOrder(50L, 60L);
        assertThat(notifications)
                .extracting(Notification::getType)
                .containsOnly(NotificationType.PRODUCT_TRADING_STARTED);
        assertThat(notifications)
                .extracting(Notification::getTargetId)
                .containsOnly(ITEM_ID);
    }

    @Test
    void notifyWishersTradingStarted_doesNothingWhenNoWishers() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.ON_SALE);

        when(wishRepository.findNotificationReceiversByItemId(ITEM_ID)).thenReturn(List.of());

        productService.notifyWishersTradingStarted(item, 70L);

        verify(notificationService, never()).saveAll(any());
    }

    private ProductCreateRequest request(FeePolicy feePolicy, int shippingFee, List<Long> attachedOotdIds) {
        return requestWithMeasurements(feePolicy, shippingFee, attachedOotdIds, bottomMeasurements());
    }

    private ProductCreateRequest requestWithMeasurements(
            FeePolicy feePolicy,
            int shippingFee,
            List<Long> attachedOotdIds,
            ProductMeasurements measurements
    ) {
        return new ProductCreateRequest(
                "Levis",
                "LVC 1955 501",
                "하의",
                "데님",
                WearingTarget.UNISEX,
                "KR",
                "L",
                LocalDate.of(2025, 10, 1),
                "https://image.url/item.jpg",
                50000,
                shippingFee,
                feePolicy,
                "https://image.url/product.jpg",
                measurements,
                ProductConditionFlags.empty(),
                "3회 착용했습니다.",
                attachedOotdIds
        );
    }

    private ProductUpdateRequest updateRequest(FeePolicy feePolicy, int shippingFee, List<Long> attachedOotdIds) {
        return new ProductUpdateRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                52000,
                shippingFee,
                feePolicy,
                "https://image.url/product-updated.jpg",
                bottomMeasurements(),
                new ProductConditionFlags(true, false, false, false, false),
                "updated description",
                attachedOotdIds
        );
    }

    private ProductUpdateRequest updateRequestWithItemInfo() {
        return new ProductUpdateRequest(
                "Levis",
                "LVC 1955 501",
                "하의",
                "데님",
                WearingTarget.UNISEX,
                "KR",
                "L",
                LocalDate.of(2025, 10, 1),
                "https://image.url/item.jpg",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private ProductUpdateRequest updateRequestWithOnlyCategoryLarge() {
        return new ProductUpdateRequest(
                null,
                null,
                "하의",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private ProductMeasurements bottomMeasurements() {
        return new ProductMeasurements(
                null,
                null,
                null,
                new BigDecimal("100"),
                new BigDecimal("38"),
                new BigDecimal("30"),
                new BigDecimal("28"),
                new BigDecimal("73"),
                new BigDecimal("20"),
                null
        );
    }

    private ProductMeasurements topMeasurements() {
        return new ProductMeasurements(
                new BigDecimal("45"),
                new BigDecimal("55"),
                new BigDecimal("60"),
                new BigDecimal("70"),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private User user(Long id, UserGrade grade) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", grade);
        ReflectionTestUtils.setField(user, "username", "YuJin");
        ReflectionTestUtils.setField(user, "accountVisibility", AccountVisibility.PUBLIC);
        return user;
    }

    private Item item(Long id, User owner, ItemStatus itemStatus) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "owner", owner);
        ReflectionTestUtils.setField(item, "itemStatus", itemStatus);
        ReflectionTestUtils.setField(item, "category", category(1L, "Jackets", category(2L, "Outer", null)));
        ReflectionTestUtils.setField(item, "brandName", "Nike");
        ReflectionTestUtils.setField(item, "itemName", "Black Jacket");
        ReflectionTestUtils.setField(item, "wearingTarget", WearingTarget.UNISEX);
        ReflectionTestUtils.setField(item, "sizeSystem", "KR");
        ReflectionTestUtils.setField(item, "sizeValue", "100");
        ReflectionTestUtils.setField(item, "ootdVerifiedWearCount", 3);
        ReflectionTestUtils.setField(item, "wishCount", 12);
        return item;
    }

    private Ootd ootd(Long id) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "imageUrl", "https://image.url/ootd.jpg");
        ReflectionTestUtils.setField(ootd, "createdAt", LocalDateTime.of(2026, 7, 10, 12, 0));
        return ootd;
    }

    private Category category(Long id, String name, Category parent) {
        Category category = instantiate(Category.class);
        ReflectionTestUtils.setField(category, "id", id);
        ReflectionTestUtils.setField(category, "name", name);
        ReflectionTestUtils.setField(category, "parent", parent);
        return category;
    }

    private void stubCategoryLookup() {
        Category largeCategory = category(10L, "하의", null);
        Category smallCategory = category(11L, "데님", largeCategory);
        when(categoryRepository.findByNameAndDepthAndIsActiveTrue("하의", 1))
                .thenReturn(Optional.of(largeCategory));
        when(categoryRepository.findByNameAndParentAndDepthAndIsActiveTrue("데님", largeCategory, 2))
                .thenReturn(Optional.of(smallCategory));
    }

    private Product product(Long id, Item item, User seller, ProductStatus status) {
        Product product = Product.create(
                item,
                seller,
                50000,
                0,
                FeePolicy.SELLER_PAYS,
                new BigDecimal("0.0600"),
                "https://image.url/product.jpg",
                "{\"shoulder\":45,\"chest\":55,\"totalLength\":70}",
                "{\"stain\":false,\"tear\":false}",
                "3회 착용했습니다."
        );
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "productStatus", status);
        return product;
    }

    private PurchaseIntent purchaseIntent(Long id) {
        PurchaseIntent intent = instantiate(PurchaseIntent.class);
        ReflectionTestUtils.setField(intent, "id", id);
        ReflectionTestUtils.setField(intent, "status", PurchaseIntentStatus.SENT);
        ReflectionTestUtils.setField(intent, "paymentAuthorizationStatus", PaymentAuthorizationStatus.AUTHORIZED);
        return intent;
    }

    private PurchaseOffer purchaseOffer(Long id) {
        PurchaseOffer offer = instantiate(PurchaseOffer.class);
        ReflectionTestUtils.setField(offer, "id", id);
        ReflectionTestUtils.setField(offer, "status", PurchaseOfferStatus.SENT);
        ReflectionTestUtils.setField(offer, "paymentAuthorizationStatus", PaymentAuthorizationStatus.AUTHORIZED);
        return offer;
    }

    private ProductAttachedOotd attachedOotd(Product product, Ootd ootd) {
        return ProductAttachedOotd.create(product, ootd);
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
