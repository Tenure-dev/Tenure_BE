package com.tenure.domain.ootd.listener;

import com.tenure.domain.ootd.ai.AiTagResult;
import com.tenure.domain.ootd.ai.AiTagService;
import com.tenure.domain.ootd.event.OotdCreatedEvent;
import com.tenure.domain.tag.service.OotdTagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * OOTD 게시 트랜잭션 커밋 이후 비동기로 AI 태그 분석을 수행하고 결과를 저장한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OotdCreatedEventListener {

    private final AiTagService aiTagService;
    private final OotdTagService ootdTagService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOotdCreated(OotdCreatedEvent event) {
        log.info("OOTD_CREATED 이벤트 수신 - ootdId={}. AI 태그 분석을 시작합니다.", event.ootdId());

        List<AiTagResult> results = aiTagService.analyze(event.imageUrl());
        ootdTagService.saveAiTags(event.ootdId(), results);
    }
}
