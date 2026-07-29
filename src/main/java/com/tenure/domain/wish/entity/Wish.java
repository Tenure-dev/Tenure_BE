package com.tenure.domain.wish.entity;

import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "wishes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_wishes_user_item", columnNames = {"user_id", "item_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wish extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;

    public static Wish create(User user, Item item) {
        Wish wish = new Wish();
        wish.user = user;
        wish.item = item;
        wish.notificationEnabled = true;
        return wish;
    }

    public void updateNotificationEnabled(Boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }
}
