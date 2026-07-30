package com.tenure.domain.user.dto.response;

import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "차단한 사용자 응답")
public record BlockedUserResponse(

        @Schema(description = "차단된 사용자 ID", example = "2")
        Long userId,

        @Schema(description = "닉네임", example = "testuser2")
        String username,

        @Schema(description = "프로필 이미지 URL", example = "https://image.url/profile.jpg")
        String profileImageUrl,

        @Schema(description = "차단한 시각", example = "2026-07-27T12:00:00")
        LocalDateTime blockedAt
) {

    public static BlockedUserResponse of(User blocked, LocalDateTime blockedAt) {
        return new BlockedUserResponse(
                blocked.getId(),
                blocked.getUsername(),
                blocked.getProfileImageUrl(),
                blockedAt
        );
    }
}
