package com.tenure.domain.ootd.ai;

import java.math.BigDecimal;

public record RegionAnalysisResult(
        String labelText,
        String categoryLarge,
        String categorySmall,
        BigDecimal confidence
) {

    public static RegionAnalysisResult empty() {
        return new RegionAnalysisResult(null, null, null, null);
    }
}
