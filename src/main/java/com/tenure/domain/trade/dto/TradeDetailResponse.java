package com.tenure.domain.trade.dto;

import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.DeliveryCarrier;
import com.tenure.domain.trade.enums.TradeAction;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeViewerMode;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "거래 상세 응답")
public record TradeDetailResponse(

        @Schema(description = "거래 ID", example = "1")
        Long tradeId,

        @Schema(description = "조회자 화면 모드", example = "BUYER")
        TradeViewerMode viewerMode,

        @ArraySchema(schema = @Schema(description = "조회자가 수행 가능한 액션", example = "REGISTER_SHIPMENT"))
        List<TradeAction> availableActions,

        @Schema(description = "거래 출처", example = "PURCHASE_INTENT")
        TradeSourceType sourceType,

        @Schema(description = "거래 출처 ID", example = "1")
        Long sourceId,

        @Schema(description = "아이템 ID", example = "10")
        Long itemId,

        @Schema(description = "판매 상품 ID", example = "1")
        Long productId,

        @Schema(description = "구매자 사용자 ID", example = "2")
        Long buyerUserId,

        @Schema(description = "판매자 사용자 ID", example = "1")
        Long sellerUserId,

        @Schema(description = "거래 상태", example = "PAID")
        TradeStatus status,

        @Schema(description = "배송사", example = "CJ_LOGISTICS")
        DeliveryCarrier deliveryCarrier,

        @Schema(description = "운송장 번호", example = "123456789")
        String trackingNumber,

        @Schema(description = "상품가/제안가", example = "50000")
        Integer itemPrice,

        @Schema(description = "배송비", example = "0")
        Integer shippingFee,

        @Schema(description = "구매자 수수료. 역할에 따라 노출 (BUYER 뷰에서만 값 존재)", example = "1500")
        Integer buyerServiceFee,

        @Schema(description = "총 결제액. 역할에 따라 노출 (BUYER 뷰에서만 값 존재)", example = "51500")
        Integer paymentAmount,

        @Schema(description = "판매자 수수료. 역할에 따라 노출 (SELLER 뷰에서만 값 존재)", example = "1500")
        Integer sellerServiceFee,

        @Schema(description = "정산액. 역할에 따라 노출 (SELLER 뷰에서만 값 존재)", example = "48500")
        Integer settlementAmount,

        @Schema(description = "거래 생성 시각", example = "2026-07-10T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "거래 수정 시각", example = "2026-07-10T12:00:00")
        LocalDateTime updatedAt
) {

    public static TradeDetailResponse of(Trade trade, TradeViewerMode viewerMode, List<TradeAction> availableActions) {
        boolean isBuyerView = viewerMode == TradeViewerMode.BUYER;
        return new TradeDetailResponse(
                trade.getId(),
                viewerMode,
                availableActions,
                trade.getSourceType(),
                trade.getSourceId(),
                trade.getItem().getId(),
                trade.getProduct() == null ? null : trade.getProduct().getId(),
                trade.getBuyer().getId(),
                trade.getSeller().getId(),
                trade.getStatus(),
                trade.getDeliveryCarrier(),
                trade.getTrackingNumber(),
                trade.getItemPrice(),
                trade.getBuyerShippingFee(),
                isBuyerView ? trade.getBuyerServiceFee() : null,
                isBuyerView ? trade.getPaymentAmount() : null,
                isBuyerView ? null : trade.getSellerServiceFee(),
                isBuyerView ? null : trade.getSettlementAmount(),
                trade.getCreatedAt(),
                trade.getUpdatedAt()
        );
    }
}
