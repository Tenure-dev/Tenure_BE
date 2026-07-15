package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "구매 제안 허용 설정 응답")
public record ItemOfferSettingResponse(

        @Schema(description = "아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "구매 제안 허용 여부", example = "true")
        Boolean purchaseOfferEnabled
) {

    public static ItemOfferSettingResponse from(Item item) {
        return new ItemOfferSettingResponse(
                item.getId(),
                item.getPurchaseOfferEnabled()
        );
    }
}