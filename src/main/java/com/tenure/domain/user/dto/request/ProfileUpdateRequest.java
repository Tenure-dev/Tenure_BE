package com.tenure.domain.user.dto.request;

import com.tenure.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;


@Schema(description = "프로필 수정 요청 (보낸 필드만 수정)")
public record ProfileUpdateRequest(

        @Schema(description = "닉네임", example = "YuJin")
        @Size(max = 30, message = "닉네임은 30자 이하여야 합니다.")
        String username,

        @Schema(description = "성별", example = "FEMALE")
        UserGender gender,

        @Schema(description = "키(cm)", example = "170")
        @Min(value = 100, message = "키는 100cm 이상이어야 합니다.")
        @Max(value = 250, message = "키는 250cm 이하여야 합니다.")
        Integer heightCm,

        @Schema(description = "몸무게(kg)", example = "64")
        @Min(value = 20, message = "몸무게는 20kg 이상이어야 합니다.")
        @Max(value = 300, message = "몸무게는 300kg 이하여야 합니다.")
        Integer weightKg,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl
) {
}