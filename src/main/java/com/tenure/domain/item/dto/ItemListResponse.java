package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "내 아이템 목록 응답")
public record ItemListResponse(

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

        @Schema(description = "OOTD 인증 착용 수", example = "3")
        Integer ootdVerifiedWearCount,

        @Schema(description = "최근 착용일", example = "2025-10-01")
        LocalDate lastWornAt,

        @Schema(description = "구매 제안 허용 여부", example = "true")
        Boolean purchaseOfferEnabled
) {

    public static ItemListResponse from(Item item) {
        return new ItemListResponse(
                item.getId(),
                item.getBrandName(),
                item.getItemName(),
                item.getRepresentativeImageUrl(),
                item.getItemStatus(),
                item.getOotdVerifiedWearCount(),
                item.getLastWornAt(),
                item.getPurchaseOfferEnabled()
        );
    }
}
