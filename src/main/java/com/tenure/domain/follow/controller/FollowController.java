package com.tenure.domain.follow.controller;

import com.tenure.domain.follow.dto.response.FollowResponse;
import com.tenure.domain.follow.service.FollowService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Follow", description = "팔로우 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "팔로우", description = "특정 사용자를 팔로우합니다. 승인 없이 즉시 반영됩니다.")
    @PostMapping("/{userId}/follow")
    public BaseResponse<FollowResponse> follow(@PathVariable Long userId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        FollowResponse response = followService.follow(currentUserId, userId);
        return BaseResponse.success(response, "팔로우했습니다.");
    }
}