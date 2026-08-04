package com.tenure.domain.search.repository;

import com.tenure.domain.search.dto.response.RecentKeywordResponse;
import com.tenure.domain.search.entity.RecentSearchKeyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecentSearchKeywordRepository extends JpaRepository<RecentSearchKeyword, Long> {

    //추천 검색어 10개 조회
    @Query("select r.keyword from RecentSearchKeyword r " +
            "group by r.keyword order by count(*) desc")
    List<String> findTopKeywords(Pageable pageable);


    //최근 검색어 조회(각 키워드 별 가장 최근에 검색한 키워드만 최신 순 정렬)
    @Query("select new com.tenure.domain.search.dto.response.RecentKeywordResponse(max(r.id), r.keyword) from RecentSearchKeyword  r " +
            "where r.user.id = :currentUserId " +
            "group by r.keyword " +
            "order by max(r.createdAt) desc")
    List<RecentKeywordResponse> findByUserTopKeywords(@Param("currentUserId") Long currentUserId, Pageable pageable);


    // 입력된 검색어(prefix)로 시작하는 최근 검색어 중, 검색 빈도가 높은 상위 5개의 키워드(PageRequest.of(0,5))
    @Query("select r.keyword from RecentSearchKeyword r " +
            "where lower(r.keyword) like lower(concat(:keyword, '%')) " +
            "group by r.keyword " +
            "order by count(r.keyword) desc")
    List<String> findBySuggestRecentKeywords(
            @Param("keyword") String keyword,
            Pageable pageable
    );


    @Modifying(clearAutomatically = true)
    @Query("delete from RecentSearchKeyword r " +
            "where r.user.id = :currentUserId and r.keyword = :keyword")
    void deleteRecentSearchKeywordByKeyword(@Param("currentUserId") Long currentUserId, @Param("keyword") String keyword);

    @Modifying(clearAutomatically = true)
    @Query("delete from RecentSearchKeyword r where r.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
