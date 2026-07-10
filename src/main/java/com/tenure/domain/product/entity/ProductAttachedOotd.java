package com.tenure.domain.product.entity;

import com.tenure.domain.ootd.entity.Ootd;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "product_attached_ootds",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_attached_ootds_product_ootd",
                        columnNames = {"product_id", "ootd_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductAttachedOotd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ootd_id", nullable = false)
    private Ootd ootd;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static ProductAttachedOotd create(Product product, Ootd ootd) {
        ProductAttachedOotd attachedOotd = new ProductAttachedOotd();
        attachedOotd.product = product;
        attachedOotd.ootd = ootd;
        return attachedOotd;
    }
}
