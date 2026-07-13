package com.tenure.domain.purchase.dto;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "Purchase offer reject response")
public record PurchaseOfferRejectResponse(

        @Schema(description = "Purchase offer ID", example = "123")
        Long offerId,

        @Schema(description = "Purchase offer status", example = "REJECTED")
        PurchaseOfferStatus status,

        @Schema(description = "Payment authorization status", example = "RELEASED")
        PaymentAuthorizationStatus paymentAuthorizationStatus,

        @Schema(description = "Server time", example = "2026-07-13T10:00:00+09:00")
        OffsetDateTime serverTime
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseOfferRejectResponse from(PurchaseOffer offer, LocalDateTime serverTime) {
        return new PurchaseOfferRejectResponse(
                offer.getId(),
                offer.getStatus(),
                offer.getPaymentAuthorizationStatus(),
                serverTime.atZone(SEOUL_ZONE).toOffsetDateTime()
        );
    }
}
