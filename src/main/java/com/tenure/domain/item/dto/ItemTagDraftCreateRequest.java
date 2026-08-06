package com.tenure.domain.item.dto;

import com.tenure.domain.item.enums.WearingTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "태그 작성용 간편 아이템 등록 요청")
public record ItemTagDraftCreateRequest(

        @Schema(description = "브랜드명", example = "Levis")
        @NotBlank(message = "브랜드명은 필수입니다.")
        String brandName,

        @Schema(description = "아이템명", example = "LVC 1955 501")
        @NotBlank(message = "아이템명은 필수입니다.")
        String itemName,

        @Schema(description = "착용 대상", example = "MENSWEAR")
        @NotNull(message = "착용 대상은 필수입니다.")
        WearingTarget wearingTarget,

        @Schema(description = "최초 보유 날짜", example = "2026-05-24")
        LocalDate firstOwnedAt,

        @Schema(description = "대표 이미지 URL", example = "/files/items/abc123.jpg")
        String representativeImageUrl,

        @Schema(description = "상위 카테고리. 값이 없으면 AI 분류 대기 카테고리로 저장", example = "상의")
        String categoryLarge,

        @Schema(description = "상세 카테고리. 값이 없으면 AI 분류 대기 카테고리로 저장", example = "반팔 티셔츠")
        String categorySmall
) {
}