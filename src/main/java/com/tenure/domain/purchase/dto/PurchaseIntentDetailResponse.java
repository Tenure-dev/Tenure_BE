package com.tenure.domain.purchase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Schema(description = "거래 의사 상세 조회 응답")
public record PurchaseIntentDetailResponse(

        @Schema(description = "거래 의사 ID", example = "123")
        Long intentId,

        @Schema(description = "거래 의사 상태", example = "SENT")
        PurchaseIntentStatus status,

        @Schema(description = "조회자 역할", example = "BUYER")
        ViewerRole viewerRole,

        @Schema(description = "서버 현재 시각", example = "2026-07-12T15:00:00+09:00")
        OffsetDateTime serverTime,

        @Schema(description = "응답 만료 시각", example = "2026-07-13T15:00:00+09:00")
        OffsetDateTime expiresAt,

        @Schema(description = "응답 만료까지 남은 초. 이미 만료된 경우 0", example = "86400")
        long remainingSeconds,

        @Schema(description = "결제 승인 상태", example = "AUTHORIZED")
        PaymentAuthorizationStatus paymentAuthorizationStatus,

        ProductSummary product,
        UserSummary buyer,
        UserSummary seller,
        Amounts amounts,
        Delivery delivery,

        @Schema(description = "배송지 노출 상태", example = "VISIBLE")
        DeliveryDisclosureStatus deliveryDisclosureStatus,

        @Schema(description = "결제 수단 ID", example = "MOCK_CARD")
        String paymentMethodId
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseIntentDetailResponse from(
            PurchaseIntent intent,
            ViewerRole viewerRole,
            LocalDateTime serverNow
    ) {
        boolean sellerCanSeeDelivery = viewerRole == ViewerRole.SELLER
                && intent.getStatus() == PurchaseIntentStatus.ACCEPTED;
        boolean deliveryVisible = viewerRole == ViewerRole.BUYER || sellerCanSeeDelivery;
        return new PurchaseIntentDetailResponse(
                intent.getId(),
                intent.getStatus(),
                viewerRole,
                toOffsetDateTime(serverNow),
                toOffsetDateTime(intent.getExpiresAt()),
                Math.max(0, Duration.between(serverNow, intent.getExpiresAt()).getSeconds()),
                intent.getPaymentAuthorizationStatus(),
                ProductSummary.from(intent.getProduct()),
                UserSummary.from(intent.getBuyer()),
                UserSummary.from(intent.getSeller()),
                Amounts.from(intent, viewerRole),
                deliveryVisible ? Delivery.from(intent) : null,
                deliveryVisible ? DeliveryDisclosureStatus.VISIBLE : DeliveryDisclosureStatus.AFTER_ACCEPTANCE,
                intent.getPaymentMethodId()
        );
    }

    private static OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    public enum ViewerRole {
        BUYER,
        SELLER
    }

    public enum DeliveryDisclosureStatus {
        VISIBLE,
        AFTER_ACCEPTANCE
    }

    public record ProductSummary(
            Long productId,
            Long itemId,
            String brandName,
            String itemName,
            String imageUrl
    ) {
        static ProductSummary from(Product product) {
            Item item = product.getItem();
            String imageUrl = product.getMainImageUrl() != null
                    ? product.getMainImageUrl()
                    : item.getRepresentativeImageUrl();
            return new ProductSummary(
                    product.getId(),
                    item.getId(),
                    item.getBrandName(),
                    item.getItemName(),
                    imageUrl
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
            Integer productAmount,
            Integer shippingFee,
            Integer buyerServiceFee,
            Integer sellerServiceFee,
            Integer buyerPaymentAmount,
            Integer sellerSettlementAmount
    ) {
        static Amounts from(PurchaseIntent intent, ViewerRole viewerRole) {
            boolean sellerView = viewerRole == ViewerRole.SELLER;
            return new Amounts(
                    intent.getProductPrice(),
                    intent.getBuyerShippingFee(),
                    intent.getBuyerServiceFee(),
                    sellerView ? intent.getSellerServiceFee() : null,
                    intent.getTotalPaymentAmount(),
                    sellerView ? intent.getSellerSettlementAmount() : null
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
        static Delivery from(PurchaseIntent intent) {
            return new Delivery(
                    intent.getDeliveryReceiverName(),
                    intent.getDeliveryPhone(),
                    intent.getDeliveryAddressLine1(),
                    intent.getDeliveryAddressLine2(),
                    intent.getDeliveryPostalCode(),
                    intent.getDeliveryRequestNote()
            );
        }
    }
}
