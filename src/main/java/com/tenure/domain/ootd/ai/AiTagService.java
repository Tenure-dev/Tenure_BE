package com.tenure.domain.ootd.ai;

import java.util.List;

/**
 * OOTD 이미지를 분석해 착장 아이템 태그 후보를 반환하는 AI 연동 인터페이스.
 * 구현체를 교체(Gemini, Mock 등)해도 호출부(OotdCreatedEventListener)는 변경되지 않는다.
 */
public interface AiTagService {

    List<AiTagResult> analyze(String imageUrl);
}
