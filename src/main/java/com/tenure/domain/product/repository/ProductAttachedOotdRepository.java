package com.tenure.domain.product.repository;

import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.product.entity.ProductAttachedOotd;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductAttachedOotdRepository extends JpaRepository<ProductAttachedOotd, Long> {

    @Query("""
            select attached
            from ProductAttachedOotd attached
            join fetch attached.ootd ootd
            where attached.product.id = :productId
              and ootd.publicationStatus = :publicationStatus
            order by ootd.createdAt desc
            """)
    List<ProductAttachedOotd> findActiveByProductIdOrderByOotdCreatedAtDesc(
            @Param("productId") Long productId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus
    );
}
