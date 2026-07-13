package com.tenure.domain.product.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product delete response")
public record ProductDeleteResponse(

        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Item ID", example = "10")
        Long itemId,

        @Schema(description = "Product status after hiding", example = "HIDDEN")
        ProductStatus productStatus,

        @Schema(description = "Item status after reverting sale posting", example = "OWNED")
        ItemStatus itemStatus
) {

    public static ProductDeleteResponse of(Product product, Item item) {
        return new ProductDeleteResponse(
                product.getId(),
                item.getId(),
                product.getProductStatus(),
                item.getItemStatus()
        );
    }
}
