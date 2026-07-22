package com.tenure.domain.follow.service;

import com.tenure.domain.follow.dto.response.FollowResponse;
import com.tenure.domain.follow.entity.FollowRelationship;
import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.exception.FollowErrorCode;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserBlockRepository;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 팔로우 관련 비즈니스 로직.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRelationshipRepository followRepository;
    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;

    // 팔로우 (즉시 반영)
    @Transactional
    public FollowResponse follow(Long currentUserId, Long targetUserId) {

        // 1) 자기 자신은 팔로우할 수 없다
        if (currentUserId.equals(targetUserId)) {
            throw new CustomException(FollowErrorCode.CANNOT_FOLLOW_SELF);
        }

        // 2) 대상 사용자 존재 확인
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 3) 차단 관계 확인 (내가 차단했거나, 상대가 나를 차단한 경우)
        boolean blockedByMe = userBlockRepository.isBlocked(currentUserId, targetUserId);
        boolean blockedByTarget = userBlockRepository.isBlocked(targetUserId, currentUserId);
        if (blockedByMe || blockedByTarget) {
            throw new CustomException(UserErrorCode.USER_BLOCKED);
        }

        // 4) 이미 팔로우 중이면 거부 (unique 제약 위반 방지)
        if (followRepository.existsByFollower_IdAndFollowing_Id(currentUserId, targetUserId)) {
            throw new CustomException(FollowErrorCode.ALREADY_FOLLOWING);
        }

        // 5) 팔로우 관계 생성 (항상 ACCEPTED)
        User follower = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        followRepository.save(FollowRelationship.create(follower, target));
        log.info("팔로우: {} -> {}", currentUserId, targetUserId);

        // 6) 갱신된 팔로워 수와 함께 응답
        long followerCount = followRepository.countByFollowing_IdAndStatus(targetUserId, FollowStatus.ACCEPTED);
        return new FollowResponse(targetUserId, true, followerCount);
    }
}