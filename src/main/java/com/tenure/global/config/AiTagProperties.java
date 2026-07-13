package com.tenure.global.config;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tenure.ai.tag")
public record AiTagProperties(BigDecimal confidenceThreshold) {
}
