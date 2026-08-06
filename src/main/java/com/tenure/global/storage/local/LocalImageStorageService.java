package com.tenure.global.storage.local;

import com.tenure.global.config.StorageProperties;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import com.tenure.global.storage.StoredImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "tenure.storage.type", havingValue = "local", matchIfMissing = true)
@RequiredArgsConstructor
public class LocalImageStorageService implements ImageStorageService {

    private final StorageProperties storageProperties;

    @Override
    public StoredImage storeImage(MultipartFile file, String directory) {
        try {
            String objectKey = buildObjectKey(directory, file.getOriginalFilename());
            Path target = Path.of(storageProperties.baseDir(), objectKey);
            Files.createDirectories(target.getParent());
            file.transferTo(target);
            return storedImage(objectKey, file.getContentType(), file.getSize());
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public StoredImage storeBytes(byte[] bytes, String directory, String contentType, String originalFilename) {
        try {
            String objectKey = buildObjectKey(directory, originalFilename);
            Path target = Path.of(storageProperties.baseDir(), objectKey);
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
            return storedImage(objectKey, contentType, bytes.length);
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] readBytes(String objectKey) throws IOException {
        return Files.readAllBytes(Path.of(storageProperties.baseDir(), objectKey));
    }

    @Override
    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Path.of(storageProperties.baseDir(), objectKey));
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<String> objectKeyFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }
        String baseUrl = stripTrailingSlash(storageProperties.baseUrl());
        if (!url.startsWith(baseUrl + "/")) {
            return Optional.empty();
        }
        return Optional.of(url.substring(baseUrl.length() + 1));
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private String buildObjectKey(String directory, String originalFilename) {
        return normalizeKey(directory + "/" + UUID.randomUUID() + extractExtension(originalFilename));
    }

    private StoredImage storedImage(String objectKey, String contentType, long size) {
        return new StoredImage(
                stripTrailingSlash(storageProperties.baseUrl()) + "/" + objectKey,
                objectKey,
                contentType,
                size
        );
    }

    private String normalizeKey(String key) {
        return key.replace('\\', '/');
    }

    private String stripTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
