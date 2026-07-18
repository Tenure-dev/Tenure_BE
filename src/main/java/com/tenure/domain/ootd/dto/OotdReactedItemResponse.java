package com.tenure.domain.ootd.dto;

import com.tenure.domain.ootd.entity.OotdReaction;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reacted OOTD thumbnail item response")
public record OotdReactedItemResponse(

        @Schema(description = "OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "OOTD image URL", example = "https://image.url/ootd.jpg")
        String imageUrl
) {

    public static OotdReactedItemResponse from(OotdReaction reaction) {
        return new OotdReactedItemResponse(
                reaction.getOotd().getId(),
                reaction.getOotd().getImageUrl()
        );
    }
}
