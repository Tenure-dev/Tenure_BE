package com.tenure.domain.user.dto.response;

import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답")
public record SignupResponse(

        @Schema(description = "생성된 사용자 ID", example = "1")
        Long userId,

        @Schema(description = "닉네임", example = "YuJin")
        String username
) {
    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getUsername());
    }
}