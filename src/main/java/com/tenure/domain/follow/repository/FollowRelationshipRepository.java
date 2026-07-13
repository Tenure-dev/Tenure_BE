package com.tenure.domain.follow.repository;

import com.tenure.domain.follow.entity.FollowRelationship;
import com.tenure.domain.follow.enums.FollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {

    boolean existsByFollower_IdAndFollowing_IdAndStatus(Long followerId, Long followingId, FollowStatus status);
}
