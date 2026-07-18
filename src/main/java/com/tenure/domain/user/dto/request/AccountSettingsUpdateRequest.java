package com.tenure.domain.user.dto.request;

import com.tenure.domain.user.dto.SettlementAccountDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;


@Schema(description = "계정 설정 수정 요청 (보낸 필드만 수정)")
public record AccountSettingsUpdateRequest(

        @Schema(description = "기본 배송비", example = "3000")
        @Min(value = 0, message = "배송비는 0원 이상이어야 합니다.")
        Integer defaultShippingFee,

        @Schema(description = "정산 계좌 정보")
        @Valid
        SettlementAccountDto settlementAccount
) {
}