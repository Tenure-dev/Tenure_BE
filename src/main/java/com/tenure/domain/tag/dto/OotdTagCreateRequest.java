package com.tenure.domain.tag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "OOTD 태그 등록/확정 요청")
public record OotdTagCreateRequest(

        @NotNull(message = "아이템 ID는 필수입니다.")
        @Schema(description = "연결할 아이템 ID", example = "10")
        Long itemId,

        @NotNull(message = "bbox 좌표는 필수입니다.")
        @Valid
        BboxRequest bbox,

        @NotBlank(message = "라벨 텍스트는 필수입니다.")
        @Size(max = 100, message = "라벨 텍스트는 100자 이하여야 합니다.")
        @Schema(description = "태그 라벨", example = "블루종 자켓")
        String labelText,

        @NotBlank(message = "상태값은 필수입니다.")
        @Schema(description = "태그 상태. 직접 등록/수정 시 CONFIRMED만 허용", example = "CONFIRMED")
        String status
) {

    @Schema(description = "이미지 전체 기준 0~1 상대 좌표")
    public record BboxRequest(

            @NotNull(message = "bbox.x는 필수입니다.")
            @DecimalMin(value = "0.0", message = "bbox.x는 0 이상이어야 합니다.")
            @DecimalMax(value = "1.0", message = "bbox.x는 1 이하여야 합니다.")
            @Schema(example = "0.12")
            BigDecimal x,

            @NotNull(message = "bbox.y는 필수입니다.")
            @DecimalMin(value = "0.0", message = "bbox.y는 0 이상이어야 합니다.")
            @DecimalMax(value = "1.0", message = "bbox.y는 1 이하여야 합니다.")
            @Schema(example = "0.08")
            BigDecimal y,

            @NotNull(message = "bbox.width는 필수입니다.")
            @DecimalMin(value = "0.0", message = "bbox.width는 0 이상이어야 합니다.")
            @DecimalMax(value = "1.0", message = "bbox.width는 1 이하여야 합니다.")
            @Schema(example = "0.40")
            BigDecimal width,

            @NotNull(message = "bbox.height는 필수입니다.")
            @DecimalMin(value = "0.0", message = "bbox.height는 0 이상이어야 합니다.")
            @DecimalMax(value = "1.0", message = "bbox.height는 1 이하여야 합니다.")
            @Schema(example = "0.55")
            BigDecimal height
    ) {
    }
}
