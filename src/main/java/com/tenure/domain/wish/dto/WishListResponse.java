package com.tenure.domain.wish.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.wish.entity.Wish;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "위시리스트 조회 응답")
public record WishListResponse(

        @Schema(description = "위시 ID", example = "1")
        Long wishId,

        @Schema(description = "아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "브랜드명", example = "Nike")
        String brandName,

        @Schema(description = "아이템명", example = "Gray Hoodie")
        String itemName,

        @Schema(description = "대표 이미지 URL", example = "https://image.url/item.jpg")
        String representativeImageUrl,

        @Schema(description = "아이템 상태", example = "OWNED")
        ItemStatus itemStatus,

        @Schema(description = "판매 상품 상태. 판매 상품이 없으면 null", example = "ON_SALE")
        ProductStatus saleStatus,

        @Schema(description = "판매 가격. 판매 상품이 없으면 null", example = "50000")
        Integer price,

        @Schema(description = "구매 제안 허용 여부", example = "true")
        Boolean purchaseOfferEnabled,

        @Schema(description = "알림 활성화 여부", example = "true")
        Boolean notificationEnabled,

        @Schema(description = "위시 수", example = "10")
        Integer wishCount
) {

    public static WishListResponse of(Wish wish, Product product) {
        Item item = wish.getItem();

        return new WishListResponse(
                wish.getId(),
                item.getId(),
                item.getBrandName(),
                item.getItemName(),
                item.getRepresentativeImageUrl(),
                item.getItemStatus(),
                product == null ? null : product.getProductStatus(),
                product == null ? null : product.getPrice(),
                item.getPurchaseOfferEnabled(),
                wish.getNotificationEnabled(),
                item.getWishCount()
        );
    }
}