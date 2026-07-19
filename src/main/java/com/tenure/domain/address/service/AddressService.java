package com.tenure.domain.address.service;

import com.tenure.domain.address.dto.response.AddressResponse;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final DeliveryAddressRepository addressRepository;

    // userId를 요청값으로 받지 않고 JWT에서 추출한 currentUserId 사용
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(Long currentUserId) {
        return addressRepository
                .findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUserId)
                .stream()
                .map(AddressResponse::from)
                .toList();
    }
}