package com.tenure.domain.item.repository;

import com.tenure.domain.item.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndDepthAndIsActiveTrue(String name, Integer depth);

    Optional<Category> findByNameAndParentAndDepthAndIsActiveTrue(
            String name,
            Category parent,
            Integer depth
    );

    Optional<Category> findByNameAndDepth(String name, Integer depth);
}
