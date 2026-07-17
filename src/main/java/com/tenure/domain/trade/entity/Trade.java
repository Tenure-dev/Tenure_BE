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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "trades",
        // 락 안전망: 정상 흐름에서는 product/PurchaseIntent 행 락이 동시 accept를 직렬화해 같은 출처로부터
        // Trade가 두 번 생성되는 것을 막는다. 이 제약이 실제로 위반된다면 락 프로토콜 자체가 깨졌다는 뜻이므로,
        // 해당 DataIntegrityViolationException은 잡지 않고 그대로 전파해 500으로 드러낸다.
        uniqueConstraints = @UniqueConstraint(
                name = "uk_trades_source_type_source_id",
                columnNames = {"source_type", "source_id"}
        )
)
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

    @Column(name = "item_price", nullable = false)
    private Integer itemPrice;

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

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @Column(name = "payment_method_id", nullable = false, length = 100)
    private String paymentMethodId;

    @Column(name = "payment_authorization_id", nullable = false, length = 100)
    private String paymentAuthorizationId;

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

    public static Trade create(TradeCreateCommand command) {
        Trade trade = new Trade();
        trade.sourceType = command.sourceType();
        trade.sourceId = command.sourceId();
        trade.item = command.item();
        trade.product = command.product();
        trade.buyer = command.buyer();
        trade.seller = command.seller();
        trade.itemPrice = command.itemPrice();
        trade.paymentAmount = command.paymentAmount();
        trade.buyerShippingFee = command.buyerShippingFee();
        trade.buyerServiceFee = command.buyerServiceFee();
        trade.sellerServiceFee = command.sellerServiceFee();
        trade.settlementAmount = command.settlementAmount();
        trade.status = TradeStatus.PAID;
        trade.paymentMethodId = command.paymentMethodId();
        trade.paymentAuthorizationId = command.paymentAuthorizationId();
        trade.deliveryReceiverName = command.deliveryReceiverName();
        trade.deliveryPhone = command.deliveryPhone();
        trade.deliveryAddressLine1 = command.deliveryAddressLine1();
        trade.deliveryAddressLine2 = command.deliveryAddressLine2();
        trade.deliveryPostalCode = command.deliveryPostalCode();
        trade.deliveryRequestNote = command.deliveryRequestNote();
        return trade;
    }
}
