package com.tenure.domain.item.repository;

import com.tenure.domain.item.entity.ItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {
}
