package com.tenure.domain.tag.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "OOTD 태그 일괄 등록 요청")
public record OotdTagBatchRequest(

        @NotEmpty(message = "태그 목록은 최소 1개 이상이어야 합니다.")
        @Valid
        @Schema(description = "일괄 등록할 태그 목록")
        List<TagItem> tags
) {

    @Schema(description = "일괄 등록 태그 항목")
    public record TagItem(

            @NotNull(message = "아이템 ID는 필수입니다.")
            @Schema(description = "연결할 아이템 ID", example = "10")
            Long itemId,

            @NotNull(message = "bbox 좌표는 필수입니다.")
            @Valid
            OotdTagCreateRequest.BboxRequest bbox,

            @NotBlank(message = "라벨 텍스트는 필수입니다.")
            @Size(max = 100, message = "라벨 텍스트는 100자 이하여야 합니다.")
            @Schema(description = "태그 라벨", example = "블루종 자켓")
            String labelText
    ) {
    }
}
