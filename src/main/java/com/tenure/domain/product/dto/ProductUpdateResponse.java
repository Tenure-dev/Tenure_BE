package com.tenure.domain.product.dto;

import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "Product update response")
public record ProductUpdateResponse(

        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Item ID", example = "10")
        Long itemId,

        @Schema(description = "Product status", example = "ON_SALE")
        ProductStatus status,

        @Schema(description = "Product price", example = "52000")
        Integer price,

        @Schema(description = "Shipping fee", example = "3000")
        Integer shippingFee,

        @Schema(description = "Fee policy", example = "SELLER_PAYS")
        FeePolicy feePolicy,

        @Schema(description = "Main product image URL", example = "https://image.url/product.jpg")
        String mainImageUrl,

        @Schema(description = "Measurements")
        Map<String, Object> measurements,

        @ArraySchema(schema = @Schema(description = "Condition flag", example = "STAIN"))
        List<String> conditionFlags,

        @Schema(description = "Seller description", example = "상태 설명 수정")
        String sellerDescription,

        @ArraySchema(schema = @Schema(description = "Representative OOTD ID", example = "1"))
        List<Long> attachedOotdIds
) {

    public static ProductUpdateResponse of(
            Product product,
            Map<String, Object> measurements,
            List<String> conditionFlags,
            List<Long> attachedOotdIds
    ) {
        return new ProductUpdateResponse(
                product.getId(),
                product.getItem().getId(),
                product.getProductStatus(),
                product.getPrice(),
                product.getShippingFee(),
                product.getFeePolicy(),
                product.getMainImageUrl(),
                measurements,
                conditionFlags,
                product.getSellerDescription(),
                attachedOotdIds
        );
    }
}
