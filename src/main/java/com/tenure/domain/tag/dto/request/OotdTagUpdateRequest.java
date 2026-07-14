package com.tenure.domain.tag.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "OOTD 태그 확정/수정 요청")
public record OotdTagUpdateRequest(

        @NotNull(message = "아이템 ID는 필수입니다.")
        @Schema(description = "연결할 아이템 ID", example = "10")
        Long itemId,

        @NotNull(message = "bbox 좌표는 필수입니다.")
        @Valid
        OotdTagCreateRequest.BboxRequest bbox,

        @NotBlank(message = "라벨 텍스트는 필수입니다.")
        @Size(max = 100, message = "라벨 텍스트는 100자 이하여야 합니다.")
        @Schema(description = "태그 라벨", example = "블루종 자켓")
        String labelText,

        @NotBlank(message = "상태값은 필수입니다.")
        @Schema(description = "태그 상태. 확정 시 CONFIRMED만 허용", example = "CONFIRMED")
        String status
) {
}
