package com.tenure.domain.feed.controller;

import com.tenure.domain.feed.dto.FeedResponse;
import com.tenure.domain.feed.service.FeedService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Feed", description = "Feed API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Get OOTD feed",
            description = "Returns OOTD cards without price or sale information. Supports all/following tabs and cursor pagination.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Feed returned successfully.",
            content = @Content(schema = @Schema(implementation = FeedResponse.class))
    )
    @GetMapping("/feed")
    public BaseResponse<FeedResponse> getFeed(
            @RequestParam(defaultValue = "all") String tab,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        FeedResponse response = feedService.getFeed(
                currentUserId,
                tab,
                userId,
                cursorCreatedAt,
                cursorId,
                size
        );
        return BaseResponse.success(response, "피드를 조회했습니다.");
    }
}
