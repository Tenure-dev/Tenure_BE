package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseOfferCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PurchaseOffer", description = "Purchase offer API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PurchaseOfferController {

    private final PurchaseOfferService purchaseOfferService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Create purchase offer",
            description = "Creates a one-time purchase offer for an owned, non-sale item.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "2"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PurchaseOfferCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "Create purchase offer request",
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
            description = "Purchase offer created successfully.",
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

    @Operation(
            summary = "Get purchase offer detail",
            description = "Returns purchase offer waiting detail for the proposer or item owner. Expired SENT offers are updated to EXPIRED and RELEASED.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "2"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Purchase offer detail returned successfully.",
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
