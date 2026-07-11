package com.tenure.domain.product.repository;

import com.tenure.domain.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select product from Product product where product.id = :productId")
    Optional<Product> findByIdForUpdate(@Param("productId") Long productId);
}
