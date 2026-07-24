package com.tenure.domain.trade.controller;

import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.service.PurchaseIntentAcceptService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PurchaseIntent", description = "거래 의사 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PurchaseIntentAcceptController {

    private final PurchaseIntentAcceptService purchaseIntentAcceptService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "거래 의사 수락",
            description = "판매자가 응답 대기 중인 거래 의사를 수락하고 결제 승인 상태를 CAPTURED로 변경한 뒤 거래를 생성합니다. "
                    + "같은 상품에 대해 응답 대기 중인 다른 거래 의사는 모두 취소 처리됩니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "거래 의사 수락 성공",
            content = @Content(schema = @Schema(implementation = TradeDetailResponse.class))
    )
    @PostMapping("/purchase-intents/{intentId}/accept")
    public ResponseEntity<BaseResponse<TradeDetailResponse>> acceptPurchaseIntent(
            @PathVariable Long intentId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        TradeDetailResponse response = purchaseIntentAcceptService.acceptPurchaseIntent(intentId, currentUserId);
        return ResponseEntity
                .created(URI.create("/trades/" + response.tradeId()))
                .body(BaseResponse.success(response, "거래 의사를 수락했습니다."));
    }
}
