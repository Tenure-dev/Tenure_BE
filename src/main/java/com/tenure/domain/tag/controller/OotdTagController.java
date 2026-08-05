package com.tenure.domain.tag.controller;

import com.tenure.domain.tag.dto.request.OotdTagAnalyzeRequest;
import com.tenure.domain.tag.dto.request.OotdTagBatchRequest;
import com.tenure.domain.tag.dto.request.OotdTagCreateRequest;
import com.tenure.domain.tag.dto.response.OotdTagAnalyzeResponse;
import com.tenure.domain.tag.dto.response.OotdTagBatchResponse;
import com.tenure.domain.tag.dto.response.OotdTagResponse;
import com.tenure.domain.tag.dto.request.OotdTagUpdateRequest;
import com.tenure.domain.tag.dto.response.OotdTagConfirmResponse;
import com.tenure.domain.tag.dto.response.SimilarItemResponse;
import com.tenure.domain.tag.service.OotdTagService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
                    + "직접 등록하는 태그는 항상 CONFIRMED 상태로 저장됩니다."
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
                    + "수정된 태그는 사용자가 직접 확인한 것으로 간주되어 status가 CONFIRMED로 함께 변경됩니다."
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
            summary = "OOTD 태그 일괄 등록",
            description = "OOTD 게시 시점에 사용자가 최종 선택한 태그들을 한 번에 등록합니다. "
                    + "기존에 달려있던 태그는 모두 삭제되고 요청 목록으로 대체되며, "
                    + "일괄 등록되는 태그는 항상 CONFIRMED 상태로 저장됩니다."
    )
    @ApiResponse(responseCode = "200", description = "일괄 등록 성공")
    @ApiResponse(responseCode = "400", description = "필수 파라미터 누락 또는 유효하지 않은 아이템이 포함됨")
    @ApiResponse(responseCode = "403", description = "본인이 게시한 OOTD가 아님")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 OOTD")
    @PostMapping("/{ootdId}/tags/batch")
    public BaseResponse<OotdTagBatchResponse> createTagsBatch(
            @PathVariable Long ootdId,
            @Valid @RequestBody OotdTagBatchRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdTagBatchResponse response = ootdTagService.createTagsBatch(ootdId, currentUserId, request);
        return BaseResponse.success(response, "태그가 일괄 등록되었습니다.");
    }

    @Operation(
            summary = "OOTD 박스 영역 AI 분석",
            description = "태그 작성 화면에서 사용자가 아이템 위치에 박스를 그리면, 그 영역만 분석해서 "
                    + "라벨과 카테고리를 추론하고 보유 아이템 중 일치하는 것이 있으면 itemId를 함께 알려줍니다. "
                    + "분석만 수행하며 태그를 저장하지 않으므로, 매칭된 itemId로 실제 태그를 저장하려면 "
                    + "이 응답을 이용해 POST /ootds/{ootdId}/tags를 별도로 호출해야 합니다."
    )
    @ApiResponse(responseCode = "200", description = "분석 성공")
    @ApiResponse(responseCode = "403", description = "본인이 게시한 OOTD가 아님")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 OOTD")
    @PostMapping("/{ootdId}/tags/analyze")
    public BaseResponse<OotdTagAnalyzeResponse> analyzeTagArea(
            @PathVariable Long ootdId,
            @Valid @RequestBody OotdTagAnalyzeRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdTagAnalyzeResponse response = ootdTagService.analyzeTagArea(ootdId, currentUserId, request);
        return BaseResponse.success(response, "분석이 완료되었습니다.");
    }

    @Operation(
            summary = "OOTD 태그 작성용 유사 아이템 추천",
            description = "OOTD 태그 작성 화면에서 태그로 연결할 수 있도록, 로그인 사용자의 보유 아이템 중 상위 limit개를 추천합니다. "
                    + "ootdId를 함께 전달하면 해당 OOTD에 이미 확정된 태그의 카테고리/브랜드와 겹치는 보유 아이템을 우선 추천합니다."
    )
    @ApiResponse(responseCode = "200", description = "유사 아이템 추천 목록 조회 성공")
    @ApiResponse(responseCode = "401", description = "로그인이 필요함")
    @GetMapping("/tags/similar-items")
    public BaseResponse<List<SimilarItemResponse>> getSimilarItemsForTagging(
            @RequestParam(required = false) Long ootdId,
            @RequestParam(required = false) Integer limit
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        List<SimilarItemResponse> response = ootdTagService.getSimilarItemsForTagging(currentUserId, ootdId, limit);
        return BaseResponse.success(response, "유사 아이템 추천 목록을 조회했습니다.");
    }

    @Operation(
            summary = "OOTD 태그 확인완료",
            description = "OOTD에 달린 태그들을 최종 확인완료(CONFIRMED) 처리합니다. "
                    + "태그 상태를 CONFIRMED로 변경하고, tagConfirmedAt을 기록하며, reviewRequired를 해제합니다. "
                    + "OOTD가 보관(ARCHIVED) 상태였다면 ACTIVE로 복구합니다."
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

    @Operation(
            summary = "[Mock] AI 태그 생성 시뮬레이션",
            description = "실제 AI 분석 서버 연동 전, 비동기 처리 및 태그 확인완료/수정 플로우 테스트를 위해 "
                    + "가짜 AI 태그(source=AI, status=AUTO_UNCONFIRMED, 신뢰도 0.85 이상)를 생성합니다."
    )
    @ApiResponse(responseCode = "200", description = "Mock 태그 생성 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 OOTD")
    @PostMapping("/{ootdId}/ai-tags/mock")
    public BaseResponse<List<OotdTagResponse>> generateMockAiTags(@PathVariable Long ootdId) {
        List<OotdTagResponse> response = ootdTagService.generateMockAiTags(ootdId);
        return BaseResponse.success(response, "Mock AI 태그가 생성되었습니다.");
    }
}
