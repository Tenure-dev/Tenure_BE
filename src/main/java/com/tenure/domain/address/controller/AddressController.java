package com.tenure.domain.address.controller;

import com.tenure.domain.address.dto.response.AddressResponse;
import com.tenure.domain.address.service.AddressService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tenure.domain.address.dto.request.AddressCreateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.tenure.domain.address.dto.request.AddressUpdateRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Tag(name = "Address", description = "배송지 API")
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "내 배송지 조회", description = "로그인한 사용자의 배송지 목록을 조회합니다.")
    @GetMapping
    public BaseResponse<List<AddressResponse>> getMyAddresses() {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        return BaseResponse.success(addressService.getMyAddresses(currentUserId));
    }

    @Operation(summary = "배송지 등록", description = "새 배송지를 등록합니다. 첫 배송지는 자동으로 기본 배송지가 됩니다.")
    @PostMapping
    public BaseResponse<AddressResponse> createAddress(
            @Valid @RequestBody AddressCreateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        return BaseResponse.success(
                addressService.createAddress(currentUserId, request),
                "배송지가 등록되었습니다."
        );
    }

    @Operation(summary = "배송지 수정", description = "본인 소유의 배송지를 수정합니다.")
    @PatchMapping("/{addressId}")
    public BaseResponse<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressUpdateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        return BaseResponse.success(
                addressService.updateAddress(currentUserId, addressId, request),
                "배송지가 수정되었습니다."
        );
    }
}