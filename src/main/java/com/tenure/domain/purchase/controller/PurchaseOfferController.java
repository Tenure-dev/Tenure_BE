package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseOfferSentListResponse;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.service.PurchaseOfferService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PurchaseOffer", description = "구매 제안 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PurchaseOfferController {

    private final PurchaseOfferService purchaseOfferService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "내가 보낸 구매 제안 목록 조회",
            description = "로그인 사용자가 미판매 아이템에 보낸 구매 제안 목록을 커서 기반으로 조회합니다. SENT 만료 요청은 조회 시 EXPIRED와 RELEASED로 보정합니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID. JWT 적용 후에는 SecurityContext 값을 사용합니다.",
                            example = "2"
                    ),
                    @Parameter(
                            name = "statuses",
                            description = "조회할 상태 목록. 예: statuses=SENT 또는 statuses=SENT,EXPIRED. 생략 시 전체 상태",
                            example = "SENT"
                    ),
                    @Parameter(
                            name = "cursorCreatedAt",
                            description = "다음 페이지 조회용 생성 시각 커서",
                            example = "2026-07-12T10:00:00+09:00"
                    ),
                    @Parameter(
                            name = "cursorOfferId",
                            description = "다음 페이지 조회용 구매 제안 ID 커서",
                            example = "123"
                    ),
                    @Parameter(
                            name = "size",
                            description = "페이지 크기. 기본 20, 최대 50",
                            example = "20"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "보낸 구매 제안 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = PurchaseOfferSentListResponse.class))
    )
    @GetMapping("/purchase-offers/sent")
    public BaseResponse<PurchaseOfferSentListResponse> getSentPurchaseOffers(
            @RequestParam(required = false) List<PurchaseOfferStatus> statuses,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorOfferId,
            @RequestParam(required = false) Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseOfferSentListResponse response = purchaseOfferService.getSentPurchaseOffers(
                currentUserId,
                statuses,
                cursorCreatedAt,
                cursorOfferId,
                size
        );
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }
}
