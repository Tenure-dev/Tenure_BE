package com.tenure.domain.follow.dto.response;

import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 팔로우 목록의 각 유저 항목.
 * 화면(계정 목록)에서 프로필 사진, 닉네임, 팔로우 버튼 상태 표시에 사용한다.
 */
@Schema(description = "팔로우 목록 유저 항목")
public record FollowUserResponse(

        @Schema(description = "유저 ID", example = "2")
        Long userId,

        @Schema(description = "닉네임", example = "민지")
        String username,

        @Schema(description = "프로필 이미지 URL", example = "/files/profile/abc.jpg")
        String profileImageUrl,

        @Schema(description = "현재 로그인 유저가 이 유저를 팔로우 중인지", example = "true")
        boolean following
) {
    public static FollowUserResponse of(User user, boolean following) {
        return new FollowUserResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImageUrl(),
                following
        );
    }
}