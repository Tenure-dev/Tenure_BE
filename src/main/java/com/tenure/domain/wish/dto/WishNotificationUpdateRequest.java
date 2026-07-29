package com.tenure.domain.wish.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema (description = "위시 알림 설정 변경 요청")
public record WishNotificationUpdateRequest (

    @NotNull(message = "알림 설정 여부는 필수입니다.")
    @Schema(description = "알림 활성화 여부", example = "true")
    Boolean notificationEnabled
        ){
}
