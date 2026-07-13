package com.tenure.domain.purchase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "구매 제안 상세 조회 응답")
public record PurchaseOfferDetailResponse(

        @Schema(description = "구매 제안 ID", example = "123")
        Long offerId,

        @Schema(description = "구매 제안 상태", example = "SENT")
        PurchaseOfferStatus status,

        @Schema(description = "조회자 역할", example = "PROPOSER")
        ViewerRole viewerRole,

        @Schema(description = "서버 현재 시각", example = "2026-07-13T15:00:00+09:00")
        OffsetDateTime serverTime,

        @Schema(description = "응답 만료 시각", example = "2026-07-14T15:00:00+09:00")
        OffsetDateTime expiresAt,

        @Schema(description = "응답 만료까지 남은 초. 이미 만료된 경우 0", example = "86400")
        long remainingSeconds,

        @Schema(description = "결제 승인 상태", example = "AUTHORIZED")
        PaymentAuthorizationStatus paymentAuthorizationStatus,

        ItemSummary item,
        UserSummary proposer,
        UserSummary owner,
        Amounts amounts,
        Delivery delivery,

        @Schema(description = "배송지 노출 상태", example = "VISIBLE")
        DeliveryDisclosureStatus deliveryDisclosureStatus,

        @Schema(description = "결제 수단 ID", example = "MOCK_CARD")
        String paymentMethodId
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseOfferDetailResponse from(
            PurchaseOffer offer,
            ViewerRole viewerRole,
            LocalDateTime serverNow
    ) {
        boolean ownerCanSeeDelivery = viewerRole == ViewerRole.OWNER
                && offer.getStatus() == PurchaseOfferStatus.ACCEPTED;
        boolean deliveryVisible = viewerRole == ViewerRole.PROPOSER || ownerCanSeeDelivery;
        return new PurchaseOfferDetailResponse(
                offer.getId(),
                offer.getStatus(),
                viewerRole,
                toOffsetDateTime(serverNow),
                toOffsetDateTime(offer.getExpiresAt()),
                Math.max(0, Duration.between(serverNow, offer.getExpiresAt()).getSeconds()),
                offer.getPaymentAuthorizationStatus(),
                ItemSummary.from(offer.getItem()),
                UserSummary.from(offer.getProposer()),
                UserSummary.from(offer.getOwner()),
                Amounts.from(offer, viewerRole),
                deliveryVisible ? Delivery.from(offer) : null,
                deliveryVisible ? DeliveryDisclosureStatus.VISIBLE : DeliveryDisclosureStatus.AFTER_ACCEPTANCE,
                offer.getPaymentMethodId()
        );
    }

    private static OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    public enum ViewerRole {
        PROPOSER,
        OWNER
    }

    public enum DeliveryDisclosureStatus {
        VISIBLE,
        AFTER_ACCEPTANCE
    }

    public record ItemSummary(
            Long itemId,
            String brandName,
            String itemName,
            String imageUrl
    ) {
        static ItemSummary from(Item item) {
            return new ItemSummary(
                    item.getId(),
                    item.getBrandName(),
                    item.getItemName(),
                    item.getRepresentativeImageUrl()
            );
        }
    }

    public record UserSummary(
            Long userId,
            String username,
            String profileImageUrl
    ) {
        static UserSummary from(User user) {
            return new UserSummary(
                    user.getId(),
                    user.getUsername(),
                    user.getProfileImageUrl()
            );
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Amounts(
            Integer offerAmount,
            Integer shippingFee,
            Integer proposerServiceFee,
            Integer totalPaymentAmount,
            Integer ownerSettlementAmount
    ) {
        static Amounts from(PurchaseOffer offer, ViewerRole viewerRole) {
            boolean ownerView = viewerRole == ViewerRole.OWNER;
            return new Amounts(
                    offer.getOfferPrice(),
                    offer.getProposerShippingFee(),
                    offer.getProposerServiceFee(),
                    offer.getTotalPaymentAmount(),
                    ownerView ? offer.getOwnerSettlementAmount() : null
            );
        }
    }

    public record Delivery(
            String receiverName,
            String phone,
            String addressLine1,
            String addressLine2,
            String postalCode,
            String requestNote
    ) {
        static Delivery from(PurchaseOffer offer) {
            return new Delivery(
                    offer.getDeliveryReceiverName(),
                    offer.getDeliveryPhone(),
                    offer.getDeliveryAddressLine1(),
                    offer.getDeliveryAddressLine2(),
                    offer.getDeliveryPostalCode(),
                    offer.getDeliveryRequestNote()
            );
        }
    }
}
