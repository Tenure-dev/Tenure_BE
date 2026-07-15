package com.tenure.domain.follow.repository;

import com.tenure.domain.follow.entity.FollowRelationship;
import com.tenure.domain.follow.enums.FollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {

    boolean existsByFollower_IdAndFollowing_IdAndStatus(Long followerId, Long followingId, FollowStatus status);


    //내가 팔로우 중인 유저의 id 리스트
    @Query("select uf.following.id from FollowRelationship uf " +
            "where uf.follower.id = :currentUserId " +
            "and uf.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED " +
            "and uf.following.id in :targetIds")
    List<Long> findFollowingIds(@Param("currentUserId") Long currentUserId,
                                @Param("targetIds") List<Long> targetIds);

}
