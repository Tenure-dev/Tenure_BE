package com.tenure.domain.search.repository;

import com.tenure.domain.search.dto.response.RecentUserResponse;
import com.tenure.domain.search.entity.RecentViewUser;
import com.tenure.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecentViewUserRepository extends JpaRepository<RecentViewUser, Long> {

    //최근 조회한 유저 조회(last_viewed_at 최신순 정렬. unique constraint로 중복 없음)
    @Query("select new com.tenure.domain.search.dto.response.RecentUserResponse(r.viewed.id, r.viewed.username, r.viewed.profileImageUrl) " +
            "from RecentViewUser r " +
            "where r.viewer.id = :currentUserId " +
            "order by r.lastViewedAt desc limit :count")
    List<RecentUserResponse> findByRecentUserTop10(@Param("currentUserId") Long currentUserId, @Param("count") int count);
}
