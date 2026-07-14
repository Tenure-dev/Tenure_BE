package com.tenure.domain.ootd.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "My OOTD posts cursor response")
public record OotdMyPostsResponse(

        @ArraySchema(schema = @Schema(implementation = OotdMyPostItemResponse.class))
        List<OotdMyPostItemResponse> content,

        @Schema(description = "Next cursor createdAt. Null when hasNext is false.", example = "2026-07-14T10:00:00")
        LocalDateTime nextCursorCreatedAt,

        @Schema(description = "Next cursor OOTD ID. Null when hasNext is false.", example = "10")
        Long nextCursorId,

        @Schema(description = "Whether next page exists", example = "true")
        boolean hasNext
) {
}
