package com.tenure.domain.wish.repository;

import com.tenure.domain.wish.entity.Wish;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    Optional<Wish> findByUserIdAndItemId(Long userId, Long itemId);
}
