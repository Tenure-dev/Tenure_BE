package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseOfferReceivedListResponse;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
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
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PurchaseOffer", description = "Purchase offer API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PurchaseOfferController {

    private final PurchaseOfferService purchaseOfferService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Get received purchase offers",
            description = "Returns purchase offers received by the current item owner. Expired SENT offers are updated to EXPIRED and RELEASED before querying.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "1"
                    ),
                    @Parameter(
                            name = "statuses",
                            description = "Status list. Example: statuses=SENT or statuses=SENT,EXPIRED. If omitted, all statuses are returned.",
                            example = "SENT"
                    ),
                    @Parameter(
                            name = "cursorCreatedAt",
                            description = "CreatedAt cursor for next page.",
                            example = "2026-07-12T10:00:00+09:00"
                    ),
                    @Parameter(
                            name = "cursorOfferId",
                            description = "Purchase offer id cursor for next page.",
                            example = "123"
                    ),
                    @Parameter(
                            name = "size",
                            description = "Page size. Default 20, max 50.",
                            example = "20"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Received purchase offers returned successfully.",
            content = @Content(schema = @Schema(implementation = PurchaseOfferReceivedListResponse.class))
    )
    @GetMapping("/purchase-offers/received")
    public BaseResponse<PurchaseOfferReceivedListResponse> getReceivedPurchaseOffers(
            @RequestParam(required = false) List<PurchaseOfferStatus> statuses,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorOfferId,
            @RequestParam(required = false) Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseOfferReceivedListResponse response = purchaseOfferService.getReceivedPurchaseOffers(
                currentUserId,
                statuses,
                cursorCreatedAt,
                cursorOfferId,
                size
        );
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }
}
