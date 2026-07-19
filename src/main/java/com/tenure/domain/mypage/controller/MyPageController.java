package com.tenure.domain.mypage.controller;

import com.tenure.domain.mypage.dto.MyPagePurchaseResponse;
import com.tenure.domain.mypage.dto.MyPageResponse;
import com.tenure.domain.mypage.enums.MyPagePurchaseTab;
import com.tenure.domain.mypage.service.MyPagePurchaseService;
import com.tenure.domain.mypage.service.MyPageService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MyPage", description = "마이페이지 API")
@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final CurrentUserProvider currentUserProvider;
    private final MyPagePurchaseService myPagePurchaseService;

    @Operation(
            summary = "마이페이지 조회",
            description = "로그인 사용자의 프로필 정보와 마이페이지 통계 정보를 조회합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @GetMapping("/my-page")
    public BaseResponse<MyPageResponse> getMyPage() {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        MyPageResponse response = myPageService.getMyPage(currentUserId);

        return BaseResponse.success(response, "마이페이지 조회에 성공했습니다.");
    }

    @Operation(
            summary = "구매 내역 조회",
            description = "로그인 사용자의 구매 내역을 탭 조건과 페이징 조건에 따라 조회합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @GetMapping("/my-page/purchases")
    public BaseResponse<PageResponse<MyPagePurchaseResponse>> getMyPurchases(
            @RequestParam(defaultValue = "ALL") MyPagePurchaseTab tab,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<MyPagePurchaseResponse> response = myPagePurchaseService.getMyPurchases(
                currentUserId,
                tab,
                pageable
        );

        return BaseResponse.success(response, "구매 내역 조회에 성공했습니다.");
    }
}