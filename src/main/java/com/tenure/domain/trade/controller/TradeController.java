package com.tenure.domain.trade.controller;

import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.dto.TradeListItemResponse;
import com.tenure.domain.trade.dto.TradeStatusChangeRequest;
import com.tenure.domain.trade.enums.TradeRole;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.service.TradeService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.response.PageResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Trade", description = "거래 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "거래 목록 조회",
            description = "현재 사용자가 구매자 또는 판매자로 참여한 거래 목록을 조회합니다. "
                    + "role을 지정하면 해당 역할의 거래만, 지정하지 않으면 참여한 모든 거래를 반환합니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID. JWT 적용 후에는 SecurityContext 값을 사용합니다.",
                            example = "1"
                    ),
                    @Parameter(
                            name = "role",
                            in = ParameterIn.QUERY,
                            description = "조회할 역할. BUYER면 구매한 거래, SELLER면 판매한 거래만 조회하며 미지정 시 참여한 모든 거래를 조회합니다.",
                            example = "BUYER"
                    ),
                    @Parameter(
                            name = "status",
                            in = ParameterIn.QUERY,
                            description = "거래 상태 필터. 값을 여러 개 지정하면 해당 상태들만, 미지정 시 전체 상태를 조회합니다.",
                            example = "PAID"
                    ),
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            description = "페이지 번호 (0-based)",
                            example = "0"
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            description = "페이지 크기",
                            example = "20"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "거래 목록 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = TradeListItemResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "success": true,
                                      "code": "COMMON_200",
                                      "message": "조회에 성공했습니다.",
                                      "data": {
                                        "content": [
                                          {
                                            "tradeId": 1,
                                            "sourceType": "PURCHASE_INTENT",
                                            "itemId": 10,
                                            "productId": 1,
                                            "buyerUserId": 2,
                                            "sellerUserId": 1,
                                            "paymentAmount": 50000,
                                            "status": "PAID",
                                            "createdAt": "2026-07-10T12:00:00",
                                            "item": {
                                              "itemId": 10,
                                              "itemName": "Gray Hoodie",
                                              "brandName": "Nike",
                                              "representativeImageUrl": "https://image.url/item.jpg"
                                            }
                                          }
                                        ],
                                        "page": 0,
                                        "size": 20,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "hasNext": false
                                      }
                                    }
                                    """
                    )
            )
    )
    @GetMapping("/trades")
    public BaseResponse<PageResponse<TradeListItemResponse>> getTradeList(
            @RequestParam(required = false) TradeRole role,
            @RequestParam(required = false) List<TradeStatus> status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(currentUserId, role, status, page, size);
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }

    @Operation(
            summary = "거래 상세 조회",
            description = "거래 상세를 조회합니다. 현재 사용자가 거래의 구매자 또는 판매자가 아니면 거래 존재 여부 노출을 막기 위해 "
                    + "404 TRADE_404를 반환합니다.",
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
            description = "거래 상세 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = TradeDetailResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "success": true,
                                      "code": "COMMON_200",
                                      "message": "조회에 성공했습니다.",
                                      "data": {
                                        "tradeId": 1,
                                        "viewerMode": "BUYER",
                                        "availableActions": [],
                                        "sourceType": "PURCHASE_INTENT",
                                        "sourceId": 1,
                                        "itemId": 10,
                                        "productId": 1,
                                        "buyerUserId": 2,
                                        "sellerUserId": 1,
                                        "status": "PAID",
                                        "deliveryCarrier": null,
                                        "customDeliveryCarrierName": null,
                                        "trackingNumber": null,
                                        "itemPrice": 50000,
                                        "shippingFee": 0,
                                        "buyerServiceFee": 1500,
                                        "paymentAmount": 51500,
                                        "sellerServiceFee": null,
                                        "settlementAmount": null,
                                        "shippedAt": null,
                                        "deliveredAt": null,
                                        "confirmedAt": null,
                                        "settledAt": null,
                                        "createdAt": "2026-07-10T12:00:00",
                                        "updatedAt": "2026-07-10T12:00:00"
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "404", description = "거래 정보를 찾을 수 없거나 참여자가 아님")
    @GetMapping("/trades/{tradeId}")
    public BaseResponse<TradeDetailResponse> getTradeDetail(@PathVariable Long tradeId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        TradeDetailResponse response = tradeService.getTradeDetail(tradeId, currentUserId);
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }

    @Operation(
            summary = "거래 상태 변경",
            description = "거래 상태를 다음 단계로 전이합니다. status만 필수이며 deliveryCarrier/trackingNumber/"
                    + "customDeliveryCarrierName은 SHIPPED 전이에서만 사용합니다. SETTLED, COMPLETED, TRANSFERRED는 "
                    + "직접 요청할 수 없는 상태로 409를 반환합니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID. JWT 적용 후에는 SecurityContext 값을 사용합니다.",
                            example = "1"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TradeStatusChangeRequest.class),
                            examples = @ExampleObject(
                                    name = "배송 등록 요청",
                                    value = """
                                            {
                                              "status": "SHIPPED",
                                              "deliveryCarrier": "CJ_LOGISTICS",
                                              "trackingNumber": "1234567890",
                                              "customDeliveryCarrierName": null
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "거래 상태 변경 성공")
    @ApiResponse(responseCode = "400", description = "운송장 정보 누락 또는 형식 오류")
    @ApiResponse(responseCode = "403", description = "해당 상태 변경 권한 없음")
    @ApiResponse(responseCode = "404", description = "거래 정보를 찾을 수 없거나 참여자가 아님")
    @ApiResponse(responseCode = "409", description = "현재 상태에서 불가능한 상태 변경")
    @PostMapping("/trades/{tradeId}/status")
    public BaseResponse<TradeDetailResponse> changeTradeStatus(
            @PathVariable Long tradeId,
            @Valid @RequestBody TradeStatusChangeRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        TradeDetailResponse response = tradeService.changeTradeStatus(tradeId, currentUserId, request);
        return BaseResponse.success(response, "거래 상태를 변경했습니다.");
    }
}
