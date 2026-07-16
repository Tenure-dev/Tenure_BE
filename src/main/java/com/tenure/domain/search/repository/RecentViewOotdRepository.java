package com.tenure.domain.search.repository;

import com.tenure.domain.search.entity.RecentViewOotd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecentViewOotdRepository extends JpaRepository<RecentViewOotd, Long> {

    // 가장 최근 조회한 OOTD id
    @Query("select r.ootd.id from RecentViewOotd r " +
            "where r.viewer.id = :userId " +
            "order by r.lastViewedAt desc limit 1")
    Optional<Long> findLatestViewedOotdId(@Param("userId") Long userId);
}
