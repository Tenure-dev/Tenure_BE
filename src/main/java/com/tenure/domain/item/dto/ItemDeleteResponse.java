package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "아이템 삭제 응답")
public record ItemDeleteResponse(

        @Schema(description = "삭제 처리된 아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "삭제 후 아이템 상태", example = "ARCHIVED")
        ItemStatus itemStatus
) {

    public static ItemDeleteResponse of(Item item) {
        return new ItemDeleteResponse(
                item.getId(),
                item.getItemStatus()
        );
    }
}