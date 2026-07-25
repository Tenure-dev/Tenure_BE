package com.tenure.domain.chat.controller;

import com.tenure.domain.chat.dto.request.ChatMessageRequest;
import com.tenure.domain.chat.dto.response.ChatMessageResponse;
import com.tenure.domain.chat.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


// 웹소캣 + STOMP 메시지 전송
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    // /pub/chats/{chatRoomId}
    @MessageMapping("/chats/{chatRoomId}")
    public void sendMessage(
            @DestinationVariable Long chatRoomId,
            @Valid @Payload ChatMessageRequest chatMessageRequest,
            SimpMessageHeaderAccessor headerAccessor
            ) {
        Long currentUserId = Long.valueOf(headerAccessor.getUser().getName());

        // 트랜잭션 커밋 후 브로드캐스트 (커밋 전 전송 시 수신자 REST 조회에서 메시지 누락 방지)
        ChatMessageResponse response = chatMessageService.sendMessage(chatRoomId, currentUserId, chatMessageRequest);
        messagingTemplate.convertAndSend("/sub/chats/" + chatRoomId, response);
    }
}
