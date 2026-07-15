package com.tenure.domain.item.controller;

import com.tenure.domain.item.dto.ItemCreateRequest;
import com.tenure.domain.item.dto.ItemCreateResponse;
import com.tenure.domain.item.dto.ItemDetailResponse;
import com.tenure.domain.item.dto.ItemListResponse;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.service.ItemService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.response.PageResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Item", description = "아이템 API")
@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CurrentUserProvider currentUserProvider;

    //아이템 등록
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

    //아이템 목록 조회
    @Operation(
            summary = "내 아이템 목록 조회",
            description = "로그인 사용자의 보유 아이템 목록을 조회합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @GetMapping("/items")
    public BaseResponse<PageResponse<ItemListResponse>> getMyItems(
            @RequestParam(required = false) String query, //검색어가 없어도 괜찮음.
            @RequestParam(required = false) ItemStatus itemStatus, //상태 필터가 없어도 괜찮음
            @RequestParam(defaultValue = "0") int page, //기본값으로 조회
            @RequestParam(defaultValue = "20") int size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size); //숫자로 받은 page/size를 Pageable 객체로 바꾼다.

        PageResponse<ItemListResponse> response = itemService.getMyItems(
                currentUserId,
                query,
                itemStatus,
                pageable
        );

        return BaseResponse.success(response, "내 아이템 목록 조회에 성공했습니다.");
    }

    //아이템 상세 조회
    @Operation(
            summary = "아이템 상세 조회",
            description = "itemId 기준으로 아이템 상세 정보를 조회합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @GetMapping("/items/{itemId}")
    public BaseResponse<ItemDetailResponse> getItemDetail(
            @PathVariable Long itemId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ItemDetailResponse response = itemService.getItemDetail(currentUserId, itemId);

        return BaseResponse.success(response, "아이템 상세 조회에 성공했습니다.");
    }
}
