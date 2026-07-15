package com.tenure.domain.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.feed.dto.FeedResponse;
import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    private static final Long CURRENT_USER_ID = 1L;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private OotdReactionRepository ootdReactionRepository;

    @Mock
    private FollowRelationshipRepository followRelationshipRepository;

    private FeedService feedService;

    @BeforeEach
    void setUp() {
        feedService = new FeedService(
                ootdRepository,
                ootdReactionRepository,
                followRelationshipRepository
        );
    }

    @Test
    void getFeed_returnsAllFeedWithReactionFlagsAndNextCursor() {
        User owner = user(2L, "sujun");
        Ootd first = ootd(11L, owner, LocalDateTime.of(2026, 7, 14, 10, 0));
        Ootd second = ootd(10L, owner, LocalDateTime.of(2026, 7, 14, 9, 0));
        Ootd extra = ootd(9L, owner, LocalDateTime.of(2026, 7, 14, 8, 0));

        when(ootdRepository.findFeed(
                eq(CURRENT_USER_ID),
                eq(false),
                eq(null),
                eq(OotdPublicationStatus.ACTIVE),
                eq(FollowStatus.ACCEPTED),
                eq(null),
                eq(null),
                any(Pageable.class)
        )).thenReturn(List.of(first, second, extra));
        when(ootdReactionRepository.findReactedOotdIds(
                CURRENT_USER_ID,
                List.of(11L, 10L),
                OotdReactionType.HEART
        )).thenReturn(Set.of(11L));
        when(ootdReactionRepository.findReactedOotdIds(
                CURRENT_USER_ID,
                List.of(11L, 10L),
                OotdReactionType.SAVE
        )).thenReturn(Set.of(10L));

        FeedResponse response = feedService.getFeed(CURRENT_USER_ID, "all", null, null, null, 2);

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).ootdId()).isEqualTo(11L);
        assertThat(response.content().get(0).hearted()).isTrue();
        assertThat(response.content().get(0).saved()).isFalse();
        assertThat(response.content().get(1).ootdId()).isEqualTo(10L);
        assertThat(response.content().get(1).hearted()).isFalse();
        assertThat(response.content().get(1).saved()).isTrue();
        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursorCreatedAt()).isEqualTo(second.getCreatedAt());
        assertThat(response.nextCursorId()).isEqualTo(second.getId());
    }

    @Test
    void getFeed_acceptsFollowingUserFilterForAcceptedFollowing() {
        Long followingUserId = 2L;
        when(followRelationshipRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                CURRENT_USER_ID,
                followingUserId,
                FollowStatus.ACCEPTED
        )).thenReturn(true);
        when(ootdRepository.findFeed(
                eq(CURRENT_USER_ID),
                eq(true),
                eq(followingUserId),
                eq(OotdPublicationStatus.ACTIVE),
                eq(FollowStatus.ACCEPTED),
                eq(null),
                eq(null),
                any(Pageable.class)
        )).thenReturn(List.of());

        FeedResponse response = feedService.getFeed(CURRENT_USER_ID, "following", followingUserId, null, null, 20);

        assertThat(response.content()).isEmpty();
        assertThat(response.hasNext()).isFalse();
        verify(followRelationshipRepository).existsByFollower_IdAndFollowing_IdAndStatus(
                CURRENT_USER_ID,
                followingUserId,
                FollowStatus.ACCEPTED
        );
    }

    @Test
    void getFeed_rejectsUserFilterOnAllTab() {
        assertThatThrownBy(() -> feedService.getFeed(CURRENT_USER_ID, "all", 2L, null, null, 20))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);
    }

    @Test
    void getFeed_rejectsCursorWhenOnlyOneCursorFieldExists() {
        assertThatThrownBy(() -> feedService.getFeed(
                CURRENT_USER_ID,
                "all",
                null,
                LocalDateTime.of(2026, 7, 14, 10, 0),
                null,
                20
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);
    }

    private User user(Long id, String username) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "username", username);
        ReflectionTestUtils.setField(user, "profileImageUrl", "https://image.url/profile.jpg");
        return user;
    }

    private Ootd ootd(Long id, User owner, LocalDateTime createdAt) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "owner", owner);
        ReflectionTestUtils.setField(ootd, "imageUrl", "https://image.url/ootd-" + id + ".jpg");
        ReflectionTestUtils.setField(ootd, "heartCount", 3);
        ReflectionTestUtils.setField(ootd, "saveCount", 2);
        ReflectionTestUtils.setField(ootd, "createdAt", createdAt);
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
