package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PurchaseOffer", description = "구매 제안 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PurchaseOfferController {

    private final PurchaseOfferService purchaseOfferService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "구매 제안 상세 조회",
            description = "제안자 또는 아이템 소유자가 구매 제안 응답 대기 상세를 조회합니다. SENT 상태가 만료된 경우 조회 시점에 EXPIRED와 RELEASED로 갱신합니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID. JWT 적용 후에는 SecurityContext 값을 사용합니다.",
                            example = "2"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "구매 제안 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = PurchaseOfferDetailResponse.class))
    )
    @GetMapping("/purchase-offers/{offerId}")
    public BaseResponse<PurchaseOfferDetailResponse> getPurchaseOfferDetail(
            @PathVariable Long offerId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseOfferDetailResponse response = purchaseOfferService.getPurchaseOfferDetail(
                offerId,
                currentUserId
        );
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }
}
