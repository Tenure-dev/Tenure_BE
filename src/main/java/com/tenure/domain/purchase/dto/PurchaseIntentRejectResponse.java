package com.tenure.domain.purchase.dto;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "거래 의사 거절 응답")
public record PurchaseIntentRejectResponse(

        @Schema(description = "거래 의사 ID", example = "123")
        Long intentId,

        @Schema(description = "거래 의사 상태", example = "REJECTED")
        PurchaseIntentStatus status,

        @Schema(description = "결제 승인 상태", example = "RELEASED")
        PaymentAuthorizationStatus paymentAuthorizationStatus,

        @Schema(description = "서버 기준 시각", example = "2026-07-13T10:00:00+09:00")
        OffsetDateTime serverTime
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseIntentRejectResponse from(PurchaseIntent intent, LocalDateTime serverTime) {
        return new PurchaseIntentRejectResponse(
                intent.getId(),
                intent.getStatus(),
                intent.getPaymentAuthorizationStatus(),
                serverTime.atZone(SEOUL_ZONE).toOffsetDateTime()
        );
    }
}
