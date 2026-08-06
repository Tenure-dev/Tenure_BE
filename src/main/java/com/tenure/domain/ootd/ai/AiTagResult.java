package com.tenure.domain.ootd.ai;

import java.math.BigDecimal;

public record AiTagResult(
        String labelText,
        String categoryLarge,
        String categorySmall,
        BigDecimal bboxX,
        BigDecimal bboxY,
        BigDecimal bboxWidth,
        BigDecimal bboxHeight,
        BigDecimal confidence
) {
}
