package com.tenure.domain.ootd.controller;

import com.tenure.domain.ootd.dto.OotdCreateResponse;
import com.tenure.domain.ootd.dto.OotdRelatedResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Ootd", description = "Ootd 등록/태그 API")
@RestController
@RequestMapping("/api/ootds")
@RequiredArgsConstructor
public class OotdController {

    private final OotdService ootdService;
    private final OotdRelatedService ootdRelatedService;
    private final CurrentUserProvider currentUserProvider;

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
            description = "앱 내 전용 카메라로 촬영한 착장 사진을 OOTD로 게시합니다. "
                    + "게시 응답은 AI 태그 분석을 기다리지 않고 즉시 반환되며, "
                    + "OOTD_CREATED 이벤트를 통해 백그라운드에서 비동기로 AI(Gemini) 태그 분석이 진행됩니다. "
                    + "분석 결과 중 신뢰도가 Threshold 이상인 태그만 AUTO_UNCONFIRMED 상태로 저장됩니다.",
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
    @ApiResponse(responseCode = "400", description = "대표 이미지 누락 또는 앱 내 카메라 촬영이 아닌 이미지")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<OotdCreateResponse> createOotd(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "source", required = false) String source
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdCreateResponse response = ootdService.createOotd(currentUserId, image, source);

        return BaseResponse.success(response, "OOTD가 게시되었습니다.");
    }
}
