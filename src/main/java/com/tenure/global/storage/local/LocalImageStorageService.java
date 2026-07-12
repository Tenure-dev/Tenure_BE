package com.tenure.global.storage.local;

import com.tenure.global.config.StorageProperties;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LocalImageStorageService implements ImageStorageService {

    private final StorageProperties storageProperties;

    @Override
    public String store(MultipartFile file, String directory) {
        try {
            Path targetDir = Path.of(storageProperties.baseDir(), directory);
            Files.createDirectories(targetDir);

            String fileName = UUID.randomUUID() + extractExtension(file.getOriginalFilename());
            file.transferTo(targetDir.resolve(fileName));

            return storageProperties.baseUrl() + "/" + directory + "/" + fileName;
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
