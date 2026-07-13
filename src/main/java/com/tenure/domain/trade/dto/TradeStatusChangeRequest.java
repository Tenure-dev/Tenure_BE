package com.tenure.domain.trade.dto;

import com.tenure.domain.trade.enums.DeliveryCarrier;
import com.tenure.domain.trade.enums.TradeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "거래 상태 변경 요청")
public record TradeStatusChangeRequest(

        @NotNull(message = "변경할 상태는 필수입니다.")
        @Schema(description = "변경할 상태", example = "SHIPPED")
        TradeStatus status,

        @Schema(description = "배송사. SHIPPED 전이에서만 사용", example = "CJ_LOGISTICS")
        DeliveryCarrier deliveryCarrier,

        @Schema(description = "운송장 번호. SHIPPED 전이에서만 사용", example = "1234567890")
        String trackingNumber,

        @Schema(description = "deliveryCarrier가 OTHER일 때 실제 택배사명. SHIPPED 전이에서만 사용", example = "로젠택배")
        String customDeliveryCarrierName
) {

    public static TradeStatusChangeRequest empty(TradeStatus status) {
        return new TradeStatusChangeRequest(status, null, null, null);
    }
}
