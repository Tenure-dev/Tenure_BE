package com.tenure.domain.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "거래 의사 전송 요청")
public record PurchaseIntentCreateRequest(

        @NotNull(message = "배송지는 필수입니다.")
        @Schema(description = "배송지 ID", example = "1")
        Long deliveryAddressId,

        @NotBlank(message = "결제 수단은 필수입니다.")
        @Schema(description = "MVP 시뮬레이션 결제 수단 ID", example = "MOCK_CARD")
        String paymentMethodId,

        @Schema(description = "결제 및 거래 조건 동의 여부", example = "true")
        Boolean agreement
) {
}
