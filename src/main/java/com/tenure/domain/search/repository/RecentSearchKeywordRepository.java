package com.tenure.domain.search.repository;

import com.tenure.domain.search.dto.response.RecentKeywordResponse;
import com.tenure.domain.search.entity.RecentSearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecentSearchKeywordRepository extends JpaRepository<RecentSearchKeyword, Long> {

    //추천 검색어 10개 조회
    @Query("select r.keyword from RecentSearchKeyword r " +
            "group by r.keyword order by count(*) desc limit :count")
    List<String> findTopKeywords(@Param("count") int count);


    //최근 검색어 조회(각 키워드 별 가장 최근에 검색한 키워드만 최신 순 정렬)
    @Query("select new com.tenure.domain.search.dto.response.RecentKeywordResponse(max(r.id), r.keyword) from RecentSearchKeyword  r " +
            "where r.user.id = :currentUserId " +
            "group by r.keyword " +
            "order by max(r.createdAt) desc limit :count")
    List<RecentKeywordResponse> findByUserTopKeywords(@Param("currentUserId") Long currentUserId, @Param("count") int count);


    @Modifying(clearAutomatically = true)
    @Query("delete from RecentSearchKeyword r " +
            "where r.user.id = :currentUserId and r.keyword = :keyword")
    void deleteRecentSearchKeywordByKeyword(@Param("currentUserId") Long currentUserId, @Param("keyword") String keyword);
}
