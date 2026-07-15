package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.enums.WearingTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "아이템 상세 조회 응답")
public record ItemDetailResponse(

        @Schema(description = "아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "소유자 사용자 ID", example = "1")
        Long ownerUserId,

        @Schema(description = "브랜드명", example = "Nike")
        String brandName,

        @Schema(description = "아이템명", example = "Gray Hoodie")
        String itemName,

        @Schema(description = "상위 카테고리", example = "상의")
        String categoryLarge,

        @Schema(description = "상세 카테고리", example = "후디")
        String categorySmall,

        @Schema(description = "착용 대상", example = "UNISEX")
        WearingTarget wearingTarget,

        @Schema(description = "사이즈 체계", example = "KR")
        String sizeSystem,

        @Schema(description = "사이즈 값", example = "L")
        String sizeValue,

        @Schema(description = "대표 이미지 URL", example = "https://image.url/item.jpg")
        String representativeImageUrl,

        @Schema(description = "아이템 상태", example = "OWNED")
        ItemStatus itemStatus,

        @Schema(description = "OOTD 인증 착용 수", example = "3")
        Integer ootdVerifiedWearCount,

        @Schema(description = "최근 착용일", example = "2025-10-01")
        LocalDate lastWornAt,

        @Schema(description = "최초 보유 날짜", example = "2025-01-01")
        LocalDate firstOwnedAt,

        @Schema(description = "위시 수", example = "10")
        Integer wishCount,

        @Schema(description = "구매 제안 허용 여부", example = "true")
        Boolean purchaseOfferEnabled
) {

    public static ItemDetailResponse from(Item item) {
        return new ItemDetailResponse(
                item.getId(),
                item.getOwner().getId(),
                item.getBrandName(),
                item.getItemName(),
                item.getCategory().getParent().getName(), //Item.category가 반드시 상세 카테고리(depth 2)라는 전제
                item.getCategory().getName(),
                item.getWearingTarget(),
                item.getSizeSystem(),
                item.getSizeValue(),
                item.getRepresentativeImageUrl(),
                item.getItemStatus(),
                item.getOotdVerifiedWearCount(),
                item.getLastWornAt(),
                item.getFirstOwnedAt(),
                item.getWishCount(),
                item.getPurchaseOfferEnabled()
        );
    }
}