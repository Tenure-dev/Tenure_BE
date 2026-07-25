package com.tenure.domain.chat.service;

import com.tenure.domain.chat.dto.request.ChatMessageRequest;
import com.tenure.domain.chat.dto.response.ChatMessageResponse;
import com.tenure.domain.chat.entity.ChatMessage;
import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.chat.entity.ChatRoomMember;
import com.tenure.domain.chat.enums.MessageType;
import com.tenure.domain.chat.exception.ChatErrorCode;
import com.tenure.domain.chat.repository.ChatMessageRepository;
import com.tenure.domain.chat.repository.ChatRoomMemberRepository;
import com.tenure.domain.chat.repository.ChatRoomRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpUserRegistry simpUserRegistry;
    @Transactional
    public ChatMessageResponse sendMessage(Long chatRoomId, Long senderId, ChatMessageRequest request) {

        //검증
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[메시지 전송] 해당 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                });

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> {
                    log.warn("[메시지 전송] 해당 유저를 찾을 수 없습니다. senderId = {}", senderId);
                    return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });

        if (!chatRoomMemberRepository.existsByUserIdAndChatRoomId(senderId, chatRoomId)) {
            log.warn("[메시지 전송] 채팅방 접근 권한이 없습니다. senderId = {}, chatRoomId = {}", senderId, chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
        }

        // 메시지 타입별 내용 검증
        if (request.getMessageType() == MessageType.TEXT &&
                (request.getContent() == null || request.getContent().isBlank())) {
            throw new CustomException(ChatErrorCode.INVALID_MESSAGE_CONTENT);
        }
        // 이미지면 이미지 경로가 있어야 함.
        if (request.getMessageType() == MessageType.IMAGE &&
                (request.getImageUrl() == null || request.getImageUrl().isBlank())) {
            throw new CustomException(ChatErrorCode.INVALID_MESSAGE_CONTENT);
        }

        //메시지 저장
        ChatMessage chatMessage = ChatMessage.of(chatRoom, sender, request.getMessageType(), request.getContent(), request.getImageUrl());
        chatMessageRepository.save(chatMessage);

        // 채팅방 마지막 메시지 업데이트
        String lastMessage = request.getMessageType() == MessageType.IMAGE
                ? MessageType.IMAGE.toLastMessagePreview() : request.getContent();

        chatRoom.updateLastMessage(lastMessage, chatMessage.getCreatedAt());


        Long opponentId = chatRoom.getSeller().getId().equals(senderId)
                ? chatRoom.getBuyer().getId() : chatRoom.getSeller().getId();

        ChatRoomMember receiverMember = chatRoomMemberRepository.findByUserIdAndChatRoomId(opponentId, chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[메시지 전송] 상대방이 채팅방에 없습니다. opponentId = {}, chatRoomId = {}", opponentId, chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                });

        // 현재 채팅방 구독중(접속중) 확인
        SimpUser user = simpUserRegistry.getUser(opponentId.toString());
        boolean isOnline = isOnline(chatRoomId, user);

        // 채팅방 접속중이 아닐경우 안읽음 카운트 + 1;
        if(!isOnline) {
            receiverMember.incrementUnRead();
        } else {
            receiverMember.updateLastRead(chatMessage);  // 접속중인경우: DB에도 즉시 읽음 반영
        }

        // 접속중인경우 0, 접속중이 아닌경우(안읽은 경우) 1
        int unreadCount = isOnline ? 0 : 1;

        return ChatMessageResponse.from(chatMessage, unreadCount);
    }

    // 카톡방 접속중(구독중)인지 확인 매서드
    private static boolean isOnline(Long chatRoomId, SimpUser user) {
        return user != null && user.getSessions().stream().
                flatMap(session -> session.getSubscriptions().stream())
                .anyMatch(sub -> sub.getDestination().equals("/sub/chats/" + chatRoomId));
    }

}
