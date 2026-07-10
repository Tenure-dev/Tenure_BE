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
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.product.dto.ProductCreateRequest;
import com.tenure.domain.product.dto.ProductCreateResponse;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.entity.ProductAttachedOotd;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductAttachedOotdRepository;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
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

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                itemRepository,
                productRepository,
                productAttachedOotdRepository,
                ootdRepository,
                ootdTagRepository,
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

    private User user(Long id, UserGrade grade) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", grade);
        return user;
    }

    private Item item(Long id, User owner, ItemStatus itemStatus) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "owner", owner);
        ReflectionTestUtils.setField(item, "itemStatus", itemStatus);
        return item;
    }

    private Ootd ootd(Long id) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        return ootd;
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
