package com.tenure.domain.feed.dto;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Feed OOTD card response")
public record FeedItemResponse(

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
        String profileImageUrl,

        @Schema(description = "Heart count", example = "12")
        Integer heartCount,

        @Schema(description = "Save count", example = "4")
        Integer saveCount,

        @Schema(description = "Whether current user hearted this OOTD", example = "true")
        boolean hearted,

        @Schema(description = "Whether current user saved this OOTD", example = "false")
        boolean saved
) {

    public static FeedItemResponse of(Ootd ootd, Set<Long> heartedOotdIds, Set<Long> savedOotdIds) {
        User owner = ootd.getOwner();
        return new FeedItemResponse(
                ootd.getId(),
                ootd.getImageUrl(),
                ootd.getCreatedAt(),
                owner.getId(),
                owner.getUsername(),
                owner.getProfileImageUrl(),
                ootd.getHeartCount(),
                ootd.getSaveCount(),
                heartedOotdIds.contains(ootd.getId()),
                savedOotdIds.contains(ootd.getId())
        );
    }
}
