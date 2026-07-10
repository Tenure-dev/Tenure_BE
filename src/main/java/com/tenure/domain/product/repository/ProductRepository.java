package com.tenure.domain.product.repository;

import com.tenure.domain.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            select product
            from Product product
            join fetch product.item item
            join fetch item.owner itemOwner
            join fetch item.category category
            left join fetch category.parent
            join fetch product.seller seller
            where product.id = :productId
            """)
    Optional<Product> findDetailById(@Param("productId") Long productId);
}
