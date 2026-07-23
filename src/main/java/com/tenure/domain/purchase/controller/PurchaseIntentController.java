package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseIntentCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseIntentCreateResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentCancelResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentDetailResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentRejectResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentReceivedListResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentSentListResponse;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.service.PurchaseIntentService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PurchaseIntent", description = "거래 의사 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PurchaseIntentController {

    private final PurchaseIntentService purchaseIntentService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "판매 상품 거래 의사 전송",
            description = "판매중 상품에 대한 거래 의사를 전송하고 결제 승인을 AUTHORIZED 상태로 시뮬레이션합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PurchaseIntentCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "거래 의사 전송 요청",
                                    value = """
                                            {
                                              "deliveryAddressId": 1,
                                              "paymentMethodId": "MOCK_CARD",
                                              "agreement": true
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "거래 의사 전송 성공",
            content = @Content(schema = @Schema(implementation = PurchaseIntentCreateResponse.class))
    )
    @PostMapping("/products/{productId}/purchase-intents")
    public ResponseEntity<BaseResponse<PurchaseIntentCreateResponse>> createPurchaseIntent(
            @PathVariable Long productId,
            @Valid @RequestBody PurchaseIntentCreateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseIntentCreateResponse response = purchaseIntentService.createPurchaseIntent(
                productId,
                currentUserId,
                request
        );
        return ResponseEntity
                .created(URI.create("/purchase-intents/" + response.intentId()))
                .body(BaseResponse.success(response, "거래 의사를 보냈습니다."));
    }

    @Operation(
            summary = "내가 보낸 거래 의사 목록 조회",
            description = "로그인 사용자가 판매중 상품에 보낸 거래 의사 목록을 커서 기반으로 조회합니다. SENT 만료 요청은 조회 시 EXPIRED와 RELEASED로 보정합니다.",
            parameters = {
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
                            name = "cursorIntentId",
                            description = "다음 페이지 조회용 거래 의사 ID 커서",
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
            description = "보낸 거래 의사 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = PurchaseIntentSentListResponse.class))
    )
    @GetMapping("/purchase-intents/sent")
    public BaseResponse<PurchaseIntentSentListResponse> getSentPurchaseIntents(
            @RequestParam(required = false) List<PurchaseIntentStatus> statuses,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorIntentId,
            @RequestParam(required = false) Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseIntentSentListResponse response = purchaseIntentService.getSentPurchaseIntents(
                currentUserId,
                statuses,
                cursorCreatedAt,
                cursorIntentId,
                size
        );
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }

    @Operation(
            summary = "내가 받은 거래 의사 목록 조회",
            description = "로그인 판매자가 판매중 상품에 대해 받은 거래 의사 목록을 커서 기반으로 조회합니다. SENT 만료 요청은 조회 시 EXPIRED와 RELEASED로 보정합니다.",
            parameters = {
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
                            name = "cursorIntentId",
                            description = "다음 페이지 조회용 거래 의사 ID 커서",
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
            description = "받은 거래 의사 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = PurchaseIntentReceivedListResponse.class))
    )
    @GetMapping("/purchase-intents/received")
    public BaseResponse<PurchaseIntentReceivedListResponse> getReceivedPurchaseIntents(
            @RequestParam(required = false) List<PurchaseIntentStatus> statuses,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorIntentId,
            @RequestParam(required = false) Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseIntentReceivedListResponse response = purchaseIntentService.getReceivedPurchaseIntents(
                currentUserId,
                statuses,
                cursorCreatedAt,
                cursorIntentId,
                size
        );
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }

    @Operation(
            summary = "거래 의사 상세 조회",
            description = "구매자 본인 또는 판매자가 거래 의사 응답 대기 상세를 조회합니다. SENT 상태가 만료된 경우 조회 시점에 EXPIRED와 RELEASED로 갱신합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "거래 의사 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = PurchaseIntentDetailResponse.class))
    )
    @GetMapping("/purchase-intents/{intentId}")
    public BaseResponse<PurchaseIntentDetailResponse> getPurchaseIntentDetail(
            @PathVariable Long intentId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseIntentDetailResponse response = purchaseIntentService.getPurchaseIntentDetail(
                intentId,
                currentUserId
        );
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }

    @Operation(
            summary = "거래 의사 거절",
            description = "판매자가 응답 대기 중인 거래 의사를 거절하고 결제 승인 상태를 RELEASED로 변경합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "거래 의사 거절 성공",
            content = @Content(schema = @Schema(implementation = PurchaseIntentRejectResponse.class))
    )
    @PostMapping("/purchase-intents/{intentId}/reject")
    public BaseResponse<PurchaseIntentRejectResponse> rejectPurchaseIntent(
            @PathVariable Long intentId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseIntentRejectResponse response = purchaseIntentService.rejectPurchaseIntent(
                intentId,
                currentUserId
        );
        return BaseResponse.success(response, "거래 의사를 거절했습니다.");
    }

    @Operation(
            summary = "Cancel purchase intent",
            description = "The buyer cancels a SENT purchase intent and releases the mock payment authorization."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Purchase intent canceled successfully.",
            content = @Content(schema = @Schema(implementation = PurchaseIntentCancelResponse.class))
    )
    @PostMapping("/purchase-intents/{intentId}/cancel")
    public BaseResponse<PurchaseIntentCancelResponse> cancelPurchaseIntent(
            @PathVariable Long intentId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseIntentCancelResponse response = purchaseIntentService.cancelPurchaseIntent(
                intentId,
                currentUserId
        );
        return BaseResponse.success(response, "거래 의사를 취소했습니다.");
    }
}
