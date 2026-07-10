package com.tenure.domain.product.dto;

import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "아이템 판매 전환 응답")
public record ProductCreateResponse(

        @Schema(description = "생성된 판매 상품 ID", example = "1")
        Long productId,

        @Schema(description = "판매 전환된 아이템 ID", example = "10")
        Long itemId,

        @Schema(description = "판매자 사용자 ID", example = "1")
        Long sellerUserId,

        @Schema(description = "판매 가격", example = "50000")
        Integer price,

        @Schema(description = "배송비", example = "0")
        Integer shippingFee,

        @Schema(description = "수수료 부담 방식", example = "SELLER_PAYS")
        FeePolicy feePolicy,

        @Schema(description = "판매 상품 상태", example = "ON_SALE")
        ProductStatus productStatus,

        @Schema(description = "아이템 상태", example = "ON_SALE")
        ItemStatus itemStatus,

        @ArraySchema(schema = @Schema(description = "공개할 OOTD ID", example = "1"))
        List<Long> attachedOotdIds
) {

    public static ProductCreateResponse of(Product product, Item item, List<Long> attachedOotdIds) {
        return new ProductCreateResponse(
                product.getId(),
                item.getId(),
                product.getSeller().getId(),
                product.getPrice(),
                product.getShippingFee(),
                product.getFeePolicy(),
                product.getProductStatus(),
                item.getItemStatus(),
                attachedOotdIds
        );
    }
}
