package com.tenure.domain.tag.dto.response;

import com.tenure.domain.tag.entity.OotdTag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "OOTD 태그 일괄 등록 응답")
public record OotdTagBatchResponse(

        @Schema(description = "OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "저장된 태그 수", example = "3")
        int savedCount,

        @Schema(description = "저장된 태그 목록")
        List<OotdTagResponse> tags
) {

    public static OotdTagBatchResponse of(Long ootdId, List<OotdTag> tags) {
        List<OotdTagResponse> tagResponses = tags.stream().map(OotdTagResponse::of).toList();
        return new OotdTagBatchResponse(ootdId, tagResponses.size(), tagResponses);
    }
}
