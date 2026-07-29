package com.tenure.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자주 같이 입은 옷 응답")
public record ItemFrequentlyWornTogetherResponse(

        @Schema(description = "아이템 ID", example = "10")
        Long itemId,

        @Schema(description = "브랜드명", example = "Our Legacy")
        String brandName,

        @Schema(description = "아이템명", example = "knit")
        String itemName,

        @Schema(description = "대표 이미지 URL", example = "https://image.url/item.jpg")
        String representativeImageUrl,

        @Schema(description = "기준 아이템과 함께 태그된 횟수", example = "3")
        Long togetherCount
) {
}