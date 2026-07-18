package com.tenure.domain.address.repository;

import com.tenure.domain.address.entity.DeliveryAddress;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    // 내 배송지 목록 (기본배송지 먼저, 그다음 최신순)
    List<DeliveryAddress> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);

    Optional<DeliveryAddress> findByIdAndUser_Id(Long id, Long userId);

    // 특정 사용자의 배송지 개수 (첫 배송지 여부 판단용)
    long countByUserId(Long userId);

    // 특정 사용자의 현재 기본배송지 (기존 기본 해제할 때 사용)
    Optional<DeliveryAddress> findByUserIdAndIsDefaultTrue(Long userId);
}
