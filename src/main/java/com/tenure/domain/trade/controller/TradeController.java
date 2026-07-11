package com.tenure.domain.trade.controller;

import com.tenure.domain.trade.dto.TradeListItemResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
                            description = "거래 상태 필터. 미지정 시 전체 상태를 조회합니다.",
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
                                            "createdAt": "2026-07-10T12:00:00"
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
            @RequestParam(required = false) TradeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PageResponse<TradeListItemResponse> response =
                tradeService.getTradeList(currentUserId, role, status, page, size);
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }
}
