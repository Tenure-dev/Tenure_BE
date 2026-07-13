package com.tenure.domain.purchase.dto;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "구매 제안 전송 응답")
public record PurchaseOfferCreateResponse(

        @Schema(description = "구매 제안 ID", example = "123")
        Long offerId,

        @Schema(description = "구매 제안 상태", example = "SENT")
        PurchaseOfferStatus status,

        @Schema(description = "응답 만료 시각", example = "2026-07-14T16:00:00+09:00")
        OffsetDateTime expiresAt,

        @Schema(description = "남은 응답 시간 초 단위", example = "86400")
        Long remainingSeconds,

        @Schema(description = "결제 승인 상태", example = "AUTHORIZED")
        PaymentAuthorizationStatus paymentAuthorizationStatus,

        Amounts amounts
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseOfferCreateResponse from(PurchaseOffer offer, LocalDateTime serverNow) {
        return new PurchaseOfferCreateResponse(
                offer.getId(),
                offer.getStatus(),
                offer.getExpiresAt().atZone(SEOUL_ZONE).toOffsetDateTime(),
                Math.max(0, Duration.between(serverNow, offer.getExpiresAt()).getSeconds()),
                offer.getPaymentAuthorizationStatus(),
                new Amounts(
                        offer.getOfferPrice(),
                        offer.getProposerShippingFee(),
                        offer.getProposerServiceFee(),
                        offer.getTotalPaymentAmount(),
                        offer.getOwnerSettlementAmount()
                )
        );
    }

    @Schema(description = "구매 제안 금액 명세")
    public record Amounts(
            @Schema(description = "제안 금액", example = "360000")
            Integer offerAmount,

            @Schema(description = "제안자 부담 배송비", example = "5000")
            Integer shippingFee,

            @Schema(description = "제안자 부담 수수료", example = "21600")
            Integer proposerServiceFee,

            @Schema(description = "제안자 결제 예정 금액", example = "386600")
            Integer totalPaymentAmount,

            @Schema(description = "소유자 정산 예정 금액", example = "365000")
            Integer ownerSettlementAmount
    ) {
    }
}
