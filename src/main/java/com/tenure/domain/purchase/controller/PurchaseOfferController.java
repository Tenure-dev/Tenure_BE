package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseOfferRejectResponse;
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
            summary = "Reject purchase offer",
            description = "The item owner rejects a SENT purchase offer and releases the mock payment authorization.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Purchase offer rejected successfully.",
            content = @Content(schema = @Schema(implementation = PurchaseOfferRejectResponse.class))
    )
    @PostMapping("/purchase-offers/{offerId}/reject")
    public BaseResponse<PurchaseOfferRejectResponse> rejectPurchaseOffer(
            @PathVariable Long offerId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseOfferRejectResponse response = purchaseOfferService.rejectPurchaseOffer(
                offerId,
                currentUserId
        );
        return BaseResponse.success(response, "구매 제안을 거절했습니다.");
    }
}
