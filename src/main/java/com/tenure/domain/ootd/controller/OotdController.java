package com.tenure.domain.ootd.controller;

import com.tenure.domain.ootd.dto.OotdCreateResponse;
import com.tenure.domain.ootd.dto.OotdDetailResponse;
import com.tenure.domain.ootd.dto.OotdMyPostsResponse;
import com.tenure.domain.ootd.dto.OotdReactionListResponse;
import com.tenure.domain.ootd.dto.OotdRelatedResponse;
import com.tenure.domain.ootd.service.OotdDetailService;
import com.tenure.domain.ootd.service.OotdMyPostService;
import com.tenure.domain.ootd.service.OotdReactionListService;
import com.tenure.domain.ootd.service.OotdReactionService;
import com.tenure.domain.ootd.service.OotdRelatedService;
import com.tenure.domain.ootd.service.OotdService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Ootd", description = "Ootd 등록/태그 API")
@RestController
@RequestMapping("/ootds")
@RequiredArgsConstructor
public class OotdController {

    private final OotdService ootdService;
    private final OotdMyPostService ootdMyPostService;
    private final OotdRelatedService ootdRelatedService;
    private final OotdDetailService ootdDetailService;
    private final OotdReactionService ootdReactionService;
    private final OotdReactionListService ootdReactionListService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "My OOTD posts",
            description = "Returns my OOTD posts as a flat latest list for my page monthly gallery. Includes active and archived posts.",
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
            description = "My OOTD posts returned successfully.",
            content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OotdMyPostsResponse.class))
    )
    @GetMapping("/me")
    public BaseResponse<OotdMyPostsResponse> getMyPosts(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdMyPostsResponse response = ootdMyPostService.getMyPosts(
                currentUserId,
                cursorCreatedAt,
                cursorId,
                size
        );
        return BaseResponse.success(response, "내 게시물 목록을 조회했습니다.");
    }

    @Operation(
            summary = "OOTD 상세 조회",
            description = "OOTD 상세 정보를 조회합니다. 공개 계정이거나 승인된 팔로워만 비공개 계정의 게시물을 조회할 수 있습니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger/local testing용 임시 헤더.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "OOTD 상세 조회 성공",
            content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OotdDetailResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "비공개 계정이며 승인된 팔로워가 아님")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 비공개(ARCHIVED) 처리된 OOTD")
    @GetMapping("/{ootdId}")
    public BaseResponse<OotdDetailResponse> getOotdDetail(@PathVariable Long ootdId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdDetailResponse response = ootdDetailService.getOotdDetail(currentUserId, ootdId);
        return BaseResponse.success(response, "OOTD 상세를 조회했습니다.");
    }

    @Operation(
            summary = "Related OOTDs",
            description = "Returns simple MVP related OOTD sections: similarMood, sameItems, and recommended.",
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
            description = "Related OOTDs returned successfully.",
            content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OotdRelatedResponse.class))
    )
    @GetMapping("/{ootdId}/related")
    public BaseResponse<OotdRelatedResponse> getRelatedOotds(@PathVariable Long ootdId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdRelatedResponse response = ootdRelatedService.getRelatedOotds(currentUserId, ootdId);
        return BaseResponse.success(response, "관련 OOTD를 조회했습니다.");
    }

    @Operation(
            summary = "OOTD 게시",
            description = "앱 전용 카메라로 촬영한 착장 사진을 OOTD로 게시합니다. "
                    + "게시 응답은 AI 태그 분석을 기다리지 않고 즉시 반환하며, "
                    + "OOTD_CREATED 이벤트를 통해 백그라운드에서 비동기로 AI 태그 분석을 진행합니다. "
                    + "분석 결과 중 유사도가 Threshold 이상인 태그만 AUTO_UNCONFIRMED 상태로 저장됩니다.",
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
    @ApiResponse(
            responseCode = "200",
            description = "OOTD 게시 성공",
            content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OotdCreateResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "업로드 이미지 누락 또는 앱 카메라 촬영이 아닌 이미지")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<OotdCreateResponse> createOotd(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "source", required = false) String source
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdCreateResponse response = ootdService.createOotd(currentUserId, image, source);

        return BaseResponse.success(response, "OOTD가 게시되었습니다.");
    }

    @Operation(
            summary = "OOTD 삭제",
            description = "작성자 본인이 게시한 OOTD를 삭제합니다(soft delete). 삭제된 OOTD는 상세 조회, 피드, "
                    + "마이페이지, 검색, 하트/저장 목록 등 모든 목록에서 제외되며 복원 기능은 제공하지 않습니다."
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "403", description = "본인이 게시한 OOTD가 아님")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 비공개(ARCHIVED) 처리되었거나 이미 삭제된 OOTD")
    @DeleteMapping("/{ootdId}")
    public ResponseEntity<Void> deleteOotd(@PathVariable Long ootdId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ootdService.deleteOotd(currentUserId, ootdId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "OOTD 하트 등록",
            description = "로그인 사용자가 OOTD에 하트(좋아요)를 등록합니다. 이미 등록되어 있는 경우에도 동일하게 204를 반환합니다(멱등).",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger/local testing용 임시 헤더.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(responseCode = "204", description = "하트 등록 성공 또는 이미 등록되어 있어 멱등 처리됨")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 비공개(ARCHIVED) 처리되었거나 차단 관계로 조회할 수 없는 OOTD")
    @PostMapping("/{ootdId}/heart")
    public ResponseEntity<Void> heartOotd(@PathVariable Long ootdId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ootdReactionService.heartOotd(currentUserId, ootdId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "OOTD 하트 취소",
            description = "로그인 사용자가 등록했던 OOTD 하트(좋아요)를 취소합니다. 등록 이력이 없는 경우에도 동일하게 204를 반환합니다(멱등).",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger/local testing용 임시 헤더.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(responseCode = "204", description = "하트 취소 성공 또는 등록 이력이 없어 멱등 처리됨")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 비공개(ARCHIVED) 처리되었거나 차단 관계로 조회할 수 없는 OOTD")
    @DeleteMapping("/{ootdId}/heart")
    public ResponseEntity<Void> unheartOotd(@PathVariable Long ootdId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ootdReactionService.unheartOotd(currentUserId, ootdId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "OOTD 저장 등록",
            description = "로그인 사용자가 OOTD를 저장(save)합니다. 이미 저장되어 있는 경우에도 동일하게 204를 반환합니다(멱등).",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger/local testing용 임시 헤더.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(responseCode = "204", description = "저장 성공 또는 이미 저장되어 있어 멱등 처리됨")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 비공개(ARCHIVED) 처리되었거나 차단 관계로 조회할 수 없는 OOTD")
    @PostMapping("/{ootdId}/save")
    public ResponseEntity<Void> saveOotd(@PathVariable Long ootdId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ootdReactionService.saveOotd(currentUserId, ootdId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "OOTD 저장 취소",
            description = "로그인 사용자가 저장했던 OOTD 저장(save)을 취소합니다. 저장 이력이 없는 경우에도 동일하게 204를 반환합니다(멱등).",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger/local testing용 임시 헤더.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(responseCode = "204", description = "저장 취소 성공 또는 저장 이력이 없어 멱등 처리됨")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 비공개(ARCHIVED) 처리되었거나 차단 관계로 조회할 수 없는 OOTD")
    @DeleteMapping("/{ootdId}/save")
    public ResponseEntity<Void> unsaveOotd(@PathVariable Long ootdId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ootdReactionService.unsaveOotd(currentUserId, ootdId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "하트한 OOTD 목록",
            description = "로그인 사용자가 하트(좋아요)한 OOTD를 마이페이지 썸네일 그리드용으로 조회합니다. "
                    + "반응 이후 삭제되었거나 차단 관계가 된 게시물은 제외됩니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger/local testing용 임시 헤더.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "하트한 OOTD 목록 조회 성공",
            content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OotdReactionListResponse.class))
    )
    @GetMapping("/hearted")
    public BaseResponse<OotdReactionListResponse> getHeartedOotds(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdReactionListResponse response = ootdReactionListService.getHeartedOotds(
                currentUserId,
                cursorCreatedAt,
                cursorId,
                size
        );
        return BaseResponse.success(response, "하트한 OOTD 목록을 조회했습니다.");
    }

    @Operation(
            summary = "저장한 OOTD 목록",
            description = "로그인 사용자가 저장(save)한 OOTD를 마이페이지 썸네일 그리드용으로 조회합니다. "
                    + "반응 이후 삭제되었거나 차단 관계가 된 게시물은 제외됩니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger/local testing용 임시 헤더.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "저장한 OOTD 목록 조회 성공",
            content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OotdReactionListResponse.class))
    )
    @GetMapping("/saved")
    public BaseResponse<OotdReactionListResponse> getSavedOotds(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdReactionListResponse response = ootdReactionListService.getSavedOotds(
                currentUserId,
                cursorCreatedAt,
                cursorId,
                size
        );
        return BaseResponse.success(response, "저장한 OOTD 목록을 조회했습니다.");
    }
}
