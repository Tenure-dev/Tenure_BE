package com.tenure.domain.user.dto.response;

import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGender;
import com.tenure.domain.user.enums.UserGrade;
import io.swagger.v3.oas.annotations.media.Schema;

// 타 사용자 프로필 조회 응답 DTO
// 이메일 제외 공개
@Schema(description = "타 사용자 프로필 응답 (공개용)")
public record PublicUserProfileResponse(

        @Schema(description = "사용자 ID", example = "2")
        Long userId,

        @Schema(description = "닉네임", example = "MinseoK")
        String username,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "성별", example = "MALE")
        UserGender gender,

        @Schema(description = "키(cm)", example = "180")
        Integer heightCm,

        @Schema(description = "몸무게(kg)", example = "72")
        Integer weightKg,

        @Schema(description = "등급", example = "BASIC")
        UserGrade grade
) {
    public static PublicUserProfileResponse from(User user) {
        return new PublicUserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImageUrl(),
                user.getGender(),
                user.getHeightCm(),
                user.getWeightKg(),
                user.getGrade()
        );
    }
}