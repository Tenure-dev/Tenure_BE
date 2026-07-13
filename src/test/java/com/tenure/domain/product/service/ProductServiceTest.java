package com.tenure.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.product.dto.ProductCreateRequest;
import com.tenure.domain.product.dto.ProductCreateResponse;
import com.tenure.domain.product.dto.ProductDetailResponse;
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
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private ProductRepository productRepository;

    @Mock
    private ProductAttachedOotdRepository productAttachedOotdRepository;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private OotdTagRepository ootdTagRepository;

    @Mock
    private FollowRelationshipRepository followRelationshipRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                itemRepository,
                productRepository,
                productAttachedOotdRepository,
                ootdRepository,
                ootdTagRepository,
                followRelationshipRepository,
                new ObjectMapper()
        );
    }

    @Test
    void createProduct_changesOwnedItemToOnSale() {
        User seller = user(CURRENT_USER_ID, UserGrade.BASIC);
        Item item = item(ITEM_ID, seller, ItemStatus.OWNED);
        ProductCreateRequest request = request(FeePolicy.SELLER_PAYS, 0, List.of(100L, 101L));

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
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

        ProductDetailResponse response = productService.getProductDetail(200L, 999L);

        assertThat(response.viewerMode()).isEqualTo(ProductViewerMode.BUYER);
        assertThat(response.availableActions()).containsExactly(
                ProductAction.CHAT,
                ProductAction.PURCHASE,
                ProductAction.SHARE,
                ProductAction.REPORT
        );
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
        assertThat(response.conditionFlags()).containsExactly("STAIN");
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

    private ProductCreateRequest request(FeePolicy feePolicy, int shippingFee, List<Long> attachedOotdIds) {
        return new ProductCreateRequest(
                50000,
                shippingFee,
                feePolicy,
                "https://image.url/product.jpg",
                Map.of("shoulder", 45, "chest", 55, "totalLength", 70),
                List.of("NO_DEFECT"),
                "3회 착용했습니다.",
                attachedOotdIds
        );
    }

    private ProductUpdateRequest updateRequest(FeePolicy feePolicy, int shippingFee, List<Long> attachedOotdIds) {
        return new ProductUpdateRequest(
                52000,
                shippingFee,
                feePolicy,
                "https://image.url/product-updated.jpg",
                Map.of("shoulder", 46, "chest", 56, "totalLength", 71),
                List.of("STAIN"),
                "updated description",
                attachedOotdIds
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
        ReflectionTestUtils.setField(item, "brandName", "Nike");
        ReflectionTestUtils.setField(item, "itemName", "Black Jacket");
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
                "[\"NO_DEFECT\"]",
                "3회 착용했습니다."
        );
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "productStatus", status);
        return product;
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
