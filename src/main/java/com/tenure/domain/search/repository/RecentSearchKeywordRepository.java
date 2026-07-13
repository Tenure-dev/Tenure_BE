package com.tenure.domain.search.repository;

import com.tenure.domain.search.entity.RecentSearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecentSearchKeywordRepository extends JpaRepository<RecentSearchKeyword, Long> {

    @Query("select r.keyword from RecentSearchKeyword r " +
            "group by r.keyword order by count(*) desc limit :count")
    List<String> findTopKeywords(@Param("count") int count);
}
