package com.tenure.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "닉네임 중복 확인 응답")
public record UsernameCheckResponse(

        @Schema(description = "사용 가능 여부 (true=사용가능, false=중복)", example = "true")
        boolean available
) {
}