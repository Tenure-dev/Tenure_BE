package com.tenure.domain.purchase.entity;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "purchase_offers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_purchase_offers_proposer_item",
                        columnNames = {"proposer_user_id", "item_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOffer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proposer_user_id", nullable = false)
    private User proposer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_address_id", nullable = false)
    private DeliveryAddress deliveryAddress;

    @Column(name = "offer_price", nullable = false)
    private Integer offerPrice;

    @Column(name = "proposer_shipping_fee", nullable = false)
    private Integer proposerShippingFee = 0;

    @Column(name = "proposer_service_fee", nullable = false)
    private Integer proposerServiceFee = 0;

    @Column(name = "fee_rate_snapshot", nullable = false, precision = 5, scale = 4)
    private BigDecimal feeRateSnapshot;

    @Column(name = "total_payment_amount", nullable = false)
    private Integer totalPaymentAmount;

    @Column(name = "owner_settlement_amount", nullable = false)
    private Integer ownerSettlementAmount;

    @Column(name = "payment_authorization_id", nullable = false, length = 100)
    private String paymentAuthorizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_authorization_status", nullable = false, length = 30)
    private PaymentAuthorizationStatus paymentAuthorizationStatus = PaymentAuthorizationStatus.AUTHORIZED;

    @Column(name = "payment_method_id", nullable = false, length = 100)
    private String paymentMethodId;

    @Column(name = "delivery_receiver_name", nullable = false, length = 50)
    private String deliveryReceiverName;

    @Column(name = "delivery_phone", nullable = false, length = 20)
    private String deliveryPhone;

    @Column(name = "delivery_address_line1", nullable = false, length = 255)
    private String deliveryAddressLine1;

    @Column(name = "delivery_address_line2", nullable = false, length = 255)
    private String deliveryAddressLine2;

    @Column(name = "delivery_postal_code", length = 10)
    private String deliveryPostalCode;

    @Column(name = "delivery_request_note", length = 300)
    private String deliveryRequestNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseOfferStatus status = PurchaseOfferStatus.SENT;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public static PurchaseOffer create(
            Item item,
            User proposer,
            User owner,
            DeliveryAddress deliveryAddress,
            Integer offerPrice,
            Integer proposerShippingFee,
            Integer proposerServiceFee,
            BigDecimal feeRateSnapshot,
            Integer totalPaymentAmount,
            Integer ownerSettlementAmount,
            String paymentAuthorizationId,
            String paymentMethodId,
            LocalDateTime expiresAt
    ) {
        PurchaseOffer offer = new PurchaseOffer();
        offer.item = item;
        offer.proposer = proposer;
        offer.owner = owner;
        offer.deliveryAddress = deliveryAddress;
        offer.offerPrice = offerPrice;
        offer.proposerShippingFee = proposerShippingFee;
        offer.proposerServiceFee = proposerServiceFee;
        offer.feeRateSnapshot = feeRateSnapshot;
        offer.totalPaymentAmount = totalPaymentAmount;
        offer.ownerSettlementAmount = ownerSettlementAmount;
        offer.paymentAuthorizationId = paymentAuthorizationId;
        offer.paymentAuthorizationStatus = PaymentAuthorizationStatus.AUTHORIZED;
        offer.paymentMethodId = paymentMethodId;
        offer.deliveryReceiverName = deliveryAddress.getReceiverName();
        offer.deliveryPhone = deliveryAddress.getPhone();
        offer.deliveryAddressLine1 = deliveryAddress.getAddressLine1();
        offer.deliveryAddressLine2 = deliveryAddress.getAddressLine2();
        offer.deliveryPostalCode = deliveryAddress.getPostalCode();
        offer.deliveryRequestNote = deliveryAddress.getRequestNote();
        offer.status = PurchaseOfferStatus.SENT;
        offer.expiresAt = expiresAt;
        return offer;
    }

    public boolean isSent() {
        return status == PurchaseOfferStatus.SENT;
    }

    public boolean isExpiredAt(LocalDateTime now) {
        return !expiresAt.isAfter(now);
    }

    public void expireAndReleaseAuthorization() {
        this.status = PurchaseOfferStatus.EXPIRED;
        this.paymentAuthorizationStatus = PaymentAuthorizationStatus.RELEASED;
    }
}
