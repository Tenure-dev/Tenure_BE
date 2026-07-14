package com.tenure.domain.ootd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Related OOTD response")
public record OotdRelatedResponse(

        @Schema(description = "Similar mood OOTDs")
        List<OotdRelatedCardResponse> similarMood,

        @Schema(description = "OOTDs grouped by same tagged items")
        List<OotdRelatedItemSectionResponse> sameItems,

        @Schema(description = "Recommended OOTDs")
        List<OotdRelatedCardResponse> recommended
) {
}
