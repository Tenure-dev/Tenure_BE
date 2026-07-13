package com.tenure.domain.purchase.repository;

import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOfferRepository extends JpaRepository<PurchaseOffer, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select offer from PurchaseOffer offer where offer.id = :offerId")
    Optional<PurchaseOffer> findByIdForUpdate(@Param("offerId") Long offerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select offer
            from PurchaseOffer offer
            join fetch offer.item item
            join fetch offer.owner owner
            where offer.proposer.id = :proposerUserId
              and offer.status = :status
              and offer.expiresAt <= :now
            """)
    List<PurchaseOffer> findExpiredSentByProposerIdForUpdate(
            @Param("proposerUserId") Long proposerUserId,
            @Param("status") PurchaseOfferStatus status,
            @Param("now") LocalDateTime now
    );

    @Query("""
            select offer
            from PurchaseOffer offer
            join fetch offer.item item
            join fetch offer.owner owner
            where offer.proposer.id = :proposerUserId
              and offer.status in :statuses
              and (
                    :cursorCreatedAt is null
                    or offer.createdAt < :cursorCreatedAt
                    or (offer.createdAt = :cursorCreatedAt and offer.id < :cursorOfferId)
                  )
            order by offer.createdAt desc, offer.id desc
            """)
    List<PurchaseOffer> findSentListByProposerWithCursor(
            @Param("proposerUserId") Long proposerUserId,
            @Param("statuses") Collection<PurchaseOfferStatus> statuses,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorOfferId") Long cursorOfferId,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select offer
            from PurchaseOffer offer
            where offer.item.id = :itemId
              and offer.status = :status
            """)
    List<PurchaseOffer> findSentByItemIdForUpdate(
            @Param("itemId") Long itemId,
            @Param("status") PurchaseOfferStatus status
    );
}
