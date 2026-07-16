package com.tenure.domain.chat.dto.response;

import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.chat.entity.ChatRoomMember;
import com.tenure.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

//채팅방 목록에서 각각의 채팅방을 담을 dto
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomSummaryResponse {
    private Long chatRoomId;
    private String opponentUsername;
    private String opponentProfileImgUrl;
    private String brandName;
    private String itemName;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;

    public static ChatRoomSummaryResponse from(ChatRoomMember member, Long currentUserId) {

        ChatRoom chatRoom = member.getChatRoom();

        //사용자가 해당 채팅방의 구매자면 판매자를, 판매자면 구매자를 반환 (상대방을 반환)
        User opponent = chatRoom.getBuyer().getId().equals(currentUserId)
                ? chatRoom.getSeller() : chatRoom.getBuyer();

        return new ChatRoomSummaryResponse(chatRoom.getId(), opponent.getUsername(), opponent.getProfileImageUrl(),
                chatRoom.getItem().getBrandName(), chatRoom.getItem().getItemName(), chatRoom.getLastMessage(),
                chatRoom.getLastMessageAt(), member.getUnreadCount());
    }
}
