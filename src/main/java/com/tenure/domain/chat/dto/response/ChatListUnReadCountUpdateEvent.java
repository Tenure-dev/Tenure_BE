package com.tenure.domain.chat.dto.response;

import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.chat.entity.ChatRoomMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatListUnReadCountUpdateEvent {

    private Long chatRoomId;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;

    public static ChatListUnReadCountUpdateEvent from (ChatRoom chatRoom, ChatRoomMember chatRoomMember) {
        return new ChatListUnReadCountUpdateEvent(chatRoom.getId(), chatRoom.getLastMessage(),
                chatRoom.getLastMessageAt(), chatRoomMember.getUnreadCount());
    }
}
