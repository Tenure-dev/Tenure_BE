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

    public static ChatMessageCursorResponse from(Slice<ChatMessage> slice) {
        List<ChatMessage> content = slice.getContent();
        boolean hasNext = slice.hasNext();

        LocalDateTime nextCursor = null;
        Long nextCursorId = null;

        if(hasNext && !content.isEmpty()) {
            ChatMessage lastMessage = content.get(content.size() - 1);
            nextCursor = lastMessage.getCreatedAt();
            nextCursorId = lastMessage.getId();
        }

        return new ChatMessageCursorResponse(
                slice.map(ChatMessageResponse::from).toList(),
                nextCursor,
                nextCursorId,
                hasNext
        );

    }
}
