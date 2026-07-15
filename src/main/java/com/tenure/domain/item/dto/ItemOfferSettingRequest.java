package com.tenure.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "구매 제안 허용 설정 요청")
public record ItemOfferSettingRequest(

        @NotNull(message = "구매 제안 허용 여부는 필수입니다.")
        @Schema(description = "구매 제안 허용 여부", example = "true")
        Boolean purchaseOfferEnabled
) {
}