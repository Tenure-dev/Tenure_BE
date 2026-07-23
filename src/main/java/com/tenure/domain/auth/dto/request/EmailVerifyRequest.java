package com.tenure.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증번호 확인 요청")
public record EmailVerifyRequest(

        @Schema(description = "인증받는 이메일", example = "1234@gmail.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효하지 않은 이메일입니다.")
        String email,

        @Schema(description = "인증번호", example = "123456")
        @NotBlank(message = "인증번호는 필수입니다.")
        String code
) {
}