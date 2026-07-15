package com.tenure.domain.ootd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.ootd.dto.OotdRelatedResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OotdRelatedServiceTest {

    private static final Long CURRENT_USER_ID = 1L;
    private static final Long OOTD_ID = 100L;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private OotdTagRepository ootdTagRepository;

    private OotdRelatedService ootdRelatedService;

    @BeforeEach
    void setUp() {
        ootdRelatedService = new OotdRelatedService(ootdRepository, ootdTagRepository);
    }

    @Test
    void getRelatedOotds_returnsSimpleRuleBasedSections() {
        User owner = user(2L, "seller");
        Ootd source = ootd(OOTD_ID, owner, LocalDateTime.of(2026, 7, 14, 12, 0));
        Category category = category(5L, "Outer");
        Item item = item(10L, owner, category, "Nike", "Black Jacket");
        OotdTag tag = tag(1L, source, item);

        Ootd similar = ootd(101L, owner, LocalDateTime.of(2026, 7, 13, 12, 0));
        Ootd sameItem = ootd(102L, user(3L, "buyer"), LocalDateTime.of(2026, 7, 12, 12, 0));
        Ootd categoryMatch = ootd(103L, user(4L, "user4"), LocalDateTime.of(2026, 7, 11, 12, 0));
        Ootd latestFallback = ootd(104L, user(5L, "user5"), LocalDateTime.of(2026, 7, 10, 12, 0));

        when(ootdRepository.findVisibleActiveById(OOTD_ID, CURRENT_USER_ID, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(source));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(tag));
        when(ootdRepository.findRelatedBySameOwner(
                eq(CURRENT_USER_ID),
                eq(owner.getId()),
                eq(OOTD_ID),
                eq(OotdPublicationStatus.ACTIVE),
                any(Pageable.class)
        )).thenReturn(List.of(similar));
        when(ootdTagRepository.findRelatedOotdsByItemId(
                eq(item.getId()),
                eq(CURRENT_USER_ID),
                eq(OOTD_ID),
                eq(OotdPublicationStatus.ACTIVE),
                eq(TagStatus.CONFIRMED),
                any(Pageable.class)
        )).thenReturn(List.of(sameItem));
        when(ootdTagRepository.findRelatedOotdsByCategoryIds(
                anyCollection(),
                eq(CURRENT_USER_ID),
                anyCollection(),
                eq(OotdPublicationStatus.ACTIVE),
                eq(TagStatus.CONFIRMED),
                any(Pageable.class)
        )).thenReturn(List.of(categoryMatch));
        when(ootdRepository.findLatestVisible(
                eq(CURRENT_USER_ID),
                anyCollection(),
                eq(OotdPublicationStatus.ACTIVE),
                any(Pageable.class)
        )).thenReturn(List.of(latestFallback));

        OotdRelatedResponse response = ootdRelatedService.getRelatedOotds(CURRENT_USER_ID, OOTD_ID);

        assertThat(response.similarMood()).extracting("ootdId").containsExactly(101L);
        assertThat(response.sameItems()).hasSize(1);
        assertThat(response.sameItems().get(0).itemId()).isEqualTo(10L);
        assertThat(response.sameItems().get(0).brandName()).isEqualTo("Nike");
        assertThat(response.sameItems().get(0).ootds()).extracting("ootdId").containsExactly(102L);
        assertThat(response.recommended()).extracting("ootdId").containsExactly(103L, 104L);
    }

    @Test
    void getRelatedOotds_returnsEmptySectionsWhenNoTagsAndNoRecommendations() {
        User owner = user(2L, "seller");
        Ootd source = ootd(OOTD_ID, owner, LocalDateTime.of(2026, 7, 14, 12, 0));

        when(ootdRepository.findVisibleActiveById(OOTD_ID, CURRENT_USER_ID, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(source));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of());
        when(ootdRepository.findRelatedBySameOwner(
                eq(CURRENT_USER_ID),
                eq(owner.getId()),
                eq(OOTD_ID),
                eq(OotdPublicationStatus.ACTIVE),
                any(Pageable.class)
        )).thenReturn(List.of());
        when(ootdRepository.findLatestVisible(
                eq(CURRENT_USER_ID),
                anyCollection(),
                eq(OotdPublicationStatus.ACTIVE),
                any(Pageable.class)
        )).thenReturn(List.of());

        OotdRelatedResponse response = ootdRelatedService.getRelatedOotds(CURRENT_USER_ID, OOTD_ID);

        assertThat(response.similarMood()).isEmpty();
        assertThat(response.sameItems()).isEmpty();
        assertThat(response.recommended()).isEmpty();
    }

    @Test
    void getRelatedOotds_throwsWhenSourceOotdIsNotVisible() {
        when(ootdRepository.findVisibleActiveById(OOTD_ID, CURRENT_USER_ID, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdRelatedService.getRelatedOotds(CURRENT_USER_ID, OOTD_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.NOT_FOUND);
    }

    private User user(Long id, String username) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "username", username);
        ReflectionTestUtils.setField(user, "profileImageUrl", "https://image.url/profile-" + id + ".jpg");
        return user;
    }

    private Category category(Long id, String name) {
        Category category = instantiate(Category.class);
        ReflectionTestUtils.setField(category, "id", id);
        ReflectionTestUtils.setField(category, "name", name);
        return category;
    }

    private Item item(Long id, User owner, Category category, String brandName, String itemName) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "owner", owner);
        ReflectionTestUtils.setField(item, "category", category);
        ReflectionTestUtils.setField(item, "brandName", brandName);
        ReflectionTestUtils.setField(item, "itemName", itemName);
        return item;
    }

    private Ootd ootd(Long id, User owner, LocalDateTime createdAt) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "owner", owner);
        ReflectionTestUtils.setField(ootd, "imageUrl", "https://image.url/ootd-" + id + ".jpg");
        ReflectionTestUtils.setField(ootd, "createdAt", createdAt);
        return ootd;
    }

    private OotdTag tag(Long id, Ootd ootd, Item item) {
        OotdTag tag = instantiate(OotdTag.class);
        ReflectionTestUtils.setField(tag, "id", id);
        ReflectionTestUtils.setField(tag, "ootd", ootd);
        ReflectionTestUtils.setField(tag, "item", item);
        return tag;
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
