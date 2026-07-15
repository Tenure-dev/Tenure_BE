package com.tenure.domain.search.repository;

import com.tenure.domain.search.dto.response.RecentUserResponse;
import com.tenure.domain.search.entity.RecentViewUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecentViewUserRepository extends JpaRepository<RecentViewUser, Long> {

    //최근 조회한 유저 조회(last_viewed_at 최신순 정렬. unique constraint로 중복 없음)
    @Query("select new com.tenure.domain.search.dto.response.RecentUserResponse(r.viewed.id, r.viewed.username, r.viewed.profileImageUrl) " +
            "from RecentViewUser r " +
            "where r.viewer.id = :currentUserId " +
            "order by r.lastViewedAt desc limit :count")
    List<RecentUserResponse> findByRecentUserTop10(@Param("currentUserId") Long currentUserId, @Param("count") int count);


    @Query("select r from RecentViewUser r " +
            "where r.viewer.id = :currentUserId and r.viewed.id = :viewedId")
    Optional<RecentViewUser> findByViewed_Id(@Param("currentUserId") Long currentUserId, @Param("viewedId") Long viewedId);

    //최근 본 사용자 삭제
    @Modifying(clearAutomatically = true)
    @Query("delete from RecentViewUser r " +
            "where r.viewer.id = :currentUserId and r.viewed.id = :recentViewedUserId")
    void deleteRecentViewedUser(@Param("currentUserId") Long currentUserId, @Param("recentViewedUserId") Long recentViewedUserId);
}
