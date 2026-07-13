package com.tenure.domain.address.repository;

import com.tenure.domain.address.entity.DeliveryAddress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    Optional<DeliveryAddress> findByIdAndUser_Id(Long id, Long userId);
}
