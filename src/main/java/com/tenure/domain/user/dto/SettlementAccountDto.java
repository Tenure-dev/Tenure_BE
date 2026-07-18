package com.tenure.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// 정산 계좌 정보
// 계좌번호는 Service에서 암호화된 값으로 저장
@Schema(description = "정산 계좌 정보")
public record SettlementAccountDto(

        @Schema(description = "은행명", example = "국민은행")
        String bankName,

        @Schema(description = "계좌번호", example = "12345678")
        String accountNumber,

        @Schema(description = "예금주", example = "박유진")
        String accountHolder
) {
}