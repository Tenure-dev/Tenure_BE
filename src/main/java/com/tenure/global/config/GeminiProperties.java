package com.tenure.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tenure.ai.gemini")
public record GeminiProperties(String apiKey, String endpoint, String model) {
}
