package com.tenure.domain.product.entity;

import com.tenure.domain.product.enums.ProductStatus;

import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.item.entity.Item;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_user_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "shipping_fee", nullable = false)
    private Integer shippingFee = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_policy", nullable = false, length = 30)
    private FeePolicy feePolicy = FeePolicy.SELLER_PAYS;

    @Column(name = "main_image_url", length = 500)
    private String mainImageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String measurements;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "condition_flags", columnDefinition = "jsonb")
    private String conditionFlags;

    @Column(name = "seller_description", columnDefinition = "text")
    private String sellerDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false, length = 20)
    private ProductStatus productStatus = ProductStatus.ON_SALE;

    public static Product create(
            Item item,
            User seller,
            Integer price,
            Integer shippingFee,
            FeePolicy feePolicy,
            String mainImageUrl,
            String measurements,
            String conditionFlags,
            String sellerDescription
    ) {
        Product product = new Product();
        product.item = item;
        product.seller = seller;
        product.price = price;
        product.shippingFee = shippingFee;
        product.feePolicy = feePolicy;
        product.mainImageUrl = mainImageUrl;
        product.measurements = measurements;
        product.conditionFlags = conditionFlags;
        product.sellerDescription = sellerDescription;
        product.productStatus = ProductStatus.ON_SALE;
        return product;
    }
}
