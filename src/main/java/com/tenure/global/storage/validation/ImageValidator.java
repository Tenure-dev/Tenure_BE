package com.tenure.global.storage.validation;

import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageValidator {

    private static final long MAX_IMAGE_BYTES = 10L * 1024L * 1024L;
    private static final double ITEM_RATIO = 3.0 / 4.0;
    private static final double ITEM_RATIO_TOLERANCE = 0.01;
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public ImageMetadata validateGeneralImage(MultipartFile image) {
        return validate(image, false);
    }

    public ImageMetadata validateItemRepresentativeImage(MultipartFile image) {
        return validate(image, true);
    }

    private ImageMetadata validate(MultipartFile image, boolean requireThreeToFourRatio) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        if (image.getSize() > MAX_IMAGE_BYTES) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        String declaredContentType = image.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(declaredContentType)) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }

        byte[] bytes = readBytes(image);
        ImageMetadata metadata = detect(bytes);
        if (!declaredContentType.equals(metadata.contentType())) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        if (requireThreeToFourRatio && !isThreeToFour(metadata.width(), metadata.height())) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        return metadata;
    }

    private byte[] readBytes(MultipartFile image) {
        try {
            return image.getBytes();
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
    }

    private ImageMetadata detect(byte[] bytes) {
        if (isJpeg(bytes)) {
            return readWithImageIo(bytes, "image/jpeg");
        }
        if (isPng(bytes)) {
            return readWithImageIo(bytes, "image/png");
        }
        if (isWebp(bytes)) {
            return readWebp(bytes);
        }
        throw new CustomException(CommonErrorCode.INVALID_REQUEST);
    }

    private boolean isJpeg(byte[] bytes) {
        return bytes.length >= 3
                && unsigned(bytes[0]) == 0xFF
                && unsigned(bytes[1]) == 0xD8
                && unsigned(bytes[2]) == 0xFF;
    }

    private boolean isPng(byte[] bytes) {
        return bytes.length >= 8
                && unsigned(bytes[0]) == 0x89
                && bytes[1] == 'P'
                && bytes[2] == 'N'
                && bytes[3] == 'G'
                && unsigned(bytes[4]) == 0x0D
                && unsigned(bytes[5]) == 0x0A
                && unsigned(bytes[6]) == 0x1A
                && unsigned(bytes[7]) == 0x0A;
    }

    private boolean isWebp(byte[] bytes) {
        return bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P';
    }

    private ImageMetadata readWithImageIo(byte[] bytes, String contentType) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                throw new CustomException(CommonErrorCode.INVALID_REQUEST);
            }
            return new ImageMetadata(contentType, image.getWidth(), image.getHeight());
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
    }

    private ImageMetadata readWebp(byte[] bytes) {
        if (containsChunk(bytes, "ANIM")) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        int offset = 12;
        while (offset + 8 <= bytes.length) {
            String chunkType = ascii(bytes, offset, 4);
            int chunkSize = littleEndianInt(bytes, offset + 4);
            int payload = offset + 8;
            if (chunkSize < 0 || payload + chunkSize > bytes.length) {
                throw new CustomException(CommonErrorCode.INVALID_REQUEST);
            }
            if ("VP8 ".equals(chunkType)) {
                return readLossyWebp(bytes, payload, chunkSize);
            }
            if ("VP8L".equals(chunkType)) {
                return readLosslessWebp(bytes, payload, chunkSize);
            }
            if ("VP8X".equals(chunkType)) {
                return readExtendedWebp(bytes, payload, chunkSize);
            }
            offset = payload + chunkSize + (chunkSize % 2);
        }
        throw new CustomException(CommonErrorCode.INVALID_REQUEST);
    }

    private ImageMetadata readLossyWebp(byte[] bytes, int payload, int chunkSize) {
        if (chunkSize < 10
                || unsigned(bytes[payload + 3]) != 0x9D
                || unsigned(bytes[payload + 4]) != 0x01
                || unsigned(bytes[payload + 5]) != 0x2A) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        int width = littleEndianShort(bytes, payload + 6) & 0x3FFF;
        int height = littleEndianShort(bytes, payload + 8) & 0x3FFF;
        return new ImageMetadata("image/webp", width, height);
    }

    private ImageMetadata readLosslessWebp(byte[] bytes, int payload, int chunkSize) {
        if (chunkSize < 5 || unsigned(bytes[payload]) != 0x2F) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        int b1 = unsigned(bytes[payload + 1]);
        int b2 = unsigned(bytes[payload + 2]);
        int b3 = unsigned(bytes[payload + 3]);
        int b4 = unsigned(bytes[payload + 4]);
        int width = 1 + (((b2 & 0x3F) << 8) | b1);
        int height = 1 + (((b4 & 0x0F) << 10) | (b3 << 2) | ((b2 & 0xC0) >> 6));
        return new ImageMetadata("image/webp", width, height);
    }

    private ImageMetadata readExtendedWebp(byte[] bytes, int payload, int chunkSize) {
        if (chunkSize < 10) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        int flags = unsigned(bytes[payload]);
        if ((flags & 0x02) != 0) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        int width = 1 + littleEndian24(bytes, payload + 4);
        int height = 1 + littleEndian24(bytes, payload + 7);
        return new ImageMetadata("image/webp", width, height);
    }

    private boolean containsChunk(byte[] bytes, String targetChunkType) {
        int offset = 12;
        while (offset + 8 <= bytes.length) {
            String chunkType = ascii(bytes, offset, 4);
            int chunkSize = littleEndianInt(bytes, offset + 4);
            int payload = offset + 8;
            if (chunkSize < 0 || payload + chunkSize > bytes.length) {
                return false;
            }
            if (targetChunkType.equals(chunkType)) {
                return true;
            }
            offset = payload + chunkSize + (chunkSize % 2);
        }
        return false;
    }

    private boolean isThreeToFour(int width, int height) {
        if (width <= 0 || height <= 0) {
            return false;
        }
        return Math.abs(((double) width / (double) height) - ITEM_RATIO) <= ITEM_RATIO_TOLERANCE;
    }

    private int unsigned(byte value) {
        return value & 0xFF;
    }

    private String ascii(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, java.nio.charset.StandardCharsets.US_ASCII);
    }

    private int littleEndianInt(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private int littleEndianShort(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
    }

    private int littleEndian24(byte[] bytes, int offset) {
        return unsigned(bytes[offset])
                | (unsigned(bytes[offset + 1]) << 8)
                | (unsigned(bytes[offset + 2]) << 16);
    }
}
