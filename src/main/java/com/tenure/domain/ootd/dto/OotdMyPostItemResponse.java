package com.tenure.domain.ootd.dto;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdTagStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "My OOTD post item response")
public record OotdMyPostItemResponse(

        @Schema(description = "OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "OOTD image URL", example = "https://image.url/ootd.jpg")
        String imageUrl,

        @Schema(description = "OOTD tag status", example = "AUTO_UNCONFIRMED")
        OotdTagStatus tagStatus,

        @Schema(description = "OOTD publication status", example = "ARCHIVED")
        OotdPublicationStatus publicationStatus,

        @Schema(description = "Whether this post should be dimmed in my page gallery", example = "true")
        boolean archived,

        @Schema(description = "Whether tag review is required", example = "true")
        Boolean reviewRequired,

        @Schema(description = "Tag review deadline", example = "2026-07-17T10:00:00")
        LocalDateTime reviewDeadlineAt,

        @Schema(description = "Archived date time", example = "2026-07-17T10:00:00")
        LocalDateTime archivedAt,

        @Schema(description = "Heart count", example = "12")
        Integer heartCount,

        @Schema(description = "Save count", example = "4")
        Integer saveCount,

        @Schema(description = "Whether current user hearted this OOTD", example = "true")
        boolean hearted,

        @Schema(description = "Whether current user saved this OOTD", example = "false")
        boolean saved,

        @Schema(description = "Created date time", example = "2026-07-14T10:00:00")
        LocalDateTime createdAt
) {

    public static OotdMyPostItemResponse of(Ootd ootd, boolean hearted, boolean saved) {
        return new OotdMyPostItemResponse(
                ootd.getId(),
                ootd.getImageUrl(),
                ootd.getTagStatus(),
                ootd.getPublicationStatus(),
                ootd.getPublicationStatus() == OotdPublicationStatus.ARCHIVED,
                ootd.getReviewRequired(),
                ootd.getReviewDeadlineAt(),
                ootd.getArchivedAt(),
                ootd.getHeartCount(),
                ootd.getSaveCount(),
                hearted,
                saved,
                ootd.getCreatedAt()
        );
    }
}
