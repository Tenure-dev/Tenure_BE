package com.tenure.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// 비밀번호 재설정 인증번호 발송 요청
@Schema(description = "비밀번호 재설정 인증번호 발송 요청")
public record PasswordResetSendRequest(

        @Schema(description = "가입한 이메일", example = "1234@gmail.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효하지 않은 이메일입니다.")
        String email
) {
}