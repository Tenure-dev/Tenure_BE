package com.tenure.domain.wish.controller;

import com.tenure.domain.wish.dto.WishCreateResponse;
import com.tenure.domain.wish.service.WishService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
}