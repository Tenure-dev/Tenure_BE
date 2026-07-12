package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseIntentCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseIntentCreateResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentDetailResponse;
import com.tenure.domain.purchase.service.PurchaseIntentService;
import com.tenure.global.response.BaseResponse;
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
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID. JWT 적용 후에는 SecurityContext 값을 사용합니다.",
                            example = "2"
                    )
            },
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
            content = @Content(
                    schema = @Schema(implementation = PurchaseIntentCreateResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "success": true,
                                      "code": "COMMON_200",
                                      "message": "거래 의사를 보냈습니다.",
                                      "data": {
                                        "intentId": 123,
                                        "status": "SENT",
                                        "expiresAt": "2026-07-12T15:00:00+09:00",
                                        "amounts": {
                                          "productAmount": 360000,
                                          "shippingFee": 5000,
                                          "buyerServiceFee": 0,
                                          "sellerServiceFee": 21600,
                                          "buyerPaymentAmount": 365000,
                                          "sellerSettlementAmount": 343400
                                        }
                                      }
                                    }
                                    """
                    )
            )
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
            summary = "거래 의사 상세 조회",
            description = "구매자 본인 또는 판매자가 거래 의사 응답 대기 상세를 조회합니다. SENT 상태가 만료된 경우 조회 시점에 EXPIRED와 RELEASED로 갱신합니다.",
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
            description = "거래 의사 상세 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = PurchaseIntentDetailResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "success": true,
                                      "code": "COMMON_200",
                                      "message": "조회에 성공했습니다.",
                                      "data": {
                                        "intentId": 123,
                                        "status": "SENT",
                                        "viewerRole": "BUYER",
                                        "serverTime": "2026-07-12T15:00:00+09:00",
                                        "expiresAt": "2026-07-13T15:00:00+09:00",
                                        "remainingSeconds": 86400,
                                        "paymentAuthorizationStatus": "AUTHORIZED",
                                        "product": {
                                          "productId": 10,
                                          "itemId": 5,
                                          "brandName": "Nike",
                                          "itemName": "Black Jacket",
                                          "imageUrl": "https://image.url/product.jpg"
                                        },
                                        "buyer": {
                                          "userId": 2,
                                          "username": "GilDong",
                                          "profileImageUrl": "https://image.url/buyer.jpg"
                                        },
                                        "seller": {
                                          "userId": 1,
                                          "username": "YuJin",
                                          "profileImageUrl": "https://image.url/seller.jpg"
                                        },
                                        "amounts": {
                                          "productAmount": 360000,
                                          "shippingFee": 5000,
                                          "buyerServiceFee": 0,
                                          "buyerPaymentAmount": 365000
                                        },
                                        "delivery": {
                                          "receiverName": "Buyer",
                                          "phone": "010-1234-5678",
                                          "addressLine1": "Seoul Gangnam",
                                          "addressLine2": "101",
                                          "postalCode": "12345",
                                          "requestNote": "Leave at door"
                                        },
                                        "deliveryDisclosureStatus": "VISIBLE",
                                        "paymentMethodId": "MOCK_CARD"
                                      }
                                    }
                                    """
                    )
            )
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
}
