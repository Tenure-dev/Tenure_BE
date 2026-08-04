package com.tenure.domain.trade.dto;

import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.DeliveryCarrier;
import com.tenure.domain.trade.enums.TradeAction;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.user.entity.User;
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

        @Schema(description = "deliveryCarrier가 OTHER일 때 실제 택배사명", example = "로젠택배")
        String customDeliveryCarrierName,

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

        @Schema(description = "수령인 이름", example = "홍길동")
        String deliveryReceiverName,

        @Schema(description = "수령인 연락처", example = "010-1234-5678")
        String deliveryPhone,

        @Schema(description = "배송지 주소", example = "서울시 강남구")
        String deliveryAddressLine1,

        @Schema(description = "배송지 상세 주소", example = "101동 101호")
        String deliveryAddressLine2,

        @Schema(description = "우편번호", example = "12345")
        String deliveryPostalCode,

        @Schema(description = "배송 요청사항", example = "문 앞에 놓아주세요")
        String deliveryRequestNote,

        @Schema(description = "거래 요청사항", example = "상품 상태가 사진과 동일하다면 바로 거래 진행하고 싶어요. 확인 후 답변 부탁드립니다.")
        String tradeRequestNote,

        @Schema(description = "발송 시각", example = "2026-07-11T09:00:00")
        LocalDateTime shippedAt,

        @Schema(description = "배송 완료 시각", example = "2026-07-12T15:00:00")
        LocalDateTime deliveredAt,

        @Schema(description = "구매 확정 시각", example = "2026-07-15T10:00:00")
        LocalDateTime confirmedAt,

        @Schema(description = "정산 완료 시각", example = "2026-07-15T10:00:01")
        LocalDateTime settledAt,

        @Schema(description = "거래 생성 시각", example = "2026-07-10T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "거래 수정 시각", example = "2026-07-10T12:00:00")
        LocalDateTime updatedAt,

        Counterpart counterpart
) {

    public static TradeDetailResponse of(
            Trade trade,
            TradeViewerMode viewerMode,
            List<TradeAction> availableActions,
            long counterpartFollowerCount
    ) {
        boolean isBuyerView = viewerMode == TradeViewerMode.BUYER;
        User counterpartUser = isBuyerView ? trade.getSeller() : trade.getBuyer();
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
                trade.getStatus().displayStatus(),
                trade.getDeliveryCarrier(),
                trade.getCustomDeliveryCarrierName(),
                trade.getTrackingNumber(),
                trade.getItemPrice(),
                trade.getBuyerShippingFee(),
                isBuyerView ? trade.getBuyerServiceFee() : null,
                isBuyerView ? trade.getPaymentAmount() : null,
                isBuyerView ? null : trade.getSellerServiceFee(),
                isBuyerView ? null : trade.getSettlementAmount(),
                trade.getDeliveryReceiverName(),
                trade.getDeliveryPhone(),
                trade.getDeliveryAddressLine1(),
                trade.getDeliveryAddressLine2(),
                trade.getDeliveryPostalCode(),
                trade.getDeliveryRequestNote(),
                trade.getTradeRequestNote(),
                trade.getShippedAt(),
                trade.getDeliveredAt(),
                trade.getConfirmedAt(),
                trade.getSettledAt(),
                trade.getCreatedAt(),
                trade.getUpdatedAt(),
                Counterpart.from(counterpartUser, counterpartFollowerCount)
        );
    }

    @Schema(description = "상대방(구매자 화면이면 판매자, 판매자 화면이면 구매자) 요약")
    public record Counterpart(
            @Schema(description = "상대방 사용자 ID", example = "2")
            Long userId,

            @Schema(description = "상대방 아이디", example = "Sujun")
            String username,

            @Schema(description = "상대방 프로필 이미지 URL", example = "https://image.url/profile.jpg")
            String profileImageUrl,

            @Schema(description = "상대방 팔로워 수", example = "1400")
            long followerCount
    ) {

        static Counterpart from(User counterpartUser, long followerCount) {
            return new Counterpart(
                    counterpartUser.getId(),
                    counterpartUser.getUsername(),
                    counterpartUser.getProfileImageUrl(),
                    followerCount
            );
        }
    }
}
