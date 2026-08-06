package com.tenure.domain.tag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "OOTD 박스 영역 AI 분석 응답")
public record OotdTagAnalyzeResponse(

        @Schema(description = "추론된 라벨 (식별 실패 시 null)", example = "블루종 자켓")
        String labelText,

        @Schema(description = "추론된 상위 카테고리 (식별 실패 시 null)", example = "아우터")
        String categoryLarge,

        @Schema(description = "추론된 세부 카테고리 (식별 실패 시 null)", example = "블루종")
        String categorySmall,

        @Schema(description = "매칭된 보유 아이템 ID 목록. 유사도가 높은 순으로 정렬되며, 매칭 실패 시 빈 배열")
        List<Long> matchedItemIds
) {

    public static OotdTagAnalyzeResponse of(
            String labelText,
            String categoryLarge,
            String categorySmall,
            List<Long> matchedItemIds
    ) {
        return new OotdTagAnalyzeResponse(labelText, categoryLarge, categorySmall, matchedItemIds);
    }
}
