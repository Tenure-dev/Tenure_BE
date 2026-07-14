package com.tenure.domain.ootd.dto;

import com.tenure.domain.ootd.entity.Ootd;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Related OOTD card response")
public record OotdRelatedCardResponse(

        @Schema(description = "OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "OOTD image URL", example = "https://image.url/ootd.jpg")
        String imageUrl,

        @Schema(description = "OOTD created date time", example = "2026-07-14T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Author user ID", example = "3")
        Long userId,

        @Schema(description = "Author username", example = "Sujun")
        String username,

        @Schema(description = "Author profile image URL", example = "https://image.url/profile.jpg")
        String profileImageUrl
) {

    public static OotdRelatedCardResponse of(Ootd ootd) {
        return new OotdRelatedCardResponse(
                ootd.getId(),
                ootd.getImageUrl(),
                ootd.getCreatedAt(),
                ootd.getOwner().getId(),
                ootd.getOwner().getUsername(),
                ootd.getOwner().getProfileImageUrl()
        );
    }
}
