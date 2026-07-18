package com.tenure.domain.ootd.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.entity.OotdReaction;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class OotdReactionRecorderTest {

    private static final Long USER_ID = 1L;
    private static final Long OOTD_ID = 100L;

    @Mock
    private OotdReactionRepository ootdReactionRepository;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private UserRepository userRepository;

    private OotdReactionRecorder ootdReactionRecorder;

    @BeforeEach
    void setUp() {
        ootdReactionRecorder = new OotdReactionRecorder(ootdReactionRepository, ootdRepository, userRepository);
    }

    @Test
    void insert_savesReactionThenIncreasesHeartCountInThatOrder() {
        User userRef = instantiate(User.class);
        Ootd ootdRef = instantiate(Ootd.class);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(userRef);
        when(ootdRepository.getReferenceById(OOTD_ID)).thenReturn(ootdRef);
        when(ootdReactionRepository.saveAndFlush(any(OotdReaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ootdReactionRecorder.insert(USER_ID, OOTD_ID, OotdReactionType.HEART);

        InOrder inOrder = Mockito.inOrder(ootdReactionRepository, ootdRepository);
        inOrder.verify(ootdReactionRepository).saveAndFlush(any(OotdReaction.class));
        inOrder.verify(ootdRepository).increaseHeartCount(OOTD_ID);
    }

    @Test
    void insert_doesNotIncreaseHeartCountAndPropagatesExceptionWhenUniqueConstraintViolated() {
        User userRef = instantiate(User.class);
        Ootd ootdRef = instantiate(Ootd.class);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(userRef);
        when(ootdRepository.getReferenceById(OOTD_ID)).thenReturn(ootdRef);
        when(ootdReactionRepository.saveAndFlush(any(OotdReaction.class)))
                .thenThrow(new DataIntegrityViolationException("uk_ootd_reactions_user_ootd_type"));

        assertThatThrownBy(() -> ootdReactionRecorder.insert(USER_ID, OOTD_ID, OotdReactionType.HEART))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(ootdRepository, never()).increaseHeartCount(OOTD_ID);
    }

    @Test
    void insert_savesReactionThenIncreasesSaveCountInThatOrder() {
        User userRef = instantiate(User.class);
        Ootd ootdRef = instantiate(Ootd.class);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(userRef);
        when(ootdRepository.getReferenceById(OOTD_ID)).thenReturn(ootdRef);
        when(ootdReactionRepository.saveAndFlush(any(OotdReaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ootdReactionRecorder.insert(USER_ID, OOTD_ID, OotdReactionType.SAVE);

        InOrder inOrder = Mockito.inOrder(ootdReactionRepository, ootdRepository);
        inOrder.verify(ootdReactionRepository).saveAndFlush(any(OotdReaction.class));
        inOrder.verify(ootdRepository).increaseSaveCount(OOTD_ID);
        verify(ootdRepository, never()).increaseHeartCount(OOTD_ID);
    }

    @Test
    void insert_doesNotIncreaseSaveCountAndPropagatesExceptionWhenUniqueConstraintViolated() {
        User userRef = instantiate(User.class);
        Ootd ootdRef = instantiate(Ootd.class);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(userRef);
        when(ootdRepository.getReferenceById(OOTD_ID)).thenReturn(ootdRef);
        when(ootdReactionRepository.saveAndFlush(any(OotdReaction.class)))
                .thenThrow(new DataIntegrityViolationException("uk_ootd_reactions_user_ootd_type"));

        assertThatThrownBy(() -> ootdReactionRecorder.insert(USER_ID, OOTD_ID, OotdReactionType.SAVE))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(ootdRepository, never()).increaseSaveCount(OOTD_ID);
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
