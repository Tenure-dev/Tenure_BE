package com.tenure.domain.chat.dto.response;

import com.tenure.domain.chat.entity.ChatMessage;
import com.tenure.domain.chat.enums.MessageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageResponse {

    private Long messageId;
    private Long senderId;
    private String senderName;
    private String senderProfileImgUrl;
    private MessageType messageType;
    private String content;
    private String contentImageUrl;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(chatMessage.getId(), chatMessage.getSender().getId(), chatMessage.getSender().getUsername(),
                chatMessage.getSender().getProfileImageUrl(), chatMessage.getMessageType(), chatMessage.getContent(),
                chatMessage.getImageUrl(), chatMessage.getCreatedAt());
    }


}
