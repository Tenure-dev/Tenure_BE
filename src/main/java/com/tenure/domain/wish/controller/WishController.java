package com.tenure.domain.wish.controller;

import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.wish.dto.WishCreateResponse;
import com.tenure.domain.wish.dto.WishDeleteResponse;
import com.tenure.domain.wish.dto.WishListResponse;
import com.tenure.domain.wish.service.WishService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.response.PageResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Wish", description = "위시리스트 API")
@RestController
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "위시 등록",
            description = "로그인 사용자가 아이템을 위시리스트에 등록합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @PostMapping("/items/{itemId}/wish")
    public BaseResponse<WishCreateResponse> createWish(
            @PathVariable Long itemId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId(); //로그인한 사용자 ID 가져옴
        WishCreateResponse response = wishService.createWish(currentUserId, itemId); //위시 등록 작업(service)

        return BaseResponse.success(response, "위시 등록에 성공했습니다.");
    }

    @Operation(
            summary = "위시 해제",
            description = "로그인 사용자가 위시 등록한 아이템을 위시리스트에서 해제합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @DeleteMapping("/items/{itemId}/wish")
    public BaseResponse<WishDeleteResponse> deleteWish(
            @PathVariable Long itemId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        WishDeleteResponse response = wishService.deleteWish(currentUserId, itemId);

        return BaseResponse.success(response, "위시 해제에 성공했습니다.");
    }

    @Operation(
            summary = "위시리스트 조회",
            description = "로그인 사용자가 위시 등록한 아이템 목록을 조회합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @GetMapping("/wishes")
    public BaseResponse<PageResponse<WishListResponse>> getMyWishes(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) ProductStatus saleStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<WishListResponse> response = wishService.getMyWishes(
                currentUserId,
                query,
                saleStatus,
                pageable
        );

        return BaseResponse.success(response, "위시리스트 조회에 성공했습니다.");
    }
}