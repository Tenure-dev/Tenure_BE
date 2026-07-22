package com.tenure.domain.user.dto.request;

import com.tenure.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Schema(description = "회원가입 요청")
public record SignupRequest(

        // ===== 1단계: 계정 생성 =====
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

        // ===== 2단계: 약관 동의 (모두 필수) =====
        @Schema(description = "서비스 이용약관 동의 (필수)", example = "true")
        @AssertTrue(message = "서비스 이용약관에 동의해주세요.")
        boolean termsOfServiceAgreed,

        @Schema(description = "개인정보처리방침 동의 (필수)", example = "true")
        @AssertTrue(message = "개인정보처리방침에 동의해주세요.")
        boolean privacyPolicyAgreed,

        @Schema(description = "개인정보 제3자 제공 동의 (필수)", example = "true")
        @AssertTrue(message = "개인정보 제3자 제공에 동의해주세요.")
        boolean thirdPartyAgreed,

        // ===== 3단계: 주소 등록 (첫 배송지 = 기본 배송지) =====
        @Schema(description = "주소", example = "서울 동작구 사당로 50 (숭실대학교 정보과학관)")
        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 255)
        String addressLine1,

        @Schema(description = "상세 주소", example = "104동 501호")
        @NotBlank(message = "상세 주소는 필수입니다.")
        @Size(max = 255)
        String addressLine2,

        @Schema(description = "우편번호", example = "07027")
        @Size(max = 10)
        String postalCode,

        // ===== 4단계: 프로필 작성 =====
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
        Integer weightKg,

        @Schema(description = "프로필 이미지 URL (선택)", example = "/files/profile/abc.jpg")
        String profileImageUrl
) {
}