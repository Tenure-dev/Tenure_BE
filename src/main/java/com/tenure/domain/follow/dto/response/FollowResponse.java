package com.tenure.domain.follow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 팔로우/언팔로우 결과 응답.
 * 프론트가 버튼 상태(팔로우/팔로잉)를 갱신하는 데 사용한다.
 */
@Schema(description = "팔로우 상태 응답")
public record FollowResponse(

        @Schema(description = "대상 사용자 ID", example = "2")
        Long targetUserId,

        @Schema(description = "팔로우 중인지 여부", example = "true")
        boolean following,

        @Schema(description = "대상 사용자의 팔로워 수", example = "42")
        long followerCount
) {
}