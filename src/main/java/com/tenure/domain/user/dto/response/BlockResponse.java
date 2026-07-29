package com.tenure.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record BlockResponse(
    @Schema(description = "차단 대상 유저 ID", example = "42")
    Long targetUserId,

    @Schema(description = "현재 차단 상태", example = "true")
    boolean blocked
){
  public static BlockResponse of(Long targetUserId, boolean blocked) {
    return new BlockResponse(targetUserId, blocked);
  }
}