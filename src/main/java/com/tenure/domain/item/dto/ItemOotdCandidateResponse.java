package com.tenure.domain.item.dto;

import com.tenure.domain.ootd.entity.Ootd;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "판매 전환 대표 OOTD 후보 응답")
public record ItemOotdCandidateResponse(

        @Schema(description = "OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "OOTD 이미지 URL", example = "https://image.url/ootd.jpg")
        String imageUrl,

        @Schema(description = "OOTD 생성 시각", example = "2026-07-10T12:00:00")
        LocalDateTime createdAt
) {

    public static ItemOotdCandidateResponse from(Ootd ootd) {
        return new ItemOotdCandidateResponse(
                ootd.getId(),
                ootd.getImageUrl(),
                ootd.getCreatedAt()
        );
    }
}