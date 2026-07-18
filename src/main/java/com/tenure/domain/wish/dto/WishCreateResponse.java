package com.tenure.domain.wish.dto;

import com.tenure.domain.wish.entity.Wish;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "위시 등록 응답")
public record WishCreateResponse(

        @Schema(description = "위시 ID", example = "1")
        Long wishId,

        @Schema(description = "아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "위시 등록 사용자 ID", example = "1")
        Long userId,

        @Schema(description = "알림 활성화 여부", example = "true")
        Boolean notificationEnabled,

        @Schema(description = "아이템 위시 수", example = "10")
        Integer wishCount
) {

    public static WishCreateResponse from(Wish wish) {
        return new WishCreateResponse(
                wish.getId(),
                wish.getItem().getId(),
                wish.getUser().getId(),
                wish.getNotificationEnabled(),
                wish.getItem().getWishCount()
        );
    }
}