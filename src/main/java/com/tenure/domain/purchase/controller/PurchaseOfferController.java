package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseOfferCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateResponse;
import com.tenure.domain.purchase.service.PurchaseOfferService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
            summary = "미판매 아이템 구매 제안 전송",
            description = "미판매 보유 아이템에 대해 1회성 구매 제안을 전송하고 결제 승인을 AUTHORIZED 상태로 시뮬레이션합니다.",
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
                            schema = @Schema(implementation = PurchaseOfferCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "구매 제안 전송 요청",
                                    value = """
                                            {
                                              "offerPrice": 360000,
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
            description = "구매 제안 전송 성공",
            content = @Content(schema = @Schema(implementation = PurchaseOfferCreateResponse.class))
    )
    @PostMapping("/items/{itemId}/offers")
    public ResponseEntity<BaseResponse<PurchaseOfferCreateResponse>> createPurchaseOffer(
            @PathVariable Long itemId,
            @Valid @RequestBody PurchaseOfferCreateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseOfferCreateResponse response = purchaseOfferService.createPurchaseOffer(
                itemId,
                currentUserId,
                request
        );
        return ResponseEntity
                .created(URI.create("/purchase-offers/" + response.offerId()))
                .body(BaseResponse.success(response, "구매 제안을 보냈습니다."));
    }
}
