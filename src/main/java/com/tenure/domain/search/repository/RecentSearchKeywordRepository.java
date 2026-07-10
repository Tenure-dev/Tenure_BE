package com.tenure.domain.search.repository;

import com.tenure.domain.search.entity.RecentSearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentSearchKeywordRepository extends JpaRepository<RecentSearchKeyword, Long> {
}
