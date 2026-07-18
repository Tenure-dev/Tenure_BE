package com.tenure.domain.ootd.service;

import com.tenure.domain.ootd.entity.OotdReaction;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 반응 insert와 카운트 증가를 별도 물리 트랜잭션(REQUIRES_NEW)에서 함께 수행한다.
 * PostgreSQL은 유니크 제약 위반이 발생하면 해당 커넥션의 트랜잭션 전체가 abort 상태가 되어,
 * 호출자와 같은 트랜잭션에서 예외를 잡기만 하면 이후 정상 커밋 시점에 실패한다.
 * 이 메서드는 예외를 내부에서 잡지 않고 그대로 전파해 이 트랜잭션만 롤백시키고,
 * 호출자(별도 트랜잭션)는 그 예외를 안전하게 catch할 수 있다.
 * insert와 카운트 증가를 한 트랜잭션에 묶어 두면, 유니크 위반 시 둘 다 롤백되어
 * 카운트 미증가가 트랜잭션 레벨에서 보장된다.
 */
@Component
@RequiredArgsConstructor
public class OotdReactionRecorder {

    private final OotdReactionRepository ootdReactionRepository;
    private final OotdRepository ootdRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insert(Long userId, Long ootdId, OotdReactionType reactionType) {
        OotdReaction reaction = OotdReaction.create(
                userRepository.getReferenceById(userId),
                ootdRepository.getReferenceById(ootdId),
                reactionType
        );
        ootdReactionRepository.saveAndFlush(reaction);

        increaseCount(ootdId, reactionType);
    }

    private void increaseCount(Long ootdId, OotdReactionType reactionType) {
        switch (reactionType) {
            case HEART -> ootdRepository.increaseHeartCount(ootdId);
            case SAVE -> ootdRepository.increaseSaveCount(ootdId);
        }
    }
}
