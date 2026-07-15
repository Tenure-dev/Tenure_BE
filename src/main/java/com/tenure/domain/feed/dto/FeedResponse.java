package com.tenure.domain.feed.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Feed cursor response")
public record FeedResponse(

        @ArraySchema(schema = @Schema(implementation = FeedItemResponse.class))
        List<FeedItemResponse> content,

        @Schema(description = "Next cursor createdAt. Null when hasNext is false.", example = "2026-07-14T10:00:00")
        LocalDateTime nextCursorCreatedAt,

        @Schema(description = "Next cursor OOTD ID. Null when hasNext is false.", example = "10")
        Long nextCursorId,

        @Schema(description = "Whether next page exists", example = "true")
        boolean hasNext
) {
}
