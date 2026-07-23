package com.tenure.domain.item.entity;

import com.tenure.domain.item.enums.ItemStatus;

import com.tenure.domain.item.enums.WearingTarget;

import com.tenure.domain.common.entity.BaseTimeEntity;
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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "wearing_target", nullable = false, length = 20)
    private WearingTarget wearingTarget;

    @Column(name = "size_system", length = 30)
    private String sizeSystem;

    @Column(name = "size_value", length = 30)
    private String sizeValue;

    @Column(name = "representative_image_url", length = 500)
    private String representativeImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false, length = 30)
    private ItemStatus itemStatus = ItemStatus.OWNED;

    @Column(name = "ootd_verified_wear_count", nullable = false)
    private Integer ootdVerifiedWearCount = 0;

    @Column(name = "last_worn_at")
    private LocalDate lastWornAt;

    @Column(name = "first_owned_at")
    private LocalDate firstOwnedAt;

    @Column(name = "wish_count", nullable = false)
    private Integer wishCount = 0;

    @Column(name = "purchase_offer_enabled", nullable = false)
    private Boolean purchaseOfferEnabled = true;

    public static Item create(
            User owner,
            Category category,
            String brandName,
            String itemName,
            WearingTarget wearingTarget,
            String sizeSystem,
            String sizeValue,
            LocalDate firstOwnedAt,
            String representativeImageUrl
    ) {
        Item item = new Item();
        item.owner = owner;
        item.category = category;
        item.brandName = brandName;
        item.itemName = itemName;
        item.wearingTarget = wearingTarget;
        item.sizeSystem = sizeSystem;
        item.sizeValue = sizeValue;
        item.firstOwnedAt = firstOwnedAt;
        item.representativeImageUrl = representativeImageUrl;
        item.itemStatus = ItemStatus.OWNED;
        item.ootdVerifiedWearCount = 0;
        item.wishCount = 0;
        item.purchaseOfferEnabled = true;
        return item;
    }

    public void markOnSale() {
        this.itemStatus = ItemStatus.ON_SALE;
    }

    public void changePurchaseOfferEnabled(Boolean purchaseOfferEnabled) {
        this.purchaseOfferEnabled = purchaseOfferEnabled;
    }

    public void increaseWishCount() {
        this.wishCount++;
    }

    public void decreaseWishCount() {
        if (this.wishCount > 0) {
            this.wishCount--;
        }
    }

    public void updateInfo(
            Category category,
            String brandName,
            String itemName,
            WearingTarget wearingTarget,
            String sizeSystem,
            String sizeValue,
            LocalDate firstOwnedAt,
            String representativeImageUrl
    ) {
        this.category = category;
        this.brandName = brandName;
        this.itemName = itemName;
        this.wearingTarget = wearingTarget;
        this.sizeSystem = sizeSystem;
        this.sizeValue = sizeValue;
        this.firstOwnedAt = firstOwnedAt;
        this.representativeImageUrl = representativeImageUrl;
    }


    public void markSold() {
        this.itemStatus = ItemStatus.SOLD;
    }

    public void markOwned() {
        this.itemStatus = ItemStatus.OWNED;
    }

    // 새 소유자가 재판매·offer 수신을 이어갈 수 있어야 하며, validateOfferableItem이 OWNED 상태를 요구하므로
    // TRANSFERRED가 아닌 OWNED로 되돌린다.
    public void transferOwnership(User newOwner) {
        this.owner = newOwner;
        this.itemStatus = ItemStatus.OWNED;
        this.purchaseOfferEnabled = false;
    }
}
