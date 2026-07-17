package com.tenure.domain.wish.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "위시 해제 응답")
public record WishDeleteResponse(

        @Schema(description = "아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "위시 해제 후 위시 수", example = "0")
        Integer wishCount
) {
}