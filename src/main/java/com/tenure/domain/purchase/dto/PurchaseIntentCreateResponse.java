package com.tenure.domain.purchase.dto;

import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

        Amounts amounts
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseIntentCreateResponse from(PurchaseIntent intent) {
        return new PurchaseIntentCreateResponse(
                intent.getId(),
                intent.getStatus(),
                intent.getExpiresAt().atZone(SEOUL_ZONE).toOffsetDateTime(),
                new Amounts(
                        intent.getProductPrice(),
                        intent.getBuyerShippingFee(),
                        intent.getBuyerServiceFee(),
                        intent.getSellerServiceFee(),
                        intent.getTotalPaymentAmount(),
                        intent.getSellerSettlementAmount()
                )
        );
    }

    @Schema(description = "거래 의사 금액 명세")
    public record Amounts(
            @Schema(description = "상품 금액", example = "360000")
            Integer productAmount,

            @Schema(description = "구매자 부담 배송비", example = "5000")
            Integer shippingFee,

            @Schema(description = "구매자 부담 수수료", example = "0")
            Integer buyerServiceFee,

            @Schema(description = "판매자 부담 수수료", example = "21600")
            Integer sellerServiceFee,

            @Schema(description = "구매자 결제 예정 금액", example = "365000")
            Integer buyerPaymentAmount,

            @Schema(description = "판매자 정산 예정 금액", example = "343400")
            Integer sellerSettlementAmount
    ) {
    }
}
