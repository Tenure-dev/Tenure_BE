package com.tenure.domain.ootd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.ootd.dto.OotdCreateResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import java.lang.reflect.Constructor;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class OotdServiceTest {

    private static final Long CURRENT_USER_ID = 1L;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageStorageService imageStorageService;

    private OotdService ootdService;

    @BeforeEach
    void setUp() {
        ootdService = new OotdService(ootdRepository, userRepository, imageStorageService);
    }

    @Test
    void createOotd_savesOotdOnSuccess() {
        User owner = user(CURRENT_USER_ID);
        MultipartFile image = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "content".getBytes());

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(owner));
        when(imageStorageService.store(eq(image), anyString())).thenReturn("/files/ootds/photo.jpg");

        OotdCreateResponse response = ootdService.createOotd(CURRENT_USER_ID, image, "CAMERA");

        assertThat(response.imageUrl()).isEqualTo("/files/ootds/photo.jpg");
        assertThat(response.ownerId()).isEqualTo(CURRENT_USER_ID);

        verify(ootdRepository).save(any(Ootd.class));
    }

    @Test
    void createOotd_rejectsMissingImage() {
        MultipartFile emptyImage = new MockMultipartFile("image", new byte[0]);

        assertThatThrownBy(() -> ootdService.createOotd(CURRENT_USER_ID, emptyImage, "CAMERA"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OotdErrorCode.OOTD_IMAGE_REQUIRED);
    }

    @Test
    void createOotd_rejectsNonCameraSource() {
        MultipartFile image = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "content".getBytes());

        assertThatThrownBy(() -> ootdService.createOotd(CURRENT_USER_ID, image, "GALLERY"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OotdErrorCode.OOTD_SOURCE_INVALID);
    }

    @Test
    void createOotd_rejectsUnknownUser() {
        MultipartFile image = new MockMultipartFile("image", "photo.jpg", "image/jpeg", "content".getBytes());

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdService.createOotd(CURRENT_USER_ID, image, "CAMERA"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.UNAUTHORIZED);
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
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
