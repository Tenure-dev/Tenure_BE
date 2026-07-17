package com.tenure.domain.notification.controller;

import com.tenure.domain.notification.dto.response.NotificationCursorResponse;
import com.tenure.domain.notification.dto.response.NotificationMarkReadResponse;
import com.tenure.domain.notification.enums.NotificationCategory;
import com.tenure.domain.notification.service.NotificationService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
                            description = "알림 카테고리 필터 (null이면 전체 조회)", example = "TRADE_STATUS"),
                    @Parameter(name = "unreadOnly", in = ParameterIn.QUERY,
                            description = "읽지 않은 알림만 조회 여부", example = "false"),
                    @Parameter(name = "cursor", in = ParameterIn.QUERY,
                            description = "다음 페이지 조회용 생성 시각 커서 (첫 요청 시 생략)", example = "2026-07-13T10:00:00"),
                    @Parameter(name = "cursorId", in = ParameterIn.QUERY,
                            description = "다음 페이지 조회용 알림 ID 커서 (첫 요청 시 생략)", example = "100"),
                    @Parameter(name = "size", in = ParameterIn.QUERY,
                            description = "페이지 크기", example = "20")
            }
    )
    @GetMapping
    public BaseResponse<NotificationCursorResponse> findAllNotification(
            @RequestParam(required = false) NotificationCategory category,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor
    ) {
        NotificationCursorResponse notifications = notificationService
                .findAllNotification(currentUserProvider.getCurrentUserId(), category, unreadOnly, size, cursorId, cursor);
        return BaseResponse.success(notifications);
    }

    @Operation(
            summary = "알림 단건 읽음 처리",
            description = "특정 알림을 읽음 처리합니다. 본인 알림만 처리할 수 있습니다.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID", example = "1"),
                    @Parameter(name = "notificationId", in = ParameterIn.PATH, required = true,
                            description = "읽음 처리할 알림 ID", example = "1")
            }
    )
    @PostMapping("/{notificationId}/read")
    public BaseResponse<NotificationMarkReadResponse> markRead(@PathVariable Long notificationId) {
        NotificationMarkReadResponse response = notificationService
                .markAsRead(notificationId, currentUserProvider.getCurrentUserId());
        return BaseResponse.success(response);
    }


    @Operation(
            summary = "알림 전체 읽음 처리",
            description = "읽지 않은 모든 알림을 읽음 처리합니다.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID", example = "1")
            }
    )
    @PostMapping("/read-all")
    public BaseResponse<Void> markReadAll() {
        notificationService.markReadAll(currentUserProvider.getCurrentUserId());
        return BaseResponse.success(null);
    }

}
