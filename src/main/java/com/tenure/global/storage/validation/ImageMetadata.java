package com.tenure.global.storage.validation;

public record ImageMetadata(
        String contentType,
        int width,
        int height
) {
}
