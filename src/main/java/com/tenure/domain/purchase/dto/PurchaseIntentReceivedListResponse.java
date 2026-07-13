package com.tenure.domain.purchase.dto;

import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Schema(description = "받은 거래 의사 목록 응답")
public record PurchaseIntentReceivedListResponse(
        List<Item> content,
        Cursor nextCursor,
        boolean hasNext
) {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public static PurchaseIntentReceivedListResponse of(
            List<PurchaseIntent> intents,
            Map<Long, Long> tradeIdByIntentId,
            LocalDateTime serverNow,
            boolean hasNext
    ) {
        List<Item> content = intents.stream()
                .map(intent -> Item.from(intent, tradeIdByIntentId.get(intent.getId()), serverNow))
                .toList();
        Cursor nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            Item last = content.get(content.size() - 1);
            nextCursor = new Cursor(last.createdAt(), last.intentId());
        }
        return new PurchaseIntentReceivedListResponse(content, nextCursor, hasNext);
    }

    private static OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    public record Cursor(
            @Schema(description = "다음 페이지 조회용 생성 시각 커서", example = "2026-07-12T10:00:00+09:00")
            OffsetDateTime cursorCreatedAt,

            @Schema(description = "다음 페이지 조회용 거래 의사 ID 커서", example = "123")
            Long cursorIntentId
    ) {
    }

    public record Item(
            Long intentId,
            PurchaseIntentStatus status,
            Long productId,
            Long itemId,
            String brandName,
            String itemName,
            String imageUrl,
            Long buyerId,
            String buyerUsername,
            String buyerProfileImageUrl,
            Integer productAmount,
            Integer deliveryFee,
            Integer buyerServiceFee,
            Integer sellerServiceFee,
            Integer buyerPaymentAmount,
            Integer sellerSettlementAmount,
            PaymentAuthorizationStatus paymentAuthorizationStatus,
            OffsetDateTime createdAt,
            OffsetDateTime expiresAt,
            Long remainingSeconds,
            boolean canAccept,
            boolean canReject,
            Long tradeId
    ) {
        static Item from(PurchaseIntent intent, Long tradeId, LocalDateTime serverNow) {
            Product product = intent.getProduct();
            com.tenure.domain.item.entity.Item productItem = product.getItem();
            User buyer = intent.getBuyer();
            boolean sent = intent.getStatus() == PurchaseIntentStatus.SENT;
            String imageUrl = product.getMainImageUrl() != null
                    ? product.getMainImageUrl()
                    : productItem.getRepresentativeImageUrl();
            return new Item(
                    intent.getId(),
                    intent.getStatus(),
                    product.getId(),
                    productItem.getId(),
                    productItem.getBrandName(),
                    productItem.getItemName(),
                    imageUrl,
                    buyer.getId(),
                    buyer.getUsername(),
                    buyer.getProfileImageUrl(),
                    intent.getProductPrice(),
                    intent.getBuyerShippingFee(),
                    intent.getBuyerServiceFee(),
                    intent.getSellerServiceFee(),
                    intent.getTotalPaymentAmount(),
                    intent.getSellerSettlementAmount(),
                    intent.getPaymentAuthorizationStatus(),
                    toOffsetDateTime(intent.getCreatedAt()),
                    toOffsetDateTime(intent.getExpiresAt()),
                    sent ? Math.max(0, Duration.between(serverNow, intent.getExpiresAt()).getSeconds()) : null,
                    sent,
                    sent,
                    tradeId
            );
        }
    }
}
