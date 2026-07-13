package com.tenure.domain.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "구매 제안 전송 요청")
public record PurchaseOfferCreateRequest(

        @NotNull(message = "제안 금액은 필수입니다.")
        @PositiveOrZero(message = "제안 금액은 0원 이상이어야 합니다.")
        @Schema(description = "제안 금액", example = "360000")
        Integer offerPrice,

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
