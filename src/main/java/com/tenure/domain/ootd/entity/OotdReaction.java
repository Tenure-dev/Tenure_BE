package com.tenure.domain.ootd.entity;

import com.tenure.domain.ootd.enums.OotdReactionType;

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
        name = "ootd_reactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ootd_reactions_user_ootd_type",
                        columnNames = {"user_id", "ootd_id", "reaction_type"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OotdReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ootd_id", nullable = false)
    private Ootd ootd;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 20)
    private OotdReactionType reactionType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static OotdReaction create(User user, Ootd ootd, OotdReactionType reactionType) {
        OotdReaction reaction = new OotdReaction();
        reaction.user = user;
        reaction.ootd = ootd;
        reaction.reactionType = reactionType;
        return reaction;
    }
}
