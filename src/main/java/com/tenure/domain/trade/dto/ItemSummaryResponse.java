package com.tenure.domain.trade.dto;

import com.tenure.domain.item.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "거래 목록용 아이템 요약 응답")
public record ItemSummaryResponse(

        @Schema(description = "아이템 ID", example = "10")
        Long itemId,

        @Schema(description = "아이템명", example = "Gray Hoodie")
        String itemName,

        @Schema(description = "브랜드명", example = "Nike")
        String brandName,

        @Schema(description = "대표 이미지 URL", example = "https://image.url/item.jpg")
        String representativeImageUrl
) {

    public static ItemSummaryResponse from(Item item) {
        return new ItemSummaryResponse(
                item.getId(),
                item.getItemName(),
                item.getBrandName(),
                item.getRepresentativeImageUrl()
        );
    }
}
