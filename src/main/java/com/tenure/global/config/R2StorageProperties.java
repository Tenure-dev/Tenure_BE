package com.tenure.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tenure.storage.r2")
public record R2StorageProperties(
        String bucket,
        String endpoint,
        String publicBaseUrl,
        String accessKeyId,
        String secretAccessKey
) {
}
