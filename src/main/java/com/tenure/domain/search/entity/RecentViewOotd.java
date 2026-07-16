package com.tenure.domain.search.entity;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "recent_viewed_ootds",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_recent_viewed_ootds_viewer_ootd",
                columnNames = {"viewer_user_id", "ootd_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentViewOotd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viewer_user_id", nullable = false)
    private User viewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ootd_id", nullable = false)
    private Ootd ootd;

    @Column(name = "last_viewed_at", nullable = false)
    private LocalDateTime lastViewedAt;

    @PrePersist
    protected void onCreate() {
        this.lastViewedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastViewedAt = LocalDateTime.now();
    }

    public static RecentViewOotd of(User viewer, Ootd ootd) {
        RecentViewOotd r = new RecentViewOotd();
        r.viewer = viewer;
        r.ootd = ootd;
        return r;
    }

    public void touch() {
        this.lastViewedAt = LocalDateTime.now();
    }
}
