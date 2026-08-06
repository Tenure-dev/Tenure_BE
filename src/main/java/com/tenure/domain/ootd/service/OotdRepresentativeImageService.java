package com.tenure.domain.ootd.service;

import com.tenure.domain.ootd.dto.OotdRepresentativeImageRequest;
import com.tenure.domain.ootd.dto.OotdRepresentativeImageResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import com.tenure.global.storage.StoredImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OotdRepresentativeImageService {

    private static final String ITEM_IMAGE_DIRECTORY = "items";
    private static final String GENERATED_IMAGE_CONTENT_TYPE = "image/jpeg";
    private static final int OUTPUT_WIDTH = 900;
    private static final int OUTPUT_HEIGHT = 1200;
    private static final double ITEM_RATIO = 3.0 / 4.0;

    private final OotdRepository ootdRepository;
    private final ImageStorageService imageStorageService;

    @Transactional(readOnly = true)
    public OotdRepresentativeImageResponse createRepresentativeImage(
            Long currentUserId,
            Long ootdId,
            OotdRepresentativeImageRequest request
    ) {
        validateBbox(request.bbox());
        Ootd ootd = ootdRepository.findById(ootdId)
                .filter(found -> found.getPublicationStatus() != OotdPublicationStatus.DELETED)
                .orElseThrow(() -> new CustomException(OotdErrorCode.OOTD_NOT_FOUND));
        validateOwner(ootd, currentUserId);

        byte[] originalBytes = readOriginalBytes(ootd);
        byte[] representativeBytes = createRepresentativeImageBytes(originalBytes, request.bbox());
        StoredImage storedImage = imageStorageService.storeBytes(
                representativeBytes,
                ITEM_IMAGE_DIRECTORY,
                GENERATED_IMAGE_CONTENT_TYPE,
                "ootd-" + ootdId + "-representative.jpg"
        );
        return new OotdRepresentativeImageResponse(storedImage.url());
    }

    private byte[] readOriginalBytes(Ootd ootd) {
        String objectKey = ootd.getImageObjectKey();
        if (objectKey == null || objectKey.isBlank()) {
            objectKey = imageStorageService.objectKeyFromUrl(ootd.getImageUrl())
                    .orElseThrow(() -> new CustomException(CommonErrorCode.INVALID_REQUEST));
        }
        try {
            return imageStorageService.readBytes(objectKey);
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] createRepresentativeImageBytes(
            byte[] originalBytes,
            OotdRepresentativeImageRequest.BboxRequest bbox
    ) {
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (original == null) {
                throw new CustomException(CommonErrorCode.INVALID_REQUEST);
            }
            Rectangle bboxRect = toPixelRectangle(bbox, original.getWidth(), original.getHeight());
            Rectangle sourceRect = calculateSourceRectangle(bboxRect, original.getWidth(), original.getHeight());
            BufferedImage source = original.getSubimage(sourceRect.x(), sourceRect.y(), sourceRect.width(), sourceRect.height());
            BufferedImage output = drawToRepresentativeCanvas(source);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ImageIO.write(output, "jpg", buffer);
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private Rectangle toPixelRectangle(
            OotdRepresentativeImageRequest.BboxRequest bbox,
            int imageWidth,
            int imageHeight
    ) {
        int x = clamp(floorPixels(bbox.x(), imageWidth), 0, imageWidth - 1);
        int y = clamp(floorPixels(bbox.y(), imageHeight), 0, imageHeight - 1);
        int width = clamp(ceilPixels(bbox.width(), imageWidth), 1, imageWidth - x);
        int height = clamp(ceilPixels(bbox.height(), imageHeight), 1, imageHeight - y);
        return new Rectangle(x, y, width, height);
    }

    private Rectangle calculateSourceRectangle(Rectangle bbox, int imageWidth, int imageHeight) {
        int cropWidth = bbox.width();
        int cropHeight = bbox.height();
        if ((double) cropWidth / cropHeight > ITEM_RATIO) {
            cropHeight = (int) Math.ceil(cropWidth / ITEM_RATIO);
        } else {
            cropWidth = (int) Math.ceil(cropHeight * ITEM_RATIO);
        }

        if (cropWidth <= imageWidth && cropHeight <= imageHeight) {
            int centerX = bbox.x() + bbox.width() / 2;
            int centerY = bbox.y() + bbox.height() / 2;
            int x = clamp(centerX - cropWidth / 2, 0, imageWidth - cropWidth);
            int y = clamp(centerY - cropHeight / 2, 0, imageHeight - cropHeight);
            return new Rectangle(x, y, cropWidth, cropHeight);
        }

        return bbox;
    }

    private BufferedImage drawToRepresentativeCanvas(BufferedImage source) {
        BufferedImage output = new BufferedImage(OUTPUT_WIDTH, OUTPUT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = output.createGraphics();
        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, OUTPUT_WIDTH, OUTPUT_HEIGHT);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double scale = Math.min(
                    (double) OUTPUT_WIDTH / source.getWidth(),
                    (double) OUTPUT_HEIGHT / source.getHeight()
            );
            int drawWidth = Math.max(1, (int) Math.round(source.getWidth() * scale));
            int drawHeight = Math.max(1, (int) Math.round(source.getHeight() * scale));
            int drawX = (OUTPUT_WIDTH - drawWidth) / 2;
            int drawY = (OUTPUT_HEIGHT - drawHeight) / 2;
            graphics.drawImage(source, drawX, drawY, drawWidth, drawHeight, null);
            return output;
        } finally {
            graphics.dispose();
        }
    }

    private void validateOwner(Ootd ootd, Long currentUserId) {
        if (!ootd.getOwner().getId().equals(currentUserId)) {
            throw new CustomException(OotdErrorCode.OOTD_OWNER_ONLY);
        }
    }

    private void validateBbox(OotdRepresentativeImageRequest.BboxRequest bbox) {
        if (bbox.x().add(bbox.width()).compareTo(BigDecimal.ONE) > 0
                || bbox.y().add(bbox.height()).compareTo(BigDecimal.ONE) > 0) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
    }

    private int floorPixels(BigDecimal ratio, int total) {
        return ratio.multiply(BigDecimal.valueOf(total)).intValue();
    }

    private int ceilPixels(BigDecimal ratio, int total) {
        return (int) Math.ceil(ratio.multiply(BigDecimal.valueOf(total)).doubleValue());
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private record Rectangle(int x, int y, int width, int height) {
    }
}
