package com.tenure.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
@Schema(description = "판매 상품 상태 이상 체크")
public record ProductConditionFlags(

        @NotNull(message = "오염 여부는 필수입니다.")
        @Schema(description = "오염 여부", example = "true")
        Boolean stain,

        @NotNull(message = "찢어짐 여부는 필수입니다.")
        @Schema(description = "찢어짐 여부", example = "false")
        Boolean tear,

        @NotNull(message = "보풀/변색 여부는 필수입니다.")
        @Schema(description = "보풀/변색 여부", example = "true")
        Boolean pillingOrDiscoloration,

        @NotNull(message = "수선 이력 여부는 필수입니다.")
        @Schema(description = "수선 이력 여부", example = "false")
        Boolean repairHistory,

        @NotNull(message = "구성품 누락 여부는 필수입니다.")
        @Schema(description = "구성품 누락 여부", example = "false")
        Boolean missingComponents
) {

    public static ProductConditionFlags empty() {
        return new ProductConditionFlags(false, false, false, false, false);
    }

    public static ProductConditionFlags fromLegacyFlags(List<String> legacyFlags) {
        boolean stain = false;
        boolean tear = false;
        boolean pillingOrDiscoloration = false;
        boolean repairHistory = false;
        boolean missingComponents = false;

        for (String legacyFlag : legacyFlags) {
            if ("STAIN".equals(legacyFlag)) {
                stain = true;
            } else if ("TEAR".equals(legacyFlag)) {
                tear = true;
            } else if ("PILLING_OR_DISCOLORATION".equals(legacyFlag)) {
                pillingOrDiscoloration = true;
            } else if ("REPAIR_HISTORY".equals(legacyFlag)) {
                repairHistory = true;
            } else if ("MISSING_COMPONENTS".equals(legacyFlag)) {
                missingComponents = true;
            }
        }

        return new ProductConditionFlags(
                stain,
                tear,
                pillingOrDiscoloration,
                repairHistory,
                missingComponents
        );
    }
}
