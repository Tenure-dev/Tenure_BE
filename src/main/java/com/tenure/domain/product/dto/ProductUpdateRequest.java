package com.tenure.domain.product.dto;

import com.tenure.domain.common.enums.FeePolicy;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Schema(description = "Product update request")
public record ProductUpdateRequest(

        @Positive(message = "가격은 1원 이상이어야 합니다.")
        @Schema(description = "Product price", example = "52000")
        Integer price,

        @PositiveOrZero(message = "배송비는 0원 이상이어야 합니다.")
        @Schema(description = "Shipping fee. 0 means seller pays.", example = "3000")
        Integer shippingFee,

        @Schema(description = "Fee policy", example = "SELLER_PAYS")
        FeePolicy feePolicy,

        @Size(max = 500, message = "대표 이미지 URL은 500자 이하여야 합니다.")
        @Schema(description = "Main product image URL", example = "https://image.url/product.jpg")
        String mainImageUrl,

        @Schema(
                description = "Measurements by category. Stored as JSON.",
                example = "{\"shoulder\":45,\"chest\":55,\"totalLength\":70}"
        )
        Map<String, Object> measurements,

        @ArraySchema(schema = @Schema(description = "Condition flag", example = "STAIN"))
        List<String> conditionFlags,

        @Schema(description = "Seller description", example = "상태 설명 수정")
        String sellerDescription,

        @ArraySchema(schema = @Schema(description = "Representative OOTD ID", example = "1"))
        List<Long> attachedOotdIds
) {
}
