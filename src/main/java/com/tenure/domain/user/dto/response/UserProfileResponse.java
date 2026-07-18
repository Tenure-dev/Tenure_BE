package com.tenure.domain.user.dto.response;

import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.domain.user.enums.UserGender;
import com.tenure.domain.user.enums.UserGrade;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "내 정보 응답")
public record UserProfileResponse(

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "이메일", example = "1234@gmail.com")
        String email,

        @Schema(description = "닉네임", example = "YuJin")
        String username,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "성별", example = "FEMALE")
        UserGender gender,

        @Schema(description = "키(cm)", example = "170")
        Integer heightCm,

        @Schema(description = "몸무게(kg)", example = "64")
        Integer weightKg,

        @Schema(description = "등급", example = "BASIC")
        UserGrade grade,

        @Schema(description = "계정 공개 범위", example = "PUBLIC")
        AccountVisibility accountVisibility
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getProfileImageUrl(),
                user.getGender(),
                user.getHeightCm(),
                user.getWeightKg(),
                user.getGrade(),
                user.getAccountVisibility()
        );
    }
}