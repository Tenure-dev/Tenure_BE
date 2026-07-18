package com.tenure.domain.address.entity;

import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "delivery_addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", nullable = false, length = 255)
    private String addressLine2;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "request_note", length = 300)
    private String requestNote;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    // 배송지 생성 정적 팩토리
    public static DeliveryAddress create(
            User user,
            String receiverName,
            String phone,
            String addressLine1,
            String addressLine2,
            String postalCode,
            String requestNote,
            boolean isDefault
    ) {
        DeliveryAddress address = new DeliveryAddress();
        address.user = user;
        address.receiverName = receiverName;
        address.phone = phone;
        address.addressLine1 = addressLine1;
        address.addressLine2 = addressLine2;
        address.postalCode = postalCode;
        address.requestNote = requestNote;
        address.isDefault = isDefault;
        return address;
    }

    // 기존 기본 배송지 해제
    public void unmarkDefault() {
        this.isDefault = false;
    }

    // 배송지 부분 수정
    public void update(
            String receiverName,
            String phone,
            String addressLine1,
            String addressLine2,
            String postalCode,
            String requestNote
    ) {
        if (receiverName != null) this.receiverName = receiverName;
        if (phone != null) this.phone = phone;
        if (addressLine1 != null) this.addressLine1 = addressLine1;
        if (addressLine2 != null) this.addressLine2 = addressLine2;
        if (postalCode != null) this.postalCode = postalCode;
        if (requestNote != null) this.requestNote = requestNote;
    }

    // 기본 배송지
    public void markAsDefault() {
        this.isDefault = true;
    }

    // 배송지 등록자
    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }
}
