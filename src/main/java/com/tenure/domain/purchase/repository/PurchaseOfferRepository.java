package com.tenure.domain.purchase.repository;

import com.tenure.domain.purchase.entity.PurchaseOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOfferRepository extends JpaRepository<PurchaseOffer, Long> {
}
