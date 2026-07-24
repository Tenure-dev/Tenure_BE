package com.tenure.domain.trade.controller;

import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.service.PurchaseOfferAcceptService;
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

@Tag(name = "PurchaseOffer", description = "Purchase offer API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PurchaseOfferAcceptController {

    private final PurchaseOfferAcceptService purchaseOfferAcceptService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Accept purchase offer",
            description = "The item owner accepts a SENT purchase offer, captures the mock payment authorization, "
                    + "and creates a trade. Other SENT offers for the same item are canceled."
    )
    @ApiResponse(
            responseCode = "201",
            content = @Content(schema = @Schema(implementation = TradeDetailResponse.class))
    )
    @PostMapping("/purchase-offers/{offerId}/accept")
    public ResponseEntity<BaseResponse<TradeDetailResponse>> acceptPurchaseOffer(
            @PathVariable Long offerId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        TradeDetailResponse response = purchaseOfferAcceptService.acceptPurchaseOffer(offerId, currentUserId);
        return ResponseEntity
                .created(URI.create("/trades/" + response.tradeId()))
                .body(BaseResponse.success(response, "Purchase offer accepted."));
    }
}
