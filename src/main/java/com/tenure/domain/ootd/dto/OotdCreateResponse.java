package com.tenure.domain.ootd.dto;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdSource;
import com.tenure.domain.ootd.enums.OotdTagStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "OOTD 게시 응답")
public record OotdCreateResponse(

        @Schema(description = "생성된 OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "게시자 사용자 ID", example = "1")
        Long ownerId,

        @Schema(description = "저장된 이미지 URL", example = "/files/ootds/3f1b1c1a-....jpg")
        String imageUrl,

        @Schema(description = "촬영 경로", example = "CAMERA")
        OotdSource source,

        @Schema(description = "AI 태그 분석 상태", example = "ANALYZING")
        OotdTagStatus tagStatus,

        @Schema(description = "게시 상태", example = "ACTIVE")
        OotdPublicationStatus publicationStatus,

        @Schema(description = "게시 시각")
        LocalDateTime createdAt
) {

    public static OotdCreateResponse of(Ootd ootd) {
        return new OotdCreateResponse(
                ootd.getId(),
                ootd.getOwner().getId(),
                ootd.getImageUrl(),
                ootd.getSource(),
                ootd.getTagStatus(),
                ootd.getPublicationStatus(),
                ootd.getCreatedAt()
        );
    }
}
