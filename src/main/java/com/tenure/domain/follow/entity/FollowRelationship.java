package com.tenure.domain.follow.entity;

import com.tenure.domain.follow.enums.FollowStatus;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "follow_relationships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_follow_relationships_follower_following",
                        columnNames = {"follower_user_id", "following_user_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_user_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_user_id", nullable = false)
    private User following;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FollowStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * MVP는 공개 계정만 지원하고 승인 흐름이 없으므로 항상 ACCEPTED로 생성한다.
     * (REQUESTED/REJECTED는 승인 흐름 도입 시 사용)
     * respondedAt은 승인 흐름용 필드라 즉시 팔로우에서는 사용하지 않는다.
     */
    public static FollowRelationship create(User follower, User following) {
        FollowRelationship relationship = new FollowRelationship();
        relationship.follower = follower;
        relationship.following = following;
        relationship.status = FollowStatus.ACCEPTED;
        return relationship;
    }
}
