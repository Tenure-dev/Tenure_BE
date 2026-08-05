package com.tenure.domain.tag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OOTD 박스 영역 AI 분석 응답")
public record OotdTagAnalyzeResponse(

        @Schema(description = "추론된 라벨 (식별 실패 시 null)", example = "블루종 자켓")
        String labelText,

        @Schema(description = "추론된 상위 카테고리 (식별 실패 시 null)", example = "아우터")
        String categoryLarge,

        @Schema(description = "추론된 세부 카테고리 (식별 실패 시 null)", example = "블루종")
        String categorySmall,

        @Schema(description = "매칭된 보유 아이템 ID (매칭 실패 시 null)", example = "10")
        Long matchedItemId
) {

    public static OotdTagAnalyzeResponse of(
            String labelText,
            String categoryLarge,
            String categorySmall,
            Long matchedItemId
    ) {
        return new OotdTagAnalyzeResponse(labelText, categoryLarge, categorySmall, matchedItemId);
    }
}
