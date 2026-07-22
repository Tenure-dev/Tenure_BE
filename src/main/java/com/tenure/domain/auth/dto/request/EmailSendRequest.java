package com.tenure.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증번호 발송 요청")
public record EmailSendRequest(

        @Schema(description = "인증받을 이메일", example = "1234@gmail.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효하지 않은 이메일입니다.")
        String email
) {
}