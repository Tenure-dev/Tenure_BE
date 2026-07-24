package com.tenure.domain.ootd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tenure.domain.ootd.dto.OotdMyPostsResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.enums.OotdTagStatus;
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
class OotdMyPostServiceTest {

    private static final Long CURRENT_USER_ID = 1L;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private OotdReactionRepository ootdReactionRepository;

    private OotdMyPostService ootdMyPostService;

    @BeforeEach
    void setUp() {
        ootdMyPostService = new OotdMyPostService(ootdRepository, ootdReactionRepository);
    }

    @Test
    void getMyPosts_returnsActiveAndArchivedPostsWithNextCursor() {
        User owner = user(CURRENT_USER_ID);
        Ootd active = ootd(
                11L,
                owner,
                OotdPublicationStatus.ACTIVE,
                OotdTagStatus.CONFIRMED,
                LocalDateTime.of(2026, 7, 14, 10, 0)
        );
        Ootd archived = ootd(
                10L,
                owner,
                OotdPublicationStatus.ARCHIVED,
                OotdTagStatus.AUTO_UNCONFIRMED,
                LocalDateTime.of(2026, 7, 13, 10, 0)
        );
        Ootd extra = ootd(
                9L,
                owner,
                OotdPublicationStatus.ACTIVE,
                OotdTagStatus.ANALYZING,
                LocalDateTime.of(2026, 7, 12, 10, 0)
        );

        when(ootdRepository.findMyPostsFirstPage(
                eq(CURRENT_USER_ID),
                any(Pageable.class)
        )).thenReturn(List.of(active, archived, extra));
        when(ootdReactionRepository.findReactedOotdIds(
                eq(CURRENT_USER_ID),
                eq(List.of(11L, 10L)),
                eq(OotdReactionType.HEART)
        )).thenReturn(Set.of(11L));
        when(ootdReactionRepository.findReactedOotdIds(
                eq(CURRENT_USER_ID),
                eq(List.of(11L, 10L)),
                eq(OotdReactionType.SAVE)
        )).thenReturn(Set.of(10L));

        OotdMyPostsResponse response = ootdMyPostService.getMyPosts(CURRENT_USER_ID, null, null, 2);

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).ootdId()).isEqualTo(11L);
        assertThat(response.content().get(0).publicationStatus()).isEqualTo(OotdPublicationStatus.ACTIVE);
        assertThat(response.content().get(0).archived()).isFalse();
        assertThat(response.content().get(0).hearted()).isTrue();
        assertThat(response.content().get(0).saved()).isFalse();
        assertThat(response.content().get(1).ootdId()).isEqualTo(10L);
        assertThat(response.content().get(1).publicationStatus()).isEqualTo(OotdPublicationStatus.ARCHIVED);
        assertThat(response.content().get(1).archived()).isTrue();
        assertThat(response.content().get(1).reviewRequired()).isTrue();
        assertThat(response.content().get(1).hearted()).isFalse();
        assertThat(response.content().get(1).saved()).isTrue();
        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursorCreatedAt()).isEqualTo(archived.getCreatedAt());
        assertThat(response.nextCursorId()).isEqualTo(archived.getId());
    }

    @Test
    void getMyPosts_acceptsCursor() {
        LocalDateTime cursorCreatedAt = LocalDateTime.of(2026, 7, 14, 10, 0);
        when(ootdRepository.findMyPosts(
                eq(CURRENT_USER_ID),
                eq(cursorCreatedAt),
                eq(11L),
                any(Pageable.class)
        )).thenReturn(List.of());

        OotdMyPostsResponse response = ootdMyPostService.getMyPosts(CURRENT_USER_ID, cursorCreatedAt, 11L, 20);

        assertThat(response.content()).isEmpty();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursorCreatedAt()).isNull();
        assertThat(response.nextCursorId()).isNull();
    }

    @Test
    void getMyPosts_rejectsCursorWhenOnlyOneCursorFieldExists() {
        assertThatThrownBy(() -> ootdMyPostService.getMyPosts(
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
    void getMyPosts_rejectsInvalidSize() {
        assertThatThrownBy(() -> ootdMyPostService.getMyPosts(CURRENT_USER_ID, null, null, 51))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Ootd ootd(
            Long id,
            User owner,
            OotdPublicationStatus publicationStatus,
            OotdTagStatus tagStatus,
            LocalDateTime createdAt
    ) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "owner", owner);
        ReflectionTestUtils.setField(ootd, "imageUrl", "https://image.url/ootd-" + id + ".jpg");
        ReflectionTestUtils.setField(ootd, "tagStatus", tagStatus);
        ReflectionTestUtils.setField(ootd, "publicationStatus", publicationStatus);
        ReflectionTestUtils.setField(ootd, "reviewRequired", publicationStatus == OotdPublicationStatus.ARCHIVED);
        ReflectionTestUtils.setField(ootd, "reviewDeadlineAt", LocalDateTime.of(2026, 7, 17, 10, 0));
        ReflectionTestUtils.setField(ootd, "archivedAt", publicationStatus == OotdPublicationStatus.ARCHIVED
                ? LocalDateTime.of(2026, 7, 17, 10, 0)
                : null);
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
