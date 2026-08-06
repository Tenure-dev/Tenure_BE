package com.tenure.domain.ootd.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OOTD bbox 기반 아이템 대표 이미지 생성 응답")
public record OotdRepresentativeImageResponse(

        @Schema(description = "생성된 3:4 아이템 대표 이미지 URL")
        String representativeImageUrl
) {
}
