package com.tenure.domain.item.controller;

import com.tenure.domain.item.dto.ItemCreateRequest;
import com.tenure.domain.item.dto.ItemCreateResponse;
import com.tenure.domain.item.service.ItemService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Item", description = "아이템 API")
@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "아이템 등록",
            description = "로그인 사용자의 보유 아이템을 등록합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @PostMapping("/items")
    public BaseResponse<ItemCreateResponse> createItem(
            @Valid @RequestBody ItemCreateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ItemCreateResponse response = itemService.createItem(currentUserId, request);
        return BaseResponse.success(response, "아이템 등록에 성공했습니다.");
    }
}
