package com.tenure.domain.ootd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.ootd.dto.OotdReactionListResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.entity.OotdReaction;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OotdReactionListServiceTest {

    private static final Long CURRENT_USER_ID = 1L;

    @Mock
    private OotdReactionRepository ootdReactionRepository;

    private OotdReactionListService ootdReactionListService;

    @BeforeEach
    void setUp() {
        ootdReactionListService = new OotdReactionListService(ootdReactionRepository);
    }

    @Test
    void getHeartedOotds_returnsHeartedOotdsInReactionCreatedAtOrderWithNextCursor() {
        User user = user(CURRENT_USER_ID);
        OotdReaction newest = reaction(30L, user, ootd(11L, "https://image.url/ootd-11.jpg"),
                LocalDateTime.of(2026, 7, 14, 10, 0));
        OotdReaction middle = reaction(20L, user, ootd(10L, "https://image.url/ootd-10.jpg"),
                LocalDateTime.of(2026, 7, 13, 10, 0));
        OotdReaction extra = reaction(10L, user, ootd(9L, "https://image.url/ootd-9.jpg"),
                LocalDateTime.of(2026, 7, 12, 10, 0));

        when(ootdReactionRepository.findReactedOotds(
                eq(CURRENT_USER_ID),
                eq(OotdReactionType.HEART),
                eq(OotdPublicationStatus.ACTIVE),
                eq(null),
                eq(null),
                any(Pageable.class)
        )).thenReturn(List.of(newest, middle, extra));

        OotdReactionListResponse response = ootdReactionListService.getHeartedOotds(CURRENT_USER_ID, null, null, 2);

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).ootdId()).isEqualTo(11L);
        assertThat(response.content().get(0).imageUrl()).isEqualTo("https://image.url/ootd-11.jpg");
        assertThat(response.content().get(1).ootdId()).isEqualTo(10L);
        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursorCreatedAt()).isEqualTo(middle.getCreatedAt());
        assertThat(response.nextCursorId()).isEqualTo(middle.getId());
    }

    @Test
    void getHeartedOotds_queriesOnlyActivePublicationStatusSoDeletedOrBlockedOotdsAreExcludedByTheQuery() {
        when(ootdReactionRepository.findReactedOotds(
                eq(CURRENT_USER_ID),
                eq(OotdReactionType.HEART),
                eq(OotdPublicationStatus.ACTIVE),
                eq(null),
                eq(null),
                any(Pageable.class)
        )).thenReturn(List.of());

        ootdReactionListService.getHeartedOotds(CURRENT_USER_ID, null, null, 20);

        // 삭제/차단 제외는 리포지토리 쿼리(publicationStatus + UserBlock not exists)에서 처리되므로,
        // 서비스는 ACTIVE 상태로만 조회를 위임하고 애플리케이션 레벨의 추가 필터링을 하지 않는다.
        verify(ootdReactionRepository).findReactedOotds(
                eq(CURRENT_USER_ID),
                eq(OotdReactionType.HEART),
                eq(OotdPublicationStatus.ACTIVE),
                eq(null),
                eq(null),
                any(Pageable.class)
        );
    }

    @Test
    void getHeartedOotds_returnsEmptyListWhenNoReactions() {
        when(ootdReactionRepository.findReactedOotds(
                eq(CURRENT_USER_ID),
                eq(OotdReactionType.HEART),
                eq(OotdPublicationStatus.ACTIVE),
                eq(null),
                eq(null),
                any(Pageable.class)
        )).thenReturn(List.of());

        OotdReactionListResponse response = ootdReactionListService.getHeartedOotds(CURRENT_USER_ID, null, null, 20);

        assertThat(response.content()).isEmpty();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursorCreatedAt()).isNull();
        assertThat(response.nextCursorId()).isNull();
    }

    @Test
    void getHeartedOotds_acceptsCursor() {
        LocalDateTime cursorCreatedAt = LocalDateTime.of(2026, 7, 14, 10, 0);
        when(ootdReactionRepository.findReactedOotds(
                eq(CURRENT_USER_ID),
                eq(OotdReactionType.HEART),
                eq(OotdPublicationStatus.ACTIVE),
                eq(cursorCreatedAt),
                eq(30L),
                any(Pageable.class)
        )).thenReturn(List.of());

        OotdReactionListResponse response =
                ootdReactionListService.getHeartedOotds(CURRENT_USER_ID, cursorCreatedAt, 30L, 20);

        assertThat(response.content()).isEmpty();
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    void getHeartedOotds_rejectsCursorWhenOnlyOneCursorFieldExists() {
        assertThatThrownBy(() -> ootdReactionListService.getHeartedOotds(
                CURRENT_USER_ID,
                LocalDateTime.of(2026, 7, 14, 10, 0),
                null,
                20
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);
    }

    @Test
    void getHeartedOotds_rejectsInvalidSize() {
        assertThatThrownBy(() -> ootdReactionListService.getHeartedOotds(CURRENT_USER_ID, null, null, 51))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Ootd ootd(Long id, String imageUrl) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "imageUrl", imageUrl);
        return ootd;
    }

    private OotdReaction reaction(Long id, User user, Ootd ootd, LocalDateTime createdAt) {
        OotdReaction reaction = instantiate(OotdReaction.class);
        ReflectionTestUtils.setField(reaction, "id", id);
        ReflectionTestUtils.setField(reaction, "user", user);
        ReflectionTestUtils.setField(reaction, "ootd", ootd);
        ReflectionTestUtils.setField(reaction, "reactionType", OotdReactionType.HEART);
        ReflectionTestUtils.setField(reaction, "createdAt", createdAt);
        return reaction;
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
