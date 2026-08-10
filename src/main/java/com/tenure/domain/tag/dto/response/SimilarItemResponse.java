package com.tenure.domain.tag.dto.response;

import com.tenure.domain.item.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "태그 작성용 유사 아이템 추천 응답")
public record SimilarItemResponse(

        @Schema(description = "아이템 ID", example = "10")
        Long itemId,

        @Schema(description = "브랜드명", example = "Nike")
        String brandName,

        @Schema(description = "아이템명", example = "Gray Hoodie")
        String itemName,

        @Schema(description = "대표 이미지 URL", example = "https://image.url/item.jpg")
        String representativeImageUrl,

        @Schema(description = "카테고리 ID", example = "101")
        Long categoryId,

        @Schema(description = "카테고리명", example = "반팔 티셔츠")
        String categoryName,

        @Schema(description = "마지막 착용일 (착용 이력 없으면 null)", example = "2026-07-30")
        LocalDate lastWornAt,

        @Schema(description = "OOTD 인증 착용 횟수", example = "3")
        Integer ootdVerifiedWearCount
) {

    public static SimilarItemResponse of(Item item) {
        return new SimilarItemResponse(
                item.getId(),
                item.getBrandName(),
                item.getItemName(),
                item.getRepresentativeImageUrl(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                item.getLastWornAt(),
                item.getOotdVerifiedWearCount()
        );
    }
}
