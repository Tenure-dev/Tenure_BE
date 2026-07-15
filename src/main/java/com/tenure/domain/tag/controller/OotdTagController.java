package com.tenure.domain.tag.controller;

import com.tenure.domain.tag.dto.request.OotdTagCreateRequest;
import com.tenure.domain.tag.dto.response.OotdTagResponse;
import com.tenure.domain.tag.dto.request.OotdTagUpdateRequest;
import com.tenure.domain.tag.dto.response.OotdTagConfirmResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OotdTag", description = "OOTD 태그 API")
@RestController
@RequestMapping("/ootds")
@RequiredArgsConstructor
public class OotdTagController {

    private final OotdTagService ootdTagService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "OOTD 태그 등록",
            description = "OOTD 작성자가 직접 착장 영역(bbox)에 아이템을 태그로 등록합니다. "
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
    @PostMapping("/{ootdId}/tags")
    public BaseResponse<OotdTagResponse> createTag(
            @PathVariable Long ootdId,
            @Valid @RequestBody OotdTagCreateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdTagResponse response = ootdTagService.createManualTag(ootdId, currentUserId, request);
        return BaseResponse.success(response, "태그가 등록되었습니다.");
    }

    @Operation(
            summary = "OOTD 태그 수정",
            description = "기존 태그(AI 태그 포함)의 연결 아이템, bbox, 라벨을 수정합니다. "
                    + "수정된 태그는 사용자가 직접 확인한 것으로 간주되어 status가 CONFIRMED로 함께 변경됩니다.",
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
    @ApiResponse(responseCode = "200", description = "태그 수정 성공")
    @ApiResponse(responseCode = "400", description = "필수 파라미터 누락 또는 bbox 범위 이상")
    @ApiResponse(responseCode = "403", description = "본인이 게시한 OOTD가 아님")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 태그 또는 아이템")
    @PatchMapping("/tags/{tagId}")
    public BaseResponse<OotdTagResponse> updateTag(
            @PathVariable Long tagId,
            @Valid @RequestBody OotdTagUpdateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdTagResponse response = ootdTagService.updateTag(tagId, currentUserId, request);
        return BaseResponse.success(response, "태그가 수정되었습니다.");
    }

    @Operation(
            summary = "OOTD 태그 확인완료",
            description = "OOTD에 달린 태그들을 최종 확인완료(CONFIRMED) 처리합니다. "
                    + "태그 상태를 CONFIRMED로 변경하고, tagConfirmedAt을 기록하며, reviewRequired를 해제합니다. "
                    + "OOTD가 보관(ARCHIVED) 상태였다면 ACTIVE로 복구합니다.",
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
    @ApiResponse(responseCode = "200", description = "확인완료 처리 성공")
    @ApiResponse(responseCode = "403", description = "본인이 게시한 OOTD가 아님")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 OOTD, 또는 확인완료할 태그가 없음")
    @PostMapping("/{ootdId}/tags/confirm")
    public BaseResponse<OotdTagConfirmResponse> confirmTags(@PathVariable Long ootdId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdTagConfirmResponse response = ootdTagService.confirmTags(ootdId, currentUserId);
        return BaseResponse.success(response, "태그가 확인완료 처리되었습니다.");
    }
}
