package com.tenure.domain.product.dto;

import com.tenure.domain.common.enums.FeePolicy;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Schema(description = "아이템 판매 전환 요청")
public record ProductCreateRequest(

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

        @Size(max = 500, message = "대표 이미지 URL은 500자 이하여야 합니다.")
        @Schema(description = "대표 상품 이미지 URL", example = "https://image.url/product.jpg")
        String mainImageUrl,

        @Schema(
                description = "카테고리별 실측. 프론트에서 입력한 key/value를 그대로 저장",
                example = "{\"shoulder\":45,\"chest\":55,\"totalLength\":70}"
        )
        Map<String, Object> measurements,

        @ArraySchema(schema = @Schema(description = "상태 이상 체크", example = "NO_DEFECT"))
        List<String> conditionFlags,

        @Schema(description = "판매자 설명", example = "3회 착용했습니다.")
        String sellerDescription,

        @NotEmpty(message = "공개할 OOTD를 1개 이상 선택해주세요.")
        @ArraySchema(schema = @Schema(description = "공개할 OOTD ID", example = "1"))
        List<Long> attachedOotdIds
) {
}
