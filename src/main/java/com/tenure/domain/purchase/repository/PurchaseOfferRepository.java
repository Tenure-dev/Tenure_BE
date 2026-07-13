package com.tenure.domain.purchase.repository;

import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOfferRepository extends JpaRepository<PurchaseOffer, Long> {

    @Query("select offer.item.id from PurchaseOffer offer where offer.id = :offerId")
    Optional<Long> findItemIdById(@Param("offerId") Long offerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select offer from PurchaseOffer offer where offer.id = :offerId")
    Optional<PurchaseOffer> findByIdForUpdate(@Param("offerId") Long offerId);

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
