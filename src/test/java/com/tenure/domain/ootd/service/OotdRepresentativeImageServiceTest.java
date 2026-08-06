package com.tenure.domain.ootd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tenure.domain.ootd.dto.OotdRepresentativeImageRequest;
import com.tenure.domain.ootd.dto.OotdRepresentativeImageResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import com.tenure.global.storage.StoredImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OotdRepresentativeImageServiceTest {

    private static final Long OWNER_ID = 1L;
    private static final Long OOTD_ID = 10L;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private ImageStorageService imageStorageService;

    private OotdRepresentativeImageService service;

    @BeforeEach
    void setUp() {
        service = new OotdRepresentativeImageService(ootdRepository, imageStorageService);
    }

    @Test
    void createRepresentativeImage_createsThreeToFourJpegFromOotdImageAndBbox() throws Exception {
        Ootd ootd = ootd(OOTD_ID, user(OWNER_ID), "ootds/original.jpg");
        byte[] originalBytes = jpegBytes(1200, 1600);
        OotdRepresentativeImageRequest request = request("0.20", "0.20", "0.30", "0.30");

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(imageStorageService.readBytes("ootds/original.jpg")).thenReturn(originalBytes);
        when(imageStorageService.storeBytes(any(byte[].class), eq("items"), eq("image/jpeg"), eq("ootd-10-representative.jpg")))
                .thenReturn(new StoredImage("/files/items/generated.jpg", "items/generated.jpg", "image/jpeg", 100L));

        OotdRepresentativeImageResponse response = service.createRepresentativeImage(OWNER_ID, OOTD_ID, request);

        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);
        org.mockito.Mockito.verify(imageStorageService)
                .storeBytes(bytesCaptor.capture(), eq("items"), eq("image/jpeg"), eq("ootd-10-representative.jpg"));
        BufferedImage generated = ImageIO.read(new ByteArrayInputStream(bytesCaptor.getValue()));

        assertThat(response.representativeImageUrl()).isEqualTo("/files/items/generated.jpg");
        assertThat(generated.getWidth()).isEqualTo(900);
        assertThat(generated.getHeight()).isEqualTo(1200);
    }

    @Test
    void createRepresentativeImage_rejectsNonOwner() {
        Ootd ootd = ootd(OOTD_ID, user(OWNER_ID), "ootds/original.jpg");
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        assertThatThrownBy(() -> service.createRepresentativeImage(999L, OOTD_ID, request("0.1", "0.1", "0.2", "0.2")))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OotdErrorCode.OOTD_OWNER_ONLY);
    }

    private OotdRepresentativeImageRequest request(String x, String y, String width, String height) {
        return new OotdRepresentativeImageRequest(new OotdRepresentativeImageRequest.BboxRequest(
                new BigDecimal(x),
                new BigDecimal(y),
                new BigDecimal(width),
                new BigDecimal(height)
        ));
    }

    private byte[] jpegBytes(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(Color.LIGHT_GRAY);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.DARK_GRAY);
            graphics.fillRect(width / 4, height / 4, width / 2, height / 2);
        } finally {
            graphics.dispose();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", output);
        return output.toByteArray();
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Ootd ootd(Long id, User owner, String objectKey) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "owner", owner);
        ReflectionTestUtils.setField(ootd, "imageUrl", "/files/" + objectKey);
        ReflectionTestUtils.setField(ootd, "imageObjectKey", objectKey);
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
