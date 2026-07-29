package com.tenure.domain.chat.entity;

import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.item.entity.Item;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chat_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_user_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_user_id", nullable = false)
    private User seller;

    @Column(name = "last_message", length = 500)
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "is_closed")
    private boolean isClosed = false;


    //채팅방 생성 매서드
    public static ChatRoom of(Item item, User buyer, User seller) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.item = item;
        chatRoom.buyer = buyer;
        chatRoom.seller = seller;

        return chatRoom;
    }

    // 마지막 메시지 및 시각 갱신 매서드
    public void updateLastMessage(String lastMessage, LocalDateTime lastMessageAt) {
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
    }

    // 채팅방 닫기(한쪽이 나간 경우)
    public void close() {
        this.isClosed = true;
    }
}
