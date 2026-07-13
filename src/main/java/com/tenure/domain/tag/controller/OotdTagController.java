package com.tenure.domain.tag.controller;

import com.tenure.domain.tag.dto.OotdTagCreateRequest;
import com.tenure.domain.tag.dto.OotdTagResponse;
import com.tenure.domain.tag.service.OotdTagService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OotdTag", description = "OOTD 태그 API")
@RestController
@RequestMapping("/api/ootds/{ootdId}/tags")
@RequiredArgsConstructor
public class OotdTagController {

    private final OotdTagService ootdTagService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "OOTD 태그 등록/확정",
            description = "OOTD 작성자가 직접 착장 영역(bbox)에 아이템을 태그로 등록/확정합니다. "
                    + "직접 등록하는 태그는 항상 CONFIRMED 상태로 저장됩니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID. JWT 적용 후에는 SecurityContext 값을 사용합니다.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(responseCode = "200", description = "태그 등록 성공")
    @ApiResponse(responseCode = "400", description = "필수 파라미터 누락 또는 status가 CONFIRMED가 아님")
    @ApiResponse(responseCode = "403", description = "본인이 게시한 OOTD가 아님")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 OOTD 또는 아이템")
    @PostMapping
    public BaseResponse<OotdTagResponse> createTag(
            @PathVariable Long ootdId,
            @Valid @RequestBody OotdTagCreateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdTagResponse response = ootdTagService.createManualTag(ootdId, currentUserId, request);
        return BaseResponse.success(response, "태그가 등록되었습니다.");
    }
}
