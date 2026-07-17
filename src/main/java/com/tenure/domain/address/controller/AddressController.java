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
}