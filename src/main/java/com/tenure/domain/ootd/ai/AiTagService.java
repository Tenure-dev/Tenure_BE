package com.tenure.domain.ootd.ai;

import java.math.BigDecimal;
import java.util.List;

/**
 * OOTD 이미지를 분석해 착장 아이템 태그 후보를 반환하는 AI 연동 인터페이스.
 * 구현체를 교체(Gemini, Mock 등)해도 호출부(OotdCreatedEventListener, OotdTagService)는 변경되지 않는다.
 */
public interface AiTagService {

    List<AiTagResult> analyze(String imageUrl);

    default List<AiTagResult> analyze(String imageUrl, String imageObjectKey) {
        return analyze(imageUrl);
    }

    RegionAnalysisResult analyzeRegion(
            String imageUrl,
            BigDecimal bboxX,
            BigDecimal bboxY,
            BigDecimal bboxWidth,
            BigDecimal bboxHeight
    );

    default RegionAnalysisResult analyzeRegion(
            String imageUrl,
            String imageObjectKey,
            BigDecimal bboxX,
            BigDecimal bboxY,
            BigDecimal bboxWidth,
            BigDecimal bboxHeight
    ) {
        return analyzeRegion(imageUrl, bboxX, bboxY, bboxWidth, bboxHeight);
    }
}
