package com.tenure.domain.ootd.scheduler;

import com.tenure.domain.ootd.repository.OotdRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotScoreScheduler {

    private final OotdRepository ootdRepository;

    @Scheduled(fixedDelayString = "${tenure.ootd.hot-score.interval-minutes:10}", timeUnit = TimeUnit.MINUTES)
    public void updateHotScores() {
        int updated = ootdRepository.bulkUpdateHotScores();
        log.info("[hotScore 배치] {}건 업데이트 완료", updated);
    }
}
