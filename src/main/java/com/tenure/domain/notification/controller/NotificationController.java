package com.tenure.domain.notification.controller;

import com.tenure.domain.notification.dto.response.NotificationMarkReadResponse;
import com.tenure.domain.notification.dto.response.NotificationResponse;
import com.tenure.domain.notification.enums.NotificationCategory;
import com.tenure.domain.notification.service.NotificationService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.response.PageResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;


    @Operation(
            summary = "알림 목록 조회",
            description = "로그인 사용자의 알림 목록을 조회합니다. 카테고리 필터 및 읽지 않은 알림만 조회 가능합니다.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID", example = "1"),
                    @Parameter(name = "category", in = ParameterIn.QUERY,
                            description = "알림 카테고리 필터 (null이면 전체 조회)", example = "TRADE"),
                    @Parameter(name = "unreadOnly", in = ParameterIn.QUERY,
                            description = "읽지 않은 알림만 조회 여부", example = "false"),
                    @Parameter(name = "page", in = ParameterIn.QUERY,
                            description = "페이지 번호 (0부터 시작)", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY,
                            description = "페이지 크기", example = "20")
            }
    )
    @GetMapping
    public BaseResponse<PageResponse<NotificationResponse>> findAllNotification(
            @RequestParam(required = false) NotificationCategory category,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<NotificationResponse> notifications = notificationService
                .findAllNotification(currentUserProvider.getCurrentUserId(), category, unreadOnly, page, size);
        return BaseResponse.success(notifications);
    }


}
