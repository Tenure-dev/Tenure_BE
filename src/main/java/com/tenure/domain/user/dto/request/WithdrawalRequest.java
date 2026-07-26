package com.tenure.domain.user.dto.request;

import com.tenure.domain.user.enums.WithdrawalReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


@Schema(description = "회원 탈퇴 요청")
public record WithdrawalRequest(

        @Schema(description = "탈퇴 사유", example = "LOW_USAGE")
        @NotNull(message = "탈퇴 사유는 필수입니다.")
        WithdrawalReason reason
) {
}