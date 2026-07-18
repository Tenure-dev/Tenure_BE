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
import org.springframework.transaction.annotation.Transactional;

/**
 * 하트 등록(heartOotd)은 가시성 검증과 멱등 체크만 읽기 전용으로 수행하고, 실제 쓰기(insert + 카운트 증가)는
 * {@link OotdReactionRecorder}의 REQUIRES_NEW 트랜잭션에서 원자적으로 수행한다. insert는 uk_ootd_reactions
 * 유니크 제약을 위반하면 PostgreSQL 트랜잭션 전체가 abort 상태가 될 수 있어 별도 격리가 필요하기 때문이다.
 * 하트 취소(unheartOotd)는 DELETE가 유니크 제약을 위반할 수 없어 같은 위험이 없으므로,
 * 별도 트랜잭션 격리 없이 이 서비스의 @Transactional 안에서 delete와 카운트 감소를 한 트랜잭션으로 수행한다.
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

    @Transactional
    public void unheartOotd(Long currentUserId, Long ootdId) {
        Ootd ootd = ootdRepository.findVisibleActiveById(ootdId, currentUserId, OotdPublicationStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(OotdErrorCode.OOTD_NOT_FOUND));

        removeReaction(currentUserId, ootd.getId(), OotdReactionType.HEART);
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

    private void removeReaction(Long currentUserId, Long ootdId, OotdReactionType reactionType) {
        int deletedCount = ootdReactionRepository.deleteByUserAndOotdAndReactionType(currentUserId, ootdId, reactionType);
        if (deletedCount == 1) {
            decreaseCount(ootdId, reactionType);
        }
    }

    private void decreaseCount(Long ootdId, OotdReactionType reactionType) {
        if (reactionType == OotdReactionType.HEART) {
            ootdRepository.decreaseHeartCount(ootdId);
        }
    }
}
