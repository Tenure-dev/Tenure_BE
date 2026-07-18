package com.tenure.domain.item.entity;

import com.tenure.domain.item.enums.AcquisitionType;
import com.tenure.domain.item.enums.EndReason;
import com.tenure.domain.trade.entity.Trade;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "item_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    // 이 소유 기간을 시작시킨 취득 거래. FIRST_REGISTERED로 시작한 기간은 거래로 시작한 게 아니므로 null이다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @Enumerated(EnumType.STRING)
    @Column(name = "acquisition_type", nullable = false, length = 30)
    private AcquisitionType acquisitionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "end_reason", length = 30)
    private EndReason endReason;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static ItemHistory ofFirstRegistration(Item item, User owner, LocalDateTime startedAt) {
        ItemHistory history = new ItemHistory();
        history.item = item;
        history.owner = owner;
        history.acquisitionType = AcquisitionType.FIRST_REGISTERED;
        history.startedAt = startedAt;
        return history;
    }

    public static ItemHistory ofTenureTrade(Item item, User newOwner, Trade trade, LocalDateTime startedAt) {
        ItemHistory history = new ItemHistory();
        history.item = item;
        history.owner = newOwner;
        history.trade = trade;
        history.acquisitionType = AcquisitionType.TENURE_TRADE;
        history.startedAt = startedAt;
        return history;
    }

    public void close(EndReason endReason, LocalDateTime endedAt) {
        if (this.endedAt != null) {
            throw new IllegalStateException("이미 종료된 소유 이력입니다. historyId=" + id);
        }
        this.endReason = endReason;
        this.endedAt = endedAt;
    }
}
