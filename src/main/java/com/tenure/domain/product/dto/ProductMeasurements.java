package com.tenure.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Schema(description = "판매 상품 실측")
public record ProductMeasurements(

        @Schema(description = "어깨 너비(cm)", example = "45")
        BigDecimal shoulderWidth,

        @Schema(description = "가슴 단면(cm)", example = "55")
        BigDecimal chestWidth,

        @Schema(description = "소매길이(cm)", example = "60")
        BigDecimal sleeveLength,

        @Schema(description = "총 기장(cm)", example = "70")
        BigDecimal totalLength,

        @Schema(description = "허리 단면(cm)", example = "38")
        BigDecimal waistWidth,

        @Schema(description = "허벅지 단면(cm)", example = "30")
        BigDecimal thighWidth,

        @Schema(description = "밑위(cm)", example = "28")
        BigDecimal rise,

        @Schema(description = "인심(cm)", example = "73")
        BigDecimal inseam,

        @Schema(description = "밑단(cm)", example = "20")
        BigDecimal hemWidth,

        @Schema(description = "엉덩이 단면(cm)", example = "45")
        BigDecimal hipWidth
) {

    public static ProductMeasurements empty() {
        return new ProductMeasurements(null, null, null, null, null, null, null, null, null, null);
    }

    public static ProductMeasurements fromLegacyMap(Map<String, Object> legacyMeasurements) {
        return new ProductMeasurements(
                decimalValue(legacyMeasurements, "shoulder", "shoulderWidth"),
                decimalValue(legacyMeasurements, "chest", "chestWidth"),
                decimalValue(legacyMeasurements, "sleeve", "sleeveLength"),
                decimalValue(legacyMeasurements, "totalLength"),
                decimalValue(legacyMeasurements, "waist", "waistWidth"),
                decimalValue(legacyMeasurements, "thigh", "thighWidth"),
                decimalValue(legacyMeasurements, "rise"),
                decimalValue(legacyMeasurements, "inseam"),
                decimalValue(legacyMeasurements, "hem", "hemWidth"),
                decimalValue(legacyMeasurements, "hip", "hipWidth")
        );
    }

    public boolean isEmpty() {
        return fields().stream().allMatch(value -> value == null);
    }

    public List<BigDecimal> fields() {
        return Arrays.asList(
                shoulderWidth,
                chestWidth,
                sleeveLength,
                totalLength,
                waistWidth,
                thighWidth,
                rise,
                inseam,
                hemWidth,
                hipWidth
        );
    }

    private static BigDecimal decimalValue(Map<String, Object> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value != null) {
                return new BigDecimal(value.toString());
            }
        }
        return null;
    }
}
