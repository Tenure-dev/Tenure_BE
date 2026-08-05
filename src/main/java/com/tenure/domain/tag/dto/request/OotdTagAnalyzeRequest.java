package com.tenure.domain.tag.dto.request;

import com.tenure.domain.tag.dto.request.OotdTagCreateRequest.BboxRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "OOTD 박스 영역 AI 분석 요청")
public record OotdTagAnalyzeRequest(

        @NotNull(message = "bbox 좌표는 필수입니다.")
        @Valid
        @Schema(description = "분석할 영역의 bbox 좌표 (이미지 전체 기준 0~1 상대 좌표)")
        BboxRequest bbox
) {
}
