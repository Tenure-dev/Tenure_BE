package com.tenure.domain.ootd.controller;

import com.tenure.domain.ootd.dto.OotdRepresentativeImageRequest;
import com.tenure.domain.ootd.dto.OotdRepresentativeImageResponse;
import com.tenure.domain.ootd.service.OotdRepresentativeImageService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ootd Representative Image", description = "OOTD 기반 아이템 대표 이미지 생성 API")
@RestController
@RequestMapping("/ootds")
@RequiredArgsConstructor
public class OotdRepresentativeImageController {

    private final OotdRepresentativeImageService representativeImageService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "OOTD bbox 기반 아이템 대표 이미지 생성",
            description = "태그 작성 중 새 아이템 등록에 진입할 때 호출합니다. "
                    + "프론트는 ootdId와 bbox만 보내고, 백엔드는 저장된 원본 OOTD 이미지를 기준으로 "
                    + "bbox를 포함하는 3:4 아이템 대표 이미지를 생성해 URL을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "대표 이미지 생성 성공")
    @ApiResponse(responseCode = "400", description = "bbox 범위가 올바르지 않거나 원본 이미지를 처리할 수 없음")
    @ApiResponse(responseCode = "403", description = "본인 OOTD가 아님")
    @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 OOTD")
    @PostMapping("/{ootdId}/items/representative-image")
    public BaseResponse<OotdRepresentativeImageResponse> createRepresentativeImage(
            @PathVariable Long ootdId,
            @Valid @RequestBody OotdRepresentativeImageRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        OotdRepresentativeImageResponse response = representativeImageService.createRepresentativeImage(
                currentUserId,
                ootdId,
                request
        );
        return BaseResponse.success(response, "아이템 대표 이미지가 생성되었습니다.");
    }
}
