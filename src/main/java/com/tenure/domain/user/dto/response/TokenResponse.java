package com.tenure.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

// 로그인 성공 시 반환하는 토큰 응답 DTO. Access Token만 사용
@Schema(description = "로그인 응답 (토큰)")
public record TokenResponse(

        @Schema(description = "Access Token (JWT)", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "닉네임", example = "YuJin")
        String username
) {
}