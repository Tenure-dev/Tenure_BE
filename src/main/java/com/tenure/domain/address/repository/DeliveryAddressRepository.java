package com.tenure.domain.address.repository;

import com.tenure.domain.address.entity.DeliveryAddress;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    // 내 배송지 목록 (기본배송지 먼저, 그다음 최신순)
    List<DeliveryAddress> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);

    Optional<DeliveryAddress> findByIdAndUser_Id(Long id, Long userId);
}
