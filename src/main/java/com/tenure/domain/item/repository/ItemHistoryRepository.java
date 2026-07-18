package com.tenure.domain.item.repository;

import com.tenure.domain.item.entity.ItemHistory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {

    Page<ItemHistory> findByItemIdOrderByStartedAtDesc(Long itemId, Pageable pageable);

    Optional<ItemHistory> findByItemIdAndEndedAtIsNull(Long itemId);
}
