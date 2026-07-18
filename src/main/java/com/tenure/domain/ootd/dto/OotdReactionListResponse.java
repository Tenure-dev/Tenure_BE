package com.tenure.domain.ootd.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Reacted OOTD list cursor response")
public record OotdReactionListResponse(

        @ArraySchema(schema = @Schema(implementation = OotdReactedItemResponse.class))
        List<OotdReactedItemResponse> content,

        @Schema(description = "Next cursor reaction createdAt. Null when hasNext is false.", example = "2026-07-14T10:00:00")
        LocalDateTime nextCursorCreatedAt,

        @Schema(description = "Next cursor reaction ID. Null when hasNext is false.", example = "10")
        Long nextCursorId,

        @Schema(description = "Whether next page exists", example = "true")
        boolean hasNext
) {
}
