package com.tenure.domain.follow.repository;

import com.tenure.domain.follow.entity.FollowRelationship;
import com.tenure.domain.follow.enums.FollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;


import java.util.List;

public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {

    boolean existsByFollower_IdAndFollowing_IdAndStatus(Long followerId, Long followingId, FollowStatus status);

    // 이미 팔로우 관계가 있는지 확인 (status 무관)
    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    // 팔로우 관계 조회 (언팔로우 시 사용)
    Optional<FollowRelationship> findByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    //내가 팔로우 중인 유저의 id 리스트
    @Query("select uf.following.id from FollowRelationship uf " +
            "where uf.follower.id = :currentUserId " +
            "and uf.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED " +
            "and uf.following.id in :targetIds")
    List<Long> findFollowingIds(@Param("currentUserId") Long currentUserId,
                                @Param("targetIds") List<Long> targetIds);

    long countByFollowing_IdAndStatus(Long followingId, FollowStatus status);


    // 특정 유저가 팔로우하는 관계 목록 (팔로잉) - following 유저를 함께 로딩
    @Query("select fr from FollowRelationship fr " +
            "join fetch fr.following " +
            "where fr.follower.id = :userId and fr.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED " +
            "order by fr.createdAt desc")
    List<FollowRelationship> findFollowingsByUserId(@Param("userId") Long userId);

    // 특정 유저를 팔로우하는 관계 목록 (팔로워) - follower 유저를 함께 로딩
    @Query("select fr from FollowRelationship fr " +
            "join fetch fr.follower " +
            "where fr.following.id = :userId and fr.status = com.tenure.domain.follow.enums.FollowStatus.ACCEPTED " +
            "order by fr.createdAt desc")
    List<FollowRelationship> findFollowersByUserId(@Param("userId") Long userId);
}
