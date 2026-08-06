package com.tenure.global.storage;

public record StoredImage(
        String url,
        String objectKey,
        String contentType,
        long size
) {
}
