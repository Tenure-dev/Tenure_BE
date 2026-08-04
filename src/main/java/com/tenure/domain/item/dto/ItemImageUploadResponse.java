package com.tenure.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "아이템 이미지 업로드 응답")
public record ItemImageUploadResponse(

        @Schema(description = "업로드된 아이템 이미지 URL", example = "/files/items/abc123.jpg")
        String imageUrl
) {
}