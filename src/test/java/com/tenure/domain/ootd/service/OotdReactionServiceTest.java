package com.tenure.domain.ootd.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OotdReactionServiceTest {

    private static final Long CURRENT_USER_ID = 1L;
    private static final Long OOTD_ID = 100L;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private OotdReactionRepository ootdReactionRepository;

    @Mock
    private OotdReactionRecorder ootdReactionRecorder;

    private OotdReactionService ootdReactionService;

    @BeforeEach
    void setUp() {
        ootdReactionService = new OotdReactionService(ootdRepository, ootdReactionRepository, ootdReactionRecorder);
    }

    @Test
    void heartOotd_delegatesInsertToRecorderOnFirstRegistration() {
        User owner = user(2L);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, CURRENT_USER_ID, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdReactionRepository.existsByUser_IdAndOotd_IdAndReactionType(CURRENT_USER_ID, OOTD_ID, OotdReactionType.HEART))
                .thenReturn(false);

        ootdReactionService.heartOotd(CURRENT_USER_ID, OOTD_ID);

        verify(ootdReactionRecorder).insert(CURRENT_USER_ID, OOTD_ID, OotdReactionType.HEART);
    }

    @Test
    void heartOotd_skipsRecorderInsertWhenAlreadyHearted() {
        User owner = user(2L);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, CURRENT_USER_ID, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdReactionRepository.existsByUser_IdAndOotd_IdAndReactionType(CURRENT_USER_ID, OOTD_ID, OotdReactionType.HEART))
                .thenReturn(true);

        assertThatCode(() -> ootdReactionService.heartOotd(CURRENT_USER_ID, OOTD_ID))
                .doesNotThrowAnyException();

        verify(ootdReactionRecorder, never()).insert(CURRENT_USER_ID, OOTD_ID, OotdReactionType.HEART);
    }

    @Test
    void heartOotd_isIdempotentWhenRecorderThrowsUniqueConstraintViolationOnConcurrentRace() {
        User owner = user(2L);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findVisibleActiveById(OOTD_ID, CURRENT_USER_ID, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.of(ootd));
        when(ootdReactionRepository.existsByUser_IdAndOotd_IdAndReactionType(CURRENT_USER_ID, OOTD_ID, OotdReactionType.HEART))
                .thenReturn(false);
        doThrow(new DataIntegrityViolationException("uk_ootd_reactions_user_ootd_type"))
                .when(ootdReactionRecorder).insert(CURRENT_USER_ID, OOTD_ID, OotdReactionType.HEART);

        assertThatCode(() -> ootdReactionService.heartOotd(CURRENT_USER_ID, OOTD_ID))
                .doesNotThrowAnyException();
    }

    @Test
    void heartOotd_throwsNotFoundWhenOotdIsNotVisible() {
        when(ootdRepository.findVisibleActiveById(OOTD_ID, CURRENT_USER_ID, OotdPublicationStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdReactionService.heartOotd(CURRENT_USER_ID, OOTD_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OotdErrorCode.OOTD_NOT_FOUND);

        verify(ootdReactionRecorder, never()).insert(CURRENT_USER_ID, OOTD_ID, OotdReactionType.HEART);
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Ootd ootd(Long id, User owner) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "owner", owner);
        ReflectionTestUtils.setField(ootd, "heartCount", 0);
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
