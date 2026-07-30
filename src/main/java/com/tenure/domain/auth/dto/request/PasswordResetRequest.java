package com.tenure.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "비밀번호 재설정 요청")
public record PasswordResetRequest(

        @Schema(description = "가입한 이메일", example = "user1@test.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효하지 않은 이메일입니다.")
        String email,

        @Schema(description = "새 비밀번호 (8자 이상)", example = "newpass123")
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String newPassword,

        @Schema(description = "새 비밀번호 확인", example = "newpass123")
        @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
        String newPasswordConfirm
) {
}