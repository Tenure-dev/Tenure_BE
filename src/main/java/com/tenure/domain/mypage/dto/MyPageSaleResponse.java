package com.tenure.domain.mypage.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "마이페이지 판매 내역 응답")
public record MyPageSaleResponse(

        @Schema(description = "내역 ID", example = "1")
        Long historyId,

        @Schema(description = "내역 타입", example = "PURCHASE_INTENT")
        String historyType,

        @Schema(description = "아이템 ID", example = "10")
        Long itemId,

        @Schema(description = "판매 상품 ID", example = "1")
        Long productId,

        @Schema(description = "대표 이미지 URL", example = "https://image.url/item.jpg")
        String imageUrl,

        @Schema(description = "브랜드명", example = "Levis")
        String brandName,

        @Schema(description = "아이템명", example = "LVC 1955 501")
        String itemName,

        @Schema(description = "가격", example = "10000")
        Integer price,

        @Schema(description = "상태", example = "SENT")
        String status,

        @Schema(description = "생성 시각", example = "2026-03-25T12:00:00")
        LocalDateTime createdAt
) {

    //판매 상품에 대해 받은 구매 의사
    public static MyPageSaleResponse fromPurchaseIntent(PurchaseIntent intent) {
        Product product = intent.getProduct();
        Item item = product.getItem();
        PurchaseIntentStatus status = intent.getStatus();

        return new MyPageSaleResponse(
                intent.getId(),
                "PURCHASE_INTENT",
                item.getId(),
                product.getId(),
                product.getMainImageUrl(),
                item.getBrandName(),
                item.getItemName(),
                intent.getProductPrice(),
                status.name(),
                intent.getCreatedAt()
        );
    }

    // 보유 아이템에 대해 받은 구매 제안
    public static MyPageSaleResponse fromPurchaseOffer(PurchaseOffer offer) {
        Item item = offer.getItem();
        PurchaseOfferStatus status = offer.getStatus();

        return new MyPageSaleResponse(
                offer.getId(),
                "PURCHASE_OFFER",
                item.getId(),
                null,
                item.getRepresentativeImageUrl(),
                item.getBrandName(),
                item.getItemName(),
                offer.getOfferPrice(),
                status.name(),
                offer.getCreatedAt()
        );
    }

    // 실제 거래로 전환된 판매 내역
    public static MyPageSaleResponse fromTrade(Trade trade) {
        Item item = trade.getItem();
        Product product = trade.getProduct();
        TradeStatus status = trade.getStatus().displayStatus();

        String imageUrl = product == null
                ? item.getRepresentativeImageUrl()
                : product.getMainImageUrl();

        return new MyPageSaleResponse(
                trade.getId(),
                "TRADE",
                item.getId(),
                product == null ? null : product.getId(),
                imageUrl,
                item.getBrandName(),
                item.getItemName(),
                trade.getPaymentAmount(),
                status.name(),
                trade.getCreatedAt()
        );
    }
}