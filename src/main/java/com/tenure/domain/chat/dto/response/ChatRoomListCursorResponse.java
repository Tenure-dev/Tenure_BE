package com.tenure.domain.chat.dto.response;

import com.tenure.domain.chat.entity.ChatRoomMember;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomListCursorResponse {

    private List<ChatRoomSummaryResponse> content;
    private LocalDateTime nextCursorLastMessageAt;
    private Long nextCursorId;
    private boolean hasNext;

    public static ChatRoomListCursorResponse from (Slice<ChatRoomMember> slice, Long currentUserId) {
        List<ChatRoomMember> chatRoomMembers = slice.getContent();

        LocalDateTime nextCursorLastMessageAt = null;
        Long nextCursorId = null;
        boolean hasNext = slice.hasNext();

        if(hasNext) {
            ChatRoomMember last = chatRoomMembers.get(chatRoomMembers.size() - 1); // 맨 마지막 결과물
            nextCursorLastMessageAt = last.getChatRoom().getLastMessageAt();
            nextCursorId = last.getChatRoom().getId();
        }


        return new ChatRoomListCursorResponse(
                slice.map(chatRoomMember ->
                        ChatRoomSummaryResponse.from(chatRoomMember, currentUserId)).toList(),
                nextCursorLastMessageAt,
                nextCursorId,
                hasNext
        );

    }
}
