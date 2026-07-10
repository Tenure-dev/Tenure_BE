package com.tenure.domain.purchase.repository;

import com.tenure.domain.purchase.entity.PurchaseIntent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseIntentRepository extends JpaRepository<PurchaseIntent, Long> {
}
