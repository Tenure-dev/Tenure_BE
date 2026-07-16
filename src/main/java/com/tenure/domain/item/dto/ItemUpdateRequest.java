package com.tenure.domain.item.dto;

import com.tenure.domain.item.enums.WearingTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "아이템 수정 요청")
public record ItemUpdateRequest(

        @NotBlank(message = "브랜드명은 필수입니다.")
        @Size(max = 100, message = "브랜드명은 100자 이하여야 합니다.")
        @Schema(description = "브랜드명", example = "Levis")
        String brandName,

        @NotBlank(message = "아이템명은 필수입니다.")
        @Size(max = 100, message = "아이템명은 100자 이하여야 합니다.")
        @Schema(description = "아이템명", example = "LVC 1955 501")
        String itemName,

        @NotBlank(message = "상위 카테고리는 필수입니다.")
        @Schema(description = "상위 카테고리", example = "상의")
        String categoryLarge,

        @NotBlank(message = "상세 카테고리는 필수입니다.")
        @Schema(description = "상세 카테고리", example = "후디")
        String categorySmall,

        @NotNull(message = "착용 대상은 필수입니다.")
        @Schema(description = "착용 대상", example = "UNISEX")
        WearingTarget wearingTarget,

        @Size(max = 30, message = "사이즈 체계는 30자 이하여야 합니다.")
        @Schema(description = "사이즈 체계", example = "KR")
        String sizeSystem,

        @Size(max = 30, message = "사이즈 값은 30자 이하여야 합니다.")
        @Schema(description = "사이즈 값", example = "L")
        String sizeValue,

        @Schema(description = "최초 보유 날짜", example = "2025-10-01")
        LocalDate firstOwnedAt,

        @Size(max = 500, message = "대표 이미지 URL은 500자 이하여야 합니다.")
        @Schema(description = "대표 이미지 URL", example = "https://image.url/item.jpg")
        String representativeImageUrl
) {
}