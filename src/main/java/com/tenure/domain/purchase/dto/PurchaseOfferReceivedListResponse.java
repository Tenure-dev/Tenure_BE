package com.tenure.domain.purchase.dto;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Schema(description = "Received purchase offer list response")
public record PurchaseOfferReceivedListResponse(
        List<Item> content,
        Cursor nextCursor,
        boolean hasNext
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseOfferReceivedListResponse of(
            List<PurchaseOffer> offers,
            Map<Long, Long> tradeIdByOfferId,
            LocalDateTime serverNow,
            boolean hasNext
    ) {
        List<Item> content = offers.stream()
                .map(offer -> Item.from(offer, tradeIdByOfferId.get(offer.getId()), serverNow))
                .toList();
        Cursor nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            Item last = content.get(content.size() - 1);
            nextCursor = new Cursor(last.createdAt(), last.offerId());
        }
        return new PurchaseOfferReceivedListResponse(content, nextCursor, hasNext);
    }

    private static OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    public record Cursor(
            @Schema(description = "Next page createdAt cursor", example = "2026-07-12T10:00:00+09:00")
            OffsetDateTime cursorCreatedAt,

            @Schema(description = "Next page purchase offer id cursor", example = "123")
            Long cursorOfferId
    ) {
    }

    public record Item(
            Long offerId,
            PurchaseOfferStatus status,
            Long itemId,
            String brandName,
            String itemName,
            String imageUrl,
            Long proposerId,
            String proposerUsername,
            String proposerProfileImageUrl,
            Integer offerAmount,
            Integer shippingFee,
            Integer proposerServiceFee,
            Integer totalPaymentAmount,
            Integer ownerSettlementAmount,
            PaymentAuthorizationStatus paymentAuthorizationStatus,
            OffsetDateTime createdAt,
            OffsetDateTime expiresAt,
            Long remainingSeconds,
            boolean canAccept,
            boolean canReject,
            Long tradeId
    ) {
        static Item from(PurchaseOffer offer, Long tradeId, LocalDateTime serverNow) {
            com.tenure.domain.item.entity.Item offerItem = offer.getItem();
            User proposer = offer.getProposer();
            boolean sent = offer.getStatus() == PurchaseOfferStatus.SENT;
            return new Item(
                    offer.getId(),
                    offer.getStatus(),
                    offerItem.getId(),
                    offerItem.getBrandName(),
                    offerItem.getItemName(),
                    offerItem.getRepresentativeImageUrl(),
                    proposer.getId(),
                    proposer.getUsername(),
                    proposer.getProfileImageUrl(),
                    offer.getOfferPrice(),
                    offer.getProposerShippingFee(),
                    offer.getProposerServiceFee(),
                    offer.getTotalPaymentAmount(),
                    offer.getOwnerSettlementAmount(),
                    offer.getPaymentAuthorizationStatus(),
                    toOffsetDateTime(offer.getCreatedAt()),
                    toOffsetDateTime(offer.getExpiresAt()),
                    sent ? Math.max(0, Duration.between(serverNow, offer.getExpiresAt()).getSeconds()) : null,
                    sent,
                    sent,
                    tradeId
            );
        }
    }
}
