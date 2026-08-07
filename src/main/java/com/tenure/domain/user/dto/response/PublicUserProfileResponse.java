package com.tenure.domain.user.dto.response;

import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGender;
import com.tenure.domain.user.enums.UserGrade;
import io.swagger.v3.oas.annotations.media.Schema;

// 타 사용자 프로필 조회 응답 DTO
// 이메일 제외 공개 + 프로필 헤더용 카운트 포함
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
        UserGrade grade,

        @Schema(description = "피드(공개 OOTD) 수", example = "60")
        long feedCount,

        @Schema(description = "아이템 수", example = "16")
        long itemCount,

        @Schema(description = "팔로워 수", example = "201")
        long followerCount,

        @Schema(description = "현재 로그인 사용자가 이 유저를 팔로우 중인지", example = "false")
        boolean isFollowing
) {
    public static PublicUserProfileResponse of(
            User user,
            long feedCount,
            long itemCount,
            long followerCount,
            boolean isFollowing
    ) {
        return new PublicUserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImageUrl(),
                user.getGender(),
                user.getHeightCm(),
                user.getWeightKg(),
                user.getGrade(),
                feedCount,
                itemCount,
                followerCount,
                isFollowing
        );
    }
}