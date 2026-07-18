package com.tenure.domain.ootd.service;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * 가시성 검증과 멱등 체크는 읽기 전용 조회만 수행하므로 별도 트랜잭션으로 묶지 않는다.
 * 실제 쓰기(insert + 카운트 증가)는 {@link OotdReactionRecorder}의 REQUIRES_NEW 트랜잭션에서
 * 원자적으로 수행되므로, 이 서비스가 트랜잭션을 열어 그 커넥션을 이중으로 점유할 필요가 없다.
 */
@Service
@RequiredArgsConstructor
public class OotdReactionService {

    private final OotdRepository ootdRepository;
    private final OotdReactionRepository ootdReactionRepository;
    private final OotdReactionRecorder ootdReactionRecorder;

    public void heartOotd(Long currentUserId, Long ootdId) {
        Ootd ootd = ootdRepository.findVisibleActiveById(ootdId, currentUserId, OotdPublicationStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(OotdErrorCode.OOTD_NOT_FOUND));

        addReaction(currentUserId, ootd.getId(), OotdReactionType.HEART);
    }

    private void addReaction(Long currentUserId, Long ootdId, OotdReactionType reactionType) {
        if (ootdReactionRepository.existsByUser_IdAndOotd_IdAndReactionType(currentUserId, ootdId, reactionType)) {
            return;
        }

        try {
            ootdReactionRecorder.insert(currentUserId, ootdId, reactionType);
        } catch (DataIntegrityViolationException e) {
            // 동시 중복 등록 경합 — insert와 카운트 증가가 recorder의 REQUIRES_NEW 트랜잭션에서
            // 함께 롤백되었으므로 멱등하게 무시한다.
        }
    }
}
