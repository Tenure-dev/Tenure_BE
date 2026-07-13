package com.tenure.domain.purchase.repository;

import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseIntentRepository extends JpaRepository<PurchaseIntent, Long> {

    @Query("select intent.product.id from PurchaseIntent intent where intent.id = :intentId")
    Optional<Long> findProductIdById(@Param("intentId") Long intentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select intent from PurchaseIntent intent where intent.id = :intentId")
    Optional<PurchaseIntent> findByIdForUpdate(@Param("intentId") Long intentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select intent
            from PurchaseIntent intent
            where intent.product.id = :productId
              and intent.status = :status
            """)
    List<PurchaseIntent> findSentByProductIdForUpdate(
            @Param("productId") Long productId,
            @Param("status") PurchaseIntentStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select intent
            from PurchaseIntent intent
            where intent.product.id = :productId
              and intent.buyer.id = :buyerUserId
              and intent.status = :status
            """)
    List<PurchaseIntent> findSentByProductIdAndBuyerIdForUpdate(
            @Param("productId") Long productId,
            @Param("buyerUserId") Long buyerUserId,
            @Param("status") PurchaseIntentStatus status
    );
}
