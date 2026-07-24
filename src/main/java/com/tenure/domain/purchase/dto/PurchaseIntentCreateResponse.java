package com.tenure.domain.purchase.dto;

import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "거래 의사 전송 응답")
public record PurchaseIntentCreateResponse(

        @Schema(description = "거래 의사 ID", example = "123")
        Long intentId,

        @Schema(description = "거래 의사 상태", example = "SENT")
        PurchaseIntentStatus status,

        @Schema(description = "응답 만료 시각", example = "2026-07-12T15:00:00+09:00")
        OffsetDateTime expiresAt,

        @Schema(description = "남은 응답 시간 초 단위", example = "86400")
        Long remainingSeconds,

        Amounts amounts
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseIntentCreateResponse from(PurchaseIntent intent, LocalDateTime serverNow) {
        return new PurchaseIntentCreateResponse(
                intent.getId(),
                intent.getStatus(),
                intent.getExpiresAt().atZone(SEOUL_ZONE).toOffsetDateTime(),
                Math.max(0, Duration.between(serverNow, intent.getExpiresAt()).getSeconds()),
                Amounts.from(intent)
        );
    }

    @Schema(description = "구매자용 거래 의사 금액 명세")
    public record Amounts(
            @Schema(description = "상품 금액", example = "360000")
            Integer productAmount,

            @Schema(description = "구매자 부담 배송비", example = "5000")
            Integer deliveryFee,

            @Schema(description = "구매자 부담 수수료", example = "0")
            Integer buyerServiceFee,

            @Schema(description = "구매자 결제 예정 금액", example = "365000")
            Integer buyerPaymentAmount
    ) {
        static Amounts from(PurchaseIntent intent) {
            return new Amounts(
                    intent.getProductPrice(),
                    intent.getBuyerShippingFee(),
                    intent.getBuyerServiceFee(),
                    intent.getTotalPaymentAmount()
            );
        }
    }
}
