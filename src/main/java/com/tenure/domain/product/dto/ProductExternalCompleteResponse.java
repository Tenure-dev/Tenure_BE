package com.tenure.domain.product.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "External product completion response")
public record ProductExternalCompleteResponse(

        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Item ID", example = "10")
        Long itemId,

        @Schema(description = "Product status after external completion", example = "SOLD")
        ProductStatus productStatus,

        @Schema(description = "Item status after external completion", example = "SOLD")
        ItemStatus itemStatus,

        @Schema(description = "Canceled pending purchase intent count", example = "2")
        int canceledIntentCount,

        @Schema(description = "Canceled pending purchase offer count", example = "1")
        int canceledOfferCount
) {

    public static ProductExternalCompleteResponse of(
            Product product,
            Item item,
            int canceledIntentCount,
            int canceledOfferCount
    ) {
        return new ProductExternalCompleteResponse(
                product.getId(),
                item.getId(),
                product.getProductStatus(),
                item.getItemStatus(),
                canceledIntentCount,
                canceledOfferCount
        );
    }
}
