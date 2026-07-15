package com.tenure.domain.search.entity;

import com.tenure.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "recent_viewed_users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_recent_viewed_users_viewer_viewed",
                columnNames = {"viewer_user_id", "viewed_user_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentViewUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viewer_user_id", nullable = false)
    private User viewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viewed_user_id", nullable = false)
    private User viewed;

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

    public static RecentViewUser of(User viewer, User viewed) {
        RecentViewUser r = new RecentViewUser();
        r.viewer = viewer;
        r.viewed = viewed;
        return r;
    }

    public void touch() {
        this.lastViewedAt = LocalDateTime.now();
    }
}
