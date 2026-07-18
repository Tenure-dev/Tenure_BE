package com.tenure.domain.item.dto;

import com.tenure.domain.item.entity.ItemHistory;
import com.tenure.domain.item.enums.AcquisitionType;
import com.tenure.domain.item.enums.EndReason;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "아이템 히스토리 응답")
public record ItemHistoryResponse(

        @Schema(description = "히스토리 ID", example = "1")
        Long historyId,

        @Schema(description = "아이템 ID", example = "1")
        Long itemId,

        @Schema(description = "해당 기간의 소유자 사용자 ID", example = "2")
        Long ownerUserId,

        @Schema(description = "취득 유형", example = "TENURE_TRADE")
        AcquisitionType acquisitionType,

        @Schema(description = "종료 사유 (현재 소유 중이면 null)", example = "TENURE_TRADE")
        EndReason endReason,

        @Schema(description = "보유 시작 시각")
        LocalDateTime startedAt,

        @Schema(description = "보유 종료 시각 (현재 소유 중이면 null)")
        LocalDateTime endedAt,

        @Schema(description = "거래 ID", example = "1")
        Long tradeId
) {

    public static ItemHistoryResponse from(ItemHistory history) {
        return new ItemHistoryResponse(
                history.getId(),
                history.getItem().getId(),
                history.getOwner().getId(),
                history.getAcquisitionType(),
                history.getEndReason(),
                history.getStartedAt(),
                history.getEndedAt(),
                history.getTrade() == null ? null : history.getTrade().getId()
        );
    }
}
