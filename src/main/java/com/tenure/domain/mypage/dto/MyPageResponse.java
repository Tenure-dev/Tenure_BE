package com.tenure.domain.mypage.dto;

import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마이페이지 조회 응답")
public record MyPageResponse(

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "프로필 이미지 URL", example = "https://image.url/profile.jpg")
        String profileImageUrl,

        @Schema(description = "사용자 이름", example = "Suijun")
        String username,

        @Schema(description = "사용자 등급", example = "RECORD")
        UserGrade grade,

        @Schema(description = "키(cm)", example = "168")
        Integer heightCm,

        @Schema(description = "몸무게(kg)", example = "58")
        Integer weightKg,

        @Schema(description = "피드 수", example = "60")
        long feedCount,

        @Schema(description = "아이템 수", example = "16")
        long itemCount,

        @Schema(description = "위시 수", example = "4")
        long wishCount,

        @Schema(description = "팔로워 수", example = "201")
        long followerCount
) {
    public static MyPageResponse of(
            User user,
            long feedCount,
            long itemCount,
            long wishCount,
            long followerCount
    ) {
        return new MyPageResponse(
                user.getId(),
                user.getProfileImageUrl(),
                user.getUsername(),
                user.getGrade(),
                user.getHeightCm(),
                user.getWeightKg(),
                feedCount,
                itemCount,
                wishCount,
                followerCount
        );
    }
}