package com.tenure.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tenure.storage.local")
public record StorageProperties(String baseDir, String baseUrl) {
}
