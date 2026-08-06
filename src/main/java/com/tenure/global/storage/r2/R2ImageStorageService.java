package com.tenure.global.storage.r2;

import com.tenure.global.config.R2StorageProperties;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import com.tenure.global.storage.StoredImage;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@ConditionalOnProperty(name = "tenure.storage.type", havingValue = "r2")
@RequiredArgsConstructor
public class R2ImageStorageService implements ImageStorageService {

    private final S3Client r2S3Client;
    private final R2StorageProperties properties;

    @Override
    public StoredImage storeImage(MultipartFile file, String directory) {
        String objectKey = buildObjectKey(directory, file.getOriginalFilename());
        String contentType = file.getContentType();
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();
            r2S3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return new StoredImage(buildPublicUrl(objectKey), objectKey, contentType, file.getSize());
        } catch (IOException | S3Exception e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] readBytes(String objectKey) throws IOException {
        try {
            ResponseBytes<GetObjectResponse> bytes = r2S3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(objectKey)
                    .build());
            return bytes.asByteArray();
        } catch (S3Exception e) {
            throw new IOException("Failed to read R2 object: " + objectKey, e);
        }
    }

    @Override
    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        try {
            r2S3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(objectKey)
                    .build());
        } catch (S3Exception e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<String> objectKeyFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }
        String publicBaseUrl = stripTrailingSlash(properties.publicBaseUrl());
        if (!url.startsWith(publicBaseUrl + "/")) {
            return Optional.empty();
        }
        return Optional.of(url.substring(publicBaseUrl.length() + 1));
    }

    private String buildObjectKey(String directory, String originalFilename) {
        String normalizedDirectory = trimSlashes(directory);
        return normalizedDirectory + "/" + UUID.randomUUID() + extractExtension(originalFilename);
    }

    private String buildPublicUrl(String objectKey) {
        return stripTrailingSlash(properties.publicBaseUrl()) + "/" + objectKey;
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
    }

    private String trimSlashes(String value) {
        String result = value.replace('\\', '/');
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String stripTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
