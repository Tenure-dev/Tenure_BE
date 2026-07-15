package com.tenure.domain.tag.dto.response;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdTagStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "OOTD 태그 확인완료 응답")
public record OotdTagConfirmResponse(

        @Schema(description = "OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "태그 검토 상태", example = "CONFIRMED")
        OotdTagStatus tagStatus,

        @Schema(description = "태그 확인완료 시각")
        LocalDateTime tagConfirmedAt,

        @Schema(description = "재검토 필요 여부", example = "false")
        Boolean reviewRequired,

        @Schema(description = "게시 상태", example = "ACTIVE")
        OotdPublicationStatus publicationStatus
) {

    public static OotdTagConfirmResponse of(Ootd ootd) {
        return new OotdTagConfirmResponse(
                ootd.getId(),
                ootd.getTagStatus(),
                ootd.getTagConfirmedAt(),
                ootd.getReviewRequired(),
                ootd.getPublicationStatus()
        );
    }
}
