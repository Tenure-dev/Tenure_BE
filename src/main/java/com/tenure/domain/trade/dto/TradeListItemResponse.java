package com.tenure.domain.trade.dto;

import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "거래 목록 항목 응답")
public record TradeListItemResponse(

        @Schema(description = "거래 ID", example = "1")
        Long tradeId,

        @Schema(description = "거래 출처", example = "PURCHASE_INTENT")
        TradeSourceType sourceType,

        @Schema(description = "아이템 ID", example = "10")
        Long itemId,

        @Schema(description = "판매 상품 ID", example = "1")
        Long productId,

        @Schema(description = "구매자 사용자 ID", example = "2")
        Long buyerUserId,

        @Schema(description = "판매자 사용자 ID", example = "1")
        Long sellerUserId,

        @Schema(description = "결제 금액", example = "50000")
        Integer paymentAmount,

        @Schema(description = "거래 상태", example = "PAID")
        TradeStatus status,

        @Schema(description = "거래 생성 시각", example = "2026-07-10T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "아이템 요약")
        ItemSummaryResponse item
) {

    public static TradeListItemResponse of(Trade trade) {
        return new TradeListItemResponse(
                trade.getId(),
                trade.getSourceType(),
                trade.getItem().getId(),
                trade.getProduct() == null ? null : trade.getProduct().getId(),
                trade.getBuyer().getId(),
                trade.getSeller().getId(),
                trade.getPaymentAmount(),
                trade.getStatus().displayStatus(),
                trade.getCreatedAt(),
                ItemSummaryResponse.from(trade.getItem())
        );
    }
}
