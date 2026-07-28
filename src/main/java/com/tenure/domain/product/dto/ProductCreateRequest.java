package com.tenure.domain.product.dto;

import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.item.enums.WearingTarget;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(description = "아이템 판매 전환 요청")
public record ProductCreateRequest(

        @NotBlank(message = "브랜드명은 필수입니다.")
        @Size(max = 100, message = "브랜드명은 100자 이하여야 합니다.")
        @Schema(description = "브랜드명", example = "Levis")
        String brandName,

        @NotBlank(message = "아이템명은 필수입니다.")
        @Size(max = 100, message = "아이템명은 100자 이하여야 합니다.")
        @Schema(description = "아이템명", example = "LVC 1955 501")
        String itemName,

        @NotBlank(message = "상위 카테고리는 필수입니다.")
        @Schema(description = "상위 카테고리", example = "하의")
        String categoryLarge,

        @NotBlank(message = "세부 카테고리는 필수입니다.")
        @Schema(description = "세부 카테고리", example = "데님")
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
        @Schema(description = "아이템 대표 이미지 URL", example = "https://image.url/item.jpg")
        String representativeImageUrl,

        @NotNull(message = "가격은 필수입니다.")
        @Positive(message = "가격은 1원 이상이어야 합니다.")
        @Schema(description = "판매 가격", example = "50000")
        Integer price,

        @NotNull(message = "배송비는 필수입니다.")
        @PositiveOrZero(message = "배송비는 0원 이상이어야 합니다.")
        @Schema(description = "배송비. 0이면 판매자 부담", example = "0")
        Integer shippingFee,

        @NotNull(message = "수수료 부담 방식은 필수입니다.")
        @Schema(description = "수수료 부담 방식", example = "SELLER_PAYS")
        FeePolicy feePolicy,

        @Size(max = 500, message = "대표 상품 이미지 URL은 500자 이하여야 합니다.")
        @Schema(description = "판매 상품 대표 이미지 URL", example = "https://image.url/product.jpg")
        String mainImageUrl,

        @Schema(
                description = "카테고리별 실측. 입력 항목은 추후 확정 예정이며 현재는 key/value 그대로 저장합니다.",
                example = "{\"shoulder\":45,\"chest\":55,\"totalLength\":70}"
        )
        Map<String, Object> measurements,

        @Valid
        @Schema(description = "상태 이상 체크")
        ProductConditionFlags conditionFlags,

        @Schema(description = "판매자 설명", example = "3회 착용했습니다.")
        String sellerDescription,

        @NotEmpty(message = "대표 OOTD를 1개 이상 선택해주세요.")
        @ArraySchema(schema = @Schema(description = "대표 OOTD ID", example = "1"))
        List<Long> attachedOotdIds
) {
}
