package com.tenure.domain.item.repository;

import com.tenure.domain.item.entity.Category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndDepthAndIsActiveTrue(String name, Integer depth);

    Optional<Category> findByNameAndParentAndDepthAndIsActiveTrue(
            String name,
            Category parent,
            Integer depth
    );

    // 카테고리 목록 반환
    List<Category> findAllByIsActiveTrueOrderByDepthAscSortOrderAsc();
}
