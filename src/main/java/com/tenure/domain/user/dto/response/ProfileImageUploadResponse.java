package com.tenure.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "프로필 이미지 업로드 응답")
public record ProfileImageUploadResponse(

        @Schema(description = "저장된 이미지 URL", example = "/files/profile/abc123.jpg")
        String imageUrl
) {
}