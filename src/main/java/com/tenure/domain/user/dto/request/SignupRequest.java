package com.tenure.domain.user.dto.request;

import com.tenure.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "이메일 회원가입 요청")
public record SignupRequest(

        @Schema(description = "이메일", example = "1234@gmail.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효하지 않은 이메일입니다.")
        String email,

        @Schema(description = "비밀번호 (8자 이상)", example = "password123")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @Schema(description = "비밀번호 확인", example = "password123")
        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        String passwordConfirm,

        @Schema(description = "닉네임", example = "YuJin")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 30, message = "닉네임은 30자 이하여야 합니다.")
        String username,

        @Schema(description = "성별", example = "FEMALE")
        @NotNull(message = "성별은 필수입니다.")
        UserGender gender,

        @Schema(description = "키(cm)", example = "170")
        @NotNull(message = "키는 필수입니다.")
        @Min(value = 100, message = "키는 100cm 이상이어야 합니다.")
        @Max(value = 250, message = "키는 250cm 이하여야 합니다.")
        Integer heightCm,

        @Schema(description = "몸무게(kg)", example = "64")
        @NotNull(message = "몸무게는 필수입니다.")
        @Min(value = 20, message = "몸무게는 20kg 이상이어야 합니다.")
        @Max(value = 300, message = "몸무게는 300kg 이하여야 합니다.")
        Integer weightKg
) {
}