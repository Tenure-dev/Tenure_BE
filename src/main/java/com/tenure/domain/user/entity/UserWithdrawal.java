package com.tenure.domain.user.entity;

import com.tenure.domain.user.enums.WithdrawalReason;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 탈퇴 기록. 탈퇴한 사용자 id와 사유를 남긴다.
 */
@Getter
@Entity
@Table(name = "user_withdrawals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserWithdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private WithdrawalReason reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static UserWithdrawal create(Long userId, WithdrawalReason reason) {
        UserWithdrawal withdrawal = new UserWithdrawal();
        withdrawal.userId = userId;
        withdrawal.reason = reason;
        return withdrawal;
    }
}