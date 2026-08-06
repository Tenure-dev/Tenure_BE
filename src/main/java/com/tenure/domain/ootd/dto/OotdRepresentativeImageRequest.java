package com.tenure.domain.ootd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "OOTD bbox 기반 아이템 대표 이미지 생성 요청")
public record OotdRepresentativeImageRequest(

        @NotNull(message = "bbox 좌표는 필수입니다.")
        @Valid
        @Schema(description = "아이템 영역 bbox 좌표. 이미지 전체 기준 0~1 정규화 좌표입니다.")
        BboxRequest bbox
) {

    @Schema(description = "이미지 전체 기준 0~1 정규화 bbox 좌표")
    public record BboxRequest(

            @NotNull(message = "bbox.x는 필수입니다.")
            @DecimalMin(value = "0.0", message = "bbox.x는 0 이상이어야 합니다.")
            @DecimalMax(value = "1.0", message = "bbox.x는 1 이하여야 합니다.")
            @Schema(example = "0.12")
            BigDecimal x,

            @NotNull(message = "bbox.y는 필수입니다.")
            @DecimalMin(value = "0.0", message = "bbox.y는 0 이상이어야 합니다.")
            @DecimalMax(value = "1.0", message = "bbox.y는 1 이하여야 합니다.")
            @Schema(example = "0.18")
            BigDecimal y,

            @NotNull(message = "bbox.width는 필수입니다.")
            @DecimalMin(value = "0.0001", message = "bbox.width는 0보다 커야 합니다.")
            @DecimalMax(value = "1.0", message = "bbox.width는 1 이하여야 합니다.")
            @Schema(example = "0.35")
            BigDecimal width,

            @NotNull(message = "bbox.height는 필수입니다.")
            @DecimalMin(value = "0.0001", message = "bbox.height는 0보다 커야 합니다.")
            @DecimalMax(value = "1.0", message = "bbox.height는 1 이하여야 합니다.")
            @Schema(example = "0.44")
            BigDecimal height
    ) {
    }
}
