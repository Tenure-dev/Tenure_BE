package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.enums.WearingTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "아이템 등록 응답")
public record ItemCreateResponse(

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

        @Schema(description = "최초 보유 날짜", example = "2025-10-01")
        LocalDate firstOwnedAt
) {

    public static ItemCreateResponse of(Item item) {
        return new ItemCreateResponse(
                item.getId(),
                item.getOwner().getId(),
                item.getBrandName(),
                item.getItemName(),
                item.getCategory().getParent().getName(), // 상위 카테고리 ex) 상의
                item.getCategory().getName(), //상세 카테고리 ex) 후디
                item.getWearingTarget(),
                item.getSizeSystem(),
                item.getSizeValue(),
                item.getRepresentativeImageUrl(),
                item.getItemStatus(),
                item.getFirstOwnedAt()
        );
    }
}
