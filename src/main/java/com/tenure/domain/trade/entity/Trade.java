package com.tenure.domain.trade.entity;

import com.tenure.domain.trade.enums.DeliveryCarrier;

import com.tenure.domain.trade.enums.TradeStatus;

import com.tenure.domain.trade.enums.TradeSourceType;

import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.item.entity.Item;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "trades")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private TradeSourceType sourceType;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_user_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_user_id", nullable = false)
    private User seller;

    @Column(name = "payment_amount", nullable = false)
    private Integer paymentAmount;

    @Column(name = "buyer_shipping_fee", nullable = false)
    private Integer buyerShippingFee = 0;

    @Column(name = "buyer_service_fee", nullable = false)
    private Integer buyerServiceFee = 0;

    @Column(name = "seller_service_fee", nullable = false)
    private Integer sellerServiceFee = 0;

    @Column(name = "settlement_amount", nullable = false)
    private Integer settlementAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TradeStatus status = TradeStatus.PAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_carrier", length = 30)
    private DeliveryCarrier deliveryCarrier;

    @Column(name = "custom_delivery_carrier_name", length = 50)
    private String customDeliveryCarrierName;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "payment_method_id", nullable = false, length = 100)
    private String paymentMethodId;

    @Column(name = "payment_authorization_id", nullable = false, length = 100)
    private String paymentAuthorizationId;
}
