package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseOfferCancelResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
            summary = "Cancel purchase offer",
            description = "The proposer cancels a SENT purchase offer and releases the mock payment authorization. The one-time offer chance is not restored.",
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
            description = "Purchase offer canceled successfully.",
            content = @Content(schema = @Schema(implementation = PurchaseOfferCancelResponse.class))
    )
    @PostMapping("/purchase-offers/{offerId}/cancel")
    public BaseResponse<PurchaseOfferCancelResponse> cancelPurchaseOffer(
            @PathVariable Long offerId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseOfferCancelResponse response = purchaseOfferService.cancelPurchaseOffer(
                offerId,
                currentUserId
        );
        return BaseResponse.success(response, "구매 제안을 취소했습니다.");
    }
}
