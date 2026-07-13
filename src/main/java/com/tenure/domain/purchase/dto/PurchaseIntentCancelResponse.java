package com.tenure.domain.purchase.dto;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "Purchase intent cancel response")
public record PurchaseIntentCancelResponse(

        @Schema(description = "Purchase intent ID", example = "123")
        Long intentId,

        @Schema(description = "Purchase intent status", example = "CANCELED")
        PurchaseIntentStatus status,

        @Schema(description = "Payment authorization status", example = "RELEASED")
        PaymentAuthorizationStatus paymentAuthorizationStatus,

        @Schema(description = "Server time", example = "2026-07-13T10:00:00+09:00")
        OffsetDateTime serverTime
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseIntentCancelResponse from(PurchaseIntent intent, LocalDateTime serverTime) {
        return new PurchaseIntentCancelResponse(
                intent.getId(),
                intent.getStatus(),
                intent.getPaymentAuthorizationStatus(),
                serverTime.atZone(SEOUL_ZONE).toOffsetDateTime()
        );
    }
}
