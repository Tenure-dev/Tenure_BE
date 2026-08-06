package com.tenure.global.storage;

import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    default String store(MultipartFile file, String directory) {
        return storeImage(file, directory).url();
    }

    StoredImage storeImage(MultipartFile file, String directory);

    byte[] readBytes(String objectKey) throws IOException;

    void delete(String objectKey);

    Optional<String> objectKeyFromUrl(String url);
}
