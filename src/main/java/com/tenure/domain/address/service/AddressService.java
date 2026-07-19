package com.tenure.domain.address.service;

import com.tenure.domain.address.dto.response.AddressResponse;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tenure.domain.address.dto.request.AddressCreateRequest;
import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.domain.address.dto.request.AddressUpdateRequest;
import com.tenure.domain.address.exception.AddressErrorCode;


@Service
@RequiredArgsConstructor
public class AddressService {

    private final DeliveryAddressRepository addressRepository;
    private final UserRepository userRepository;

    // userId를 요청값으로 받지 않고 JWT에서 추출한 currentUserId 사용
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(Long currentUserId) {
        return addressRepository
                .findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUserId)
                .stream()
                .map(AddressResponse::from)
                .toList();
    }

    // 배송지 등록
    // 이 사용자의 첫 배송지면 무조건 기본배송지로 지정
    // 기본배송지는 항상 1개
    @Transactional
    public AddressResponse createAddress(Long currentUserId, AddressCreateRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 첫 배송지 여부 판단
        boolean isFirstAddress = addressRepository.countByUserId(currentUserId) == 0;

        // 첫 배송지면 강제 기본, 아니면 요청값 따름
        boolean makeDefault = isFirstAddress || Boolean.TRUE.equals(request.isDefault());

        // 기본으로 지정하는 경우, 기존 기본배송지가 있으면 먼저 해제 (기본은 1개만)
        if (makeDefault) {
            addressRepository.findByUserIdAndIsDefaultTrue(currentUserId)
                    .ifPresent(DeliveryAddress::unmarkDefault);
        }

        DeliveryAddress address = DeliveryAddress.create(
                user,
                request.receiverName(),
                request.phone(),
                request.addressLine1(),
                request.addressLine2(),
                request.postalCode(),
                request.requestNote(),
                makeDefault
        );

        DeliveryAddress saved = addressRepository.save(address);
        return AddressResponse.from(saved);
    }

    // 배송지 수정
    // 본인이 등록한 배송지만 수정 가능
    @Transactional
    public AddressResponse updateAddress(Long currentUserId, Long addressId, AddressUpdateRequest request) {
        DeliveryAddress address = getOwnedAddress(currentUserId, addressId);

        // 일반 필드 수정 (보낸 것만)
        address.update(
                request.receiverName(),
                request.phone(),
                request.addressLine1(),
                request.addressLine2(),
                request.postalCode(),
                request.requestNote()
        );

        // 기본배송지로 지정 요청이 왔고, 현재 기본이 아니면 -> 기존 기본 해제 후 이걸 기본으로
        if (Boolean.TRUE.equals(request.isDefault()) && !address.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(currentUserId)
                    .ifPresent(DeliveryAddress::unmarkDefault);
            address.markAsDefault();
        }

        return AddressResponse.from(address);
    }

    // 본인이 등록한 배송지인지 확인
    private DeliveryAddress getOwnedAddress(Long currentUserId, Long addressId) {
        DeliveryAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new CustomException(AddressErrorCode.ADDRESS_NOT_FOUND));

        if (!address.isOwnedBy(currentUserId)) {
            throw new CustomException(AddressErrorCode.ADDRESS_FORBIDDEN);
        }
        return address;
    }

    // 배송지 삭제
    // 기본배송지는 삭제 불가. 다른 걸 기본으로 바꾼 뒤에 삭제해야 함
    @Transactional
    public void deleteAddress(Long currentUserId, Long addressId) {
        DeliveryAddress address = getOwnedAddress(currentUserId, addressId);

        // 기본배송지는 삭제 금지 (다른 배송지를 기본으로 바꾼 후 삭제 가능)
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            throw new CustomException(AddressErrorCode.DEFAULT_ADDRESS_CANNOT_BE_DELETED);
        }

        addressRepository.delete(address);
    }
}