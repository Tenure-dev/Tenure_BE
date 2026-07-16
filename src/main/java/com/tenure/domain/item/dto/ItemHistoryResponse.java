package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.ItemHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "아이템 히스토리 응답")
public record ItemHistoryResponse(

        @Schema(description = "히스토리 ID", example = "1")
        Long historyId,

        @Schema(description = "아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "이전 소유자 사용자 ID", example = "1")
        Long previousOwnerUserId,

        @Schema(description = "현재 소유자 사용자 ID", example = "2")
        Long currentOwnerUserId,

        @Schema(description = "거래 ID", example = "1")
        Long tradeId,

        @Schema(description = "연결 OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "히스토리 타입", example = "OWNERSHIP_TRANSFER")
        String historyType,

        @Schema(description = "히스토리 설명", example = "거래 완료 후 소유권이 이전되었습니다.")
        String historyDescription,

        @Schema(description = "생성일")
        LocalDateTime createdAt
) {

    public static ItemHistoryResponse from(ItemHistory history) {
        return new ItemHistoryResponse(
                history.getId(),
                history.getItem().getId(),
                history.getPreviousOwner() == null ? null : history.getPreviousOwner().getId(),
                history.getCurrentOwner().getId(),
                history.getTrade() == null ? null : history.getTrade().getId(),
                history.getOotd() == null ? null : history.getOotd().getId(),
                history.getHistoryType(),
                history.getHistoryDescription(),
                history.getCreatedAt()
        );
    }
}