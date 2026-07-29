package com.tenure.domain.wish.dto;

import com.tenure.domain.wish.entity.Wish;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "위시 알림 설정 변경 응답")
public record WishNotificationUpdateResponse (

    @Schema(description = "아이템 ID", example = "1")
    Long itemId,

    @Schema(description = "사용자 ID", example = "2")
    Long userId,

    @Schema(description = "알림 활성화 여부", example = "false")
    Boolean notificationEnabled
) {

    public static WishNotificationUpdateResponse from(Wish wish) {
        return new WishNotificationUpdateResponse(
                wish.getItem().getId(),
                wish.getUser().getId(),
                wish.getNotificationEnabled()
        );
    }
}
