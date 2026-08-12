package com.tenure.domain.tag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "OOTD 박스 영역 AI 분석 응답")
public record OotdTagAnalyzeResponse(

        @Schema(description = "추론된 라벨 (식별 실패 시 null)", example = "블루종 자켓")
        String labelText,

        @Schema(description = "추론된 상위 카테고리 (식별 실패 시 null)", example = "아우터")
        String categoryLarge,

        @Schema(description = "추론된 세부 카테고리 (식별 실패 시 null)", example = "블루종")
        String categorySmall,

        @Schema(description = "AI 인식 신뢰도 (0~1, 식별 실패 시 null). "
                + "matchedItemIds가 비어있을 때 이 값이 임계값(AI_TAG_CONFIDENCE_THRESHOLD) 미만이면 신뢰도 부족, "
                + "이상인데도 비어있으면 라벨/브랜드명 텍스트 불일치가 원인입니다.", example = "0.8231")
        BigDecimal confidence,

        @Schema(description = "매칭된 보유 아이템 ID 목록. 유사도가 높은 순으로 정렬되며, 매칭 실패 시 빈 배열")
        List<Long> matchedItemIds
) {

    public static OotdTagAnalyzeResponse of(
            String labelText,
            String categoryLarge,
            String categorySmall,
            BigDecimal confidence,
            List<Long> matchedItemIds
    ) {
        return new OotdTagAnalyzeResponse(labelText, categoryLarge, categorySmall, confidence, matchedItemIds);
    }
}
