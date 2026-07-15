package com.tenure.domain.ootd.dto;

import com.tenure.domain.item.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Related OOTDs grouped by a tagged item")
public record OotdRelatedItemSectionResponse(

        @Schema(description = "Item ID", example = "10")
        Long itemId,

        @Schema(description = "Brand name", example = "Nike")
        String brandName,

        @Schema(description = "Item name", example = "Black Jacket")
        String itemName,

        @Schema(description = "Related OOTD cards for this item")
        List<OotdRelatedCardResponse> ootds
) {

    public static OotdRelatedItemSectionResponse of(Item item, List<OotdRelatedCardResponse> ootds) {
        return new OotdRelatedItemSectionResponse(
                item.getId(),
                item.getBrandName(),
                item.getItemName(),
                ootds
        );
    }
}
