package com.tenure.domain.chat.entity;

import com.tenure.domain.chat.enums.MessageType;

import com.tenure.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    private MessageType messageType;

    @Column(columnDefinition = "text")
    private String content;

    @Convert(converter = com.tenure.global.converter.StringListConverter.class)
    @Column(name = "image_urls", columnDefinition = "text")
    private List<String> imageUrls;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 채팅 매시지 생성 매서드
    public static ChatMessage of(ChatRoom chatRoom, User sender,
                                 MessageType messageType, String content, List<String> imageUrls) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.chatRoom = chatRoom;
        chatMessage.sender = sender;
        chatMessage.messageType = messageType;
        chatMessage.content = content;
        chatMessage.imageUrls = imageUrls;
        return chatMessage;

    }
}
