package com.tenure.domain.purchase.entity;

import com.tenure.domain.purchase.enums.PurchaseIntentStatus;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.product.entity.Product;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "purchase_intents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseIntent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_user_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_user_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_address_id", nullable = false)
    private DeliveryAddress deliveryAddress;

    @Column(name = "product_price", nullable = false)
    private Integer productPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_policy_snapshot", nullable = false, length = 30)
    private FeePolicy feePolicySnapshot;

    @Column(name = "buyer_shipping_fee", nullable = false)
    private Integer buyerShippingFee = 0;

    @Column(name = "buyer_service_fee", nullable = false)
    private Integer buyerServiceFee = 0;

    @Column(name = "seller_service_fee", nullable = false)
    private Integer sellerServiceFee = 0;

    @Column(name = "total_payment_amount", nullable = false)
    private Integer totalPaymentAmount;

    @Column(name = "seller_settlement_amount", nullable = false)
    private Integer sellerSettlementAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_authorization_status", nullable = false, length = 30)
    private PaymentAuthorizationStatus paymentAuthorizationStatus = PaymentAuthorizationStatus.AUTHORIZED;

    @Column(name = "payment_authorization_id", nullable = false, length = 100)
    private String paymentAuthorizationId;

    @Column(name = "payment_method_id", nullable = false, length = 100)
    private String paymentMethodId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseIntentStatus status = PurchaseIntentStatus.SENT;
}
