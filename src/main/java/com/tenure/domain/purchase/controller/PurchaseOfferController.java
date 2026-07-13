package com.tenure.domain.purchase.controller;

import com.tenure.domain.purchase.dto.PurchaseOfferCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferReceivedListResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferRejectResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferSentListResponse;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
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
                .body(BaseResponse.success(response, "Purchase offer created."));
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
        return BaseResponse.success(response, "Query succeeded.");
    }

    @Operation(
            summary = "Get sent purchase offers",
            description = "Returns the current user's sent purchase offers with cursor pagination. Expired SENT offers are updated to EXPIRED and RELEASED before querying.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true, example = "2"),
                    @Parameter(name = "statuses", description = "Status filter. Omit to return all statuses.", example = "SENT"),
                    @Parameter(name = "cursorCreatedAt", description = "Created-at cursor for the next page.", example = "2026-07-12T10:00:00+09:00"),
                    @Parameter(name = "cursorOfferId", description = "Purchase offer id cursor for the next page.", example = "123"),
                    @Parameter(name = "size", description = "Page size. Default 20, max 50.", example = "20")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sent purchase offers returned successfully.",
            content = @Content(schema = @Schema(implementation = PurchaseOfferSentListResponse.class))
    )
    @GetMapping("/purchase-offers/sent")
    public BaseResponse<PurchaseOfferSentListResponse> getSentPurchaseOffers(
            @RequestParam(required = false) List<PurchaseOfferStatus> statuses,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorOfferId,
            @RequestParam(required = false) Integer size
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        PurchaseOfferSentListResponse response = purchaseOfferService.getSentPurchaseOffers(
                currentUserId,
                statuses,
                cursorCreatedAt,
                cursorOfferId,
                size
        );
        return BaseResponse.success(response, "Query succeeded.");
    }

    @Operation(
            summary = "Get received purchase offers",
            description = "Returns purchase offers received by the current item owner. Expired SENT offers are updated to EXPIRED and RELEASED before querying.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true, example = "1"),
                    @Parameter(name = "statuses", description = "Status filter. Omit to return all statuses.", example = "SENT"),
                    @Parameter(name = "cursorCreatedAt", description = "Created-at cursor for the next page.", example = "2026-07-12T10:00:00+09:00"),
                    @Parameter(name = "cursorOfferId", description = "Purchase offer id cursor for the next page.", example = "123"),
                    @Parameter(name = "size", description = "Page size. Default 20, max 50.", example = "20")
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
        return BaseResponse.success(response, "Query succeeded.");
    }

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
        return BaseResponse.success(response, "Purchase offer rejected.");
    }
}
