package com.tenure.domain.chat.dto.response;

import com.tenure.domain.chat.entity.ChatMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageCursorResponse {

    private List<ChatMessageResponse> chatMessages;
    private LocalDateTime nextCursor;
    private Long nextCursorId;
    private boolean hasNext;
    private Long opponentLastReadMessageId;

    public static ChatMessageCursorResponse from(Slice<ChatMessage> slice, Long currentUserId, Long opponentLastReadMessageId) {
        List<ChatMessage> content = slice.getContent();
        boolean hasNext = slice.hasNext();

        LocalDateTime nextCursor = null;
        Long nextCursorId = null;

        if(hasNext && !content.isEmpty()) {
            ChatMessage lastMessage = content.get(content.size() - 1);
            nextCursor = lastMessage.getCreatedAt();
            nextCursorId = lastMessage.getId();
        }

        // unreadCount: 상대방이 내 메시지를 안읽었으면 1, 읽었으면 0
        List<ChatMessageResponse> chatMessages = content.stream().map(m -> {
            int unreadCount = m.getSender().getId().equals(currentUserId)
                    && m.getId() > opponentLastReadMessageId ? 1 : 0;
            return ChatMessageResponse.from(m, unreadCount);
        }).toList();

        return new ChatMessageCursorResponse(
                chatMessages,
                nextCursor,
                nextCursorId,
                hasNext,
                opponentLastReadMessageId
        );

    }
}
