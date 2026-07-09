package com.tenure.domain.user.entity;

import com.tenure.domain.user.enums.AccountVisibility;

import com.tenure.domain.user.enums.UserGrade;

import com.tenure.domain.user.enums.UserGender;

import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.common.enums.FeePolicy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_username", columnNames = "username")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserGender gender;

    @Column(name = "height_cm", nullable = false)
    private Integer heightCm;

    @Column(name = "weight_kg", nullable = false)
    private Integer weightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserGrade grade = UserGrade.BASIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_visibility", nullable = false, length = 20)
    private AccountVisibility accountVisibility = AccountVisibility.PUBLIC;

    @Column(name = "default_shipping_fee")
    private Integer defaultShippingFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_fee_policy", length = 30)
    private FeePolicy defaultFeePolicy;

    @Column(name = "default_purchase_offer_enabled", nullable = false)
    private Boolean defaultPurchaseOfferEnabled = false;

    @Column(name = "notification_settings", columnDefinition = "jsonb")
    private String notificationSettings;

    @Column(name = "settlement_account", columnDefinition = "jsonb")
    private String settlementAccount;

    @Column(name = "onboarding_completed", nullable = false)
    private Boolean onboardingCompleted = false;
}
