package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.WearingTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "태그 작성용 간편 아이템 등록 응답")
public record ItemTagDraftCreateResponse(

        @Schema(description = "아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "브랜드명", example = "Levis")
        String brandName,

        @Schema(description = "아이템명", example = "LVC 1955 501")
        String itemName,

        @Schema(description = "착용 대상", example = "MENSWEAR")
        WearingTarget wearingTarget,

        @Schema(description = "최초 보유 날짜", example = "2026-05-24")
        LocalDate firstOwnedAt,

        @Schema(description = "카테고리 ID", example = "1101")
        Long categoryId
) {

    public static ItemTagDraftCreateResponse of(Item item) {
        return new ItemTagDraftCreateResponse(
                item.getId(),
                item.getBrandName(),
                item.getItemName(),
                item.getWearingTarget(),
                item.getFirstOwnedAt(),
                item.getCategory().getId()
        );
    }
}