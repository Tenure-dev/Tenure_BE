package com.tenure.domain.ootd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.ootd.dto.OotdDetailResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OotdDetailServiceTest {

    private static final Long OOTD_ID = 100L;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private OotdTagRepository ootdTagRepository;

    @Mock
    private OotdReactionRepository ootdReactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FollowRelationshipRepository followRelationshipRepository;

    private OotdDetailService ootdDetailService;

    @BeforeEach
    void setUp() {
        ootdDetailService = new OotdDetailService(
                ootdRepository,
                ootdTagRepository,
                ootdReactionRepository,
                productRepository,
                followRelationshipRepository
        );
    }

    @Test
    void getOotdDetail_returnsDetailWithTagsHeartedAndSavedAndMixedOnSaleItems_withoutDedupingSameItemTags() {
        Long currentUserId = 1L;
        User owner = user(2L, AccountVisibility.PUBLIC);
        Ootd ootd = ootd(OOTD_ID, owner);
        Category outer = category(1L, "아우터", null);
        Category jacket = category(2L, "블루종", outer);
        Item onSaleItem = item(10L, jacket, "Nike", "Black Jacket");
        Item offSaleItem = item(20L, outer, "Adidas", "Windbreaker");
        OotdTag tag1 = tag(1L, ootd, onSaleItem);
        OotdTag tag2 = tag(2L, ootd, onSaleItem);
        OotdTag tag3 = tag(3L, ootd, offSaleItem);
        Product onSaleProduct = product(200L, onSaleItem, 50000, ProductStatus.ON_SALE, LocalDateTime.now());

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(tag1, tag2, tag3));
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.HEART))
                .thenReturn(Set.of(OOTD_ID));
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.SAVE))
                .thenReturn(Set.of());
        when(productRepository.findByItemIdIn(anyCollection()))
                .thenReturn(List.of(onSaleProduct));
        when(followRelationshipRepository.countByFollowing_IdAndStatus(2L, FollowStatus.ACCEPTED))
                .thenReturn(42L);
        when(ootdRepository.countByOwner_IdAndPublicationStatus(2L, OotdPublicationStatus.ACTIVE))
                .thenReturn(17L);

        OotdDetailResponse response = ootdDetailService.getOotdDetail(currentUserId, OOTD_ID);

        assertThat(response.ootdId()).isEqualTo(OOTD_ID);
        assertThat(response.author().userId()).isEqualTo(2L);
        assertThat(response.author().followerCount()).isEqualTo(42L);
        assertThat(response.author().feedCount()).isEqualTo(17L);
        assertThat(response.hearted()).isTrue();
        assertThat(response.saved()).isFalse();
        assertThat(response.tags()).hasSize(3);
        assertThat(response.tags().get(0).onSale()).isTrue();
        assertThat(response.tags().get(0).price()).isEqualTo(50000);
        assertThat(response.tags().get(0).itemStatus()).isEqualTo(ItemStatus.OWNED);
        assertThat(response.tags().get(0).purchaseOfferEnabled()).isTrue();
        assertThat(response.tags().get(0).productStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(response.tags().get(1).onSale()).isTrue();
        assertThat(response.tags().get(1).itemId()).isEqualTo(10L);
        assertThat(response.tags().get(2).onSale()).isFalse();
        assertThat(response.tags().get(2).price()).isNull();
        assertThat(response.tags().get(2).productStatus()).isNull();
        assertThat(response.tags().get(0).item().categoryLarge()).isEqualTo("아우터");
        assertThat(response.tags().get(0).item().categorySmall()).isEqualTo("블루종");
    }

    @Test
    void getOotdDetail_marksTagAsNotOnSaleWhenLatestProductIsSold() {
        Long currentUserId = 1L;
        User owner = user(2L, AccountVisibility.PUBLIC);
        Ootd ootd = ootd(OOTD_ID, owner);
        Category outer = category(1L, "아우터", null);
        Item soldItem = item(10L, outer, "Nike", "Black Jacket");
        OotdTag tag1 = tag(1L, ootd, soldItem);
        Product soldProduct = product(200L, soldItem, 50000, ProductStatus.SOLD, LocalDateTime.now());

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(tag1));
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.HEART))
                .thenReturn(Set.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.SAVE))
                .thenReturn(Set.of());
        when(productRepository.findByItemIdIn(anyCollection()))
                .thenReturn(List.of(soldProduct));
        when(followRelationshipRepository.countByFollowing_IdAndStatus(2L, FollowStatus.ACCEPTED))
                .thenReturn(0L);
        when(ootdRepository.countByOwner_IdAndPublicationStatus(2L, OotdPublicationStatus.ACTIVE))
                .thenReturn(0L);

        OotdDetailResponse response = ootdDetailService.getOotdDetail(currentUserId, OOTD_ID);

        assertThat(response.tags()).hasSize(1);
        assertThat(response.tags().get(0).onSale()).isFalse();
        assertThat(response.tags().get(0).price()).isNull();
        assertThat(response.tags().get(0).productStatus()).isEqualTo(ProductStatus.SOLD);
    }

    @Test
    void getOotdDetail_picksMostRecentProductByCreatedAtWhenItemHasMultipleProducts() {
        Long currentUserId = 1L;
        User owner = user(2L, AccountVisibility.PUBLIC);
        Ootd ootd = ootd(OOTD_ID, owner);
        Category outer = category(1L, "아우터", null);
        Item resoldItem = item(10L, outer, "Nike", "Black Jacket");
        OotdTag tag1 = tag(1L, ootd, resoldItem);
        Product olderSoldProduct = product(
                200L, resoldItem, 40000, ProductStatus.SOLD, LocalDateTime.now().minusDays(10)
        );
        Product newerOnSaleProduct = product(
                201L, resoldItem, 55000, ProductStatus.ON_SALE, LocalDateTime.now()
        );

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(tag1));
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.HEART))
                .thenReturn(Set.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.SAVE))
                .thenReturn(Set.of());
        when(productRepository.findByItemIdIn(anyCollection()))
                .thenReturn(List.of(olderSoldProduct, newerOnSaleProduct));
        when(followRelationshipRepository.countByFollowing_IdAndStatus(2L, FollowStatus.ACCEPTED))
                .thenReturn(0L);
        when(ootdRepository.countByOwner_IdAndPublicationStatus(2L, OotdPublicationStatus.ACTIVE))
                .thenReturn(0L);

        OotdDetailResponse response = ootdDetailService.getOotdDetail(currentUserId, OOTD_ID);

        assertThat(response.tags()).hasSize(1);
        assertThat(response.tags().get(0).onSale()).isTrue();
        assertThat(response.tags().get(0).price()).isEqualTo(55000);
        assertThat(response.tags().get(0).productStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    void getOotdDetail_returnsNullProductStatusWhenItemHasNoProduct() {
        Long currentUserId = 1L;
        User owner = user(2L, AccountVisibility.PUBLIC);
        Ootd ootd = ootd(OOTD_ID, owner);
        Category outer = category(1L, "아우터", null);
        Item neverListedItem = item(20L, outer, "Adidas", "Windbreaker");
        OotdTag tag1 = tag(1L, ootd, neverListedItem);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(tag1));
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.HEART))
                .thenReturn(Set.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.SAVE))
                .thenReturn(Set.of());
        when(productRepository.findByItemIdIn(anyCollection()))
                .thenReturn(List.of());
        when(followRelationshipRepository.countByFollowing_IdAndStatus(2L, FollowStatus.ACCEPTED))
                .thenReturn(0L);
        when(ootdRepository.countByOwner_IdAndPublicationStatus(2L, OotdPublicationStatus.ACTIVE))
                .thenReturn(0L);

        OotdDetailResponse response = ootdDetailService.getOotdDetail(currentUserId, OOTD_ID);

        assertThat(response.tags()).hasSize(1);
        assertThat(response.tags().get(0).onSale()).isFalse();
        assertThat(response.tags().get(0).price()).isNull();
        assertThat(response.tags().get(0).productStatus()).isNull();
        assertThat(response.tags().get(0).itemStatus()).isEqualTo(ItemStatus.OWNED);
        assertThat(response.tags().get(0).purchaseOfferEnabled()).isTrue();
    }

    @Test
    void getOotdDetail_returnsEmptyTagsAndSkipsProductLookupWhenNoConfirmedTags() {
        Long currentUserId = 1L;
        User owner = user(2L, AccountVisibility.PUBLIC);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.HEART))
                .thenReturn(Set.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.SAVE))
                .thenReturn(Set.of());

        OotdDetailResponse response = ootdDetailService.getOotdDetail(currentUserId, OOTD_ID);

        assertThat(response.tags()).isEmpty();
        verify(productRepository, never()).findByItemIdIn(anyCollection());
    }

    @Test
    void getOotdDetail_skipsVisibilityCheckForOwnPost() {
        Long currentUserId = 1L;
        User owner = user(currentUserId, AccountVisibility.PRIVATE);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.HEART))
                .thenReturn(Set.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.SAVE))
                .thenReturn(Set.of());

        OotdDetailResponse response = ootdDetailService.getOotdDetail(currentUserId, OOTD_ID);

        assertThat(response.ootdId()).isEqualTo(OOTD_ID);
        verify(followRelationshipRepository, never())
                .existsByFollower_IdAndFollowing_IdAndStatus(eq(currentUserId), eq(currentUserId), eq(FollowStatus.ACCEPTED));
    }

    @Test
    void getOotdDetail_rejectsPrivateOwnerWhenNoFollowRelationshipExists() {
        Long currentUserId = 1L;
        User owner = user(2L, AccountVisibility.PRIVATE);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(followRelationshipRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                currentUserId, owner.getId(), FollowStatus.ACCEPTED
        )).thenReturn(false);

        assertThatThrownBy(() -> ootdDetailService.getOotdDetail(currentUserId, OOTD_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OotdErrorCode.PRIVATE_OOTD_ACCESS_DENIED);
    }

    @Test
    void getOotdDetail_rejectsPrivateOwnerWhenFollowIsOnlyRequestedNotAccepted() {
        Long currentUserId = 1L;
        User owner = user(2L, AccountVisibility.PRIVATE);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(followRelationshipRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                currentUserId, owner.getId(), FollowStatus.ACCEPTED
        )).thenReturn(false);

        assertThatThrownBy(() -> ootdDetailService.getOotdDetail(currentUserId, OOTD_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OotdErrorCode.PRIVATE_OOTD_ACCESS_DENIED);
    }

    @Test
    void getOotdDetail_allowsPrivateOwnerWhenFollowIsAccepted() {
        Long currentUserId = 1L;
        User owner = user(2L, AccountVisibility.PRIVATE);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(followRelationshipRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                currentUserId, owner.getId(), FollowStatus.ACCEPTED
        )).thenReturn(true);
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.HEART))
                .thenReturn(Set.of());
        when(ootdReactionRepository.findReactedOotdIds(currentUserId, List.of(OOTD_ID), OotdReactionType.SAVE))
                .thenReturn(Set.of());

        OotdDetailResponse response = ootdDetailService.getOotdDetail(currentUserId, OOTD_ID);

        assertThat(response.ootdId()).isEqualTo(OOTD_ID);
    }

    @Test
    void getOotdDetail_throwsNotFoundWhenOotdDoesNotExist() {
        Long currentUserId = 1L;

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdDetailService.getOotdDetail(currentUserId, OOTD_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OotdErrorCode.OOTD_NOT_FOUND);
    }

    @Test
    void getOotdDetail_throwsNotFoundWhenOotdIsArchived() {
        Long currentUserId = 1L;

        when(ootdRepository.findVisibleActiveById(OOTD_ID, currentUserId, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdDetailService.getOotdDetail(currentUserId, OOTD_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OotdErrorCode.OOTD_NOT_FOUND);
    }

    private User user(Long id, AccountVisibility accountVisibility) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "username", "owner" + id);
        ReflectionTestUtils.setField(user, "profileImageUrl", "https://image.url/profile-" + id + ".jpg");
        ReflectionTestUtils.setField(user, "accountVisibility", accountVisibility);
        return user;
    }

    private Category category(Long id, String name, Category parent) {
        Category category = instantiate(Category.class);
        ReflectionTestUtils.setField(category, "id", id);
        ReflectionTestUtils.setField(category, "name", name);
        ReflectionTestUtils.setField(category, "parent", parent);
        return category;
    }

    private Item item(Long id, Category category, String brandName, String itemName) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "category", category);
        ReflectionTestUtils.setField(item, "brandName", brandName);
        ReflectionTestUtils.setField(item, "itemName", itemName);
        return item;
    }

    private Ootd ootd(Long id, User owner) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "owner", owner);
        ReflectionTestUtils.setField(ootd, "imageUrl", "https://image.url/ootd-" + id + ".jpg");
        ReflectionTestUtils.setField(ootd, "heartCount", 12);
        ReflectionTestUtils.setField(ootd, "saveCount", 4);
        ReflectionTestUtils.setField(ootd, "viewCount", 120);
        return ootd;
    }

    private OotdTag tag(Long id, Ootd ootd, Item item) {
        OotdTag tag = instantiate(OotdTag.class);
        ReflectionTestUtils.setField(tag, "id", id);
        ReflectionTestUtils.setField(tag, "ootd", ootd);
        ReflectionTestUtils.setField(tag, "item", item);
        ReflectionTestUtils.setField(tag, "labelText", "label-" + id);
        ReflectionTestUtils.setField(tag, "bboxX", new BigDecimal("0.10000"));
        ReflectionTestUtils.setField(tag, "bboxY", new BigDecimal("0.20000"));
        ReflectionTestUtils.setField(tag, "bboxWidth", new BigDecimal("0.30000"));
        ReflectionTestUtils.setField(tag, "bboxHeight", new BigDecimal("0.40000"));
        return tag;
    }

    private Product product(Long id, Item item, Integer price, ProductStatus productStatus, LocalDateTime createdAt) {
        Product product = instantiate(Product.class);
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "item", item);
        ReflectionTestUtils.setField(product, "price", price);
        ReflectionTestUtils.setField(product, "productStatus", productStatus);
        ReflectionTestUtils.setField(product, "createdAt", createdAt);
        return product;
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
