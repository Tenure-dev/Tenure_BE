package com.tenure.domain.ootd.ai;

import java.math.BigDecimal;

public record AiTagResult(
        String labelText,
        BigDecimal bboxX,
        BigDecimal bboxY,
        BigDecimal bboxWidth,
        BigDecimal bboxHeight,
        BigDecimal confidence
) {
}
