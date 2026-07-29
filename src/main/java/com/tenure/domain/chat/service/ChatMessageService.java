package com.tenure.domain.chat.service;

import com.tenure.domain.chat.dto.request.ChatMessageRequest;
import com.tenure.domain.chat.dto.response.ChatListUnReadCountUpdateEvent;
import com.tenure.domain.chat.dto.response.ChatMessageResponse;
import com.tenure.domain.chat.entity.ChatMessage;
import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.chat.entity.ChatRoomMember;
import com.tenure.domain.chat.enums.MessageType;
import com.tenure.domain.chat.exception.ChatErrorCode;
import com.tenure.domain.chat.repository.ChatMessageRepository;
import com.tenure.domain.chat.repository.ChatRoomMemberRepository;
import com.tenure.domain.chat.repository.ChatRoomRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.service.NotificationFactory;
import com.tenure.domain.notification.service.NotificationService;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserBlockRepository;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;
    @Transactional
    public void sendMessage(Long chatRoomId, Long senderId, ChatMessageRequest request) {

        //검증
        ChatRoom chatRoom = chatRoomRepository.findByIdWithItem(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[메시지 전송] 해당 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                });

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> {
                    log.warn("[메시지 전송] 해당 유저를 찾을 수 없습니다. senderId = {}", senderId);
                    return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });

        if (!chatRoomMemberRepository.existsByUserIdAndChatRoomIdAndIsExitedFalse(senderId, chatRoomId)) {
            log.warn("[메시지 전송] 채팅방 접근 권한이 없습니다. senderId = {}, chatRoomId = {}", senderId, chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
        }


        // 상대방 조회(메시지 수신자)
        User receiver = chatRoom.getSeller().getId().equals(senderId)
                ? chatRoom.getBuyer()
                : chatRoom.getSeller();


        ChatRoomMember receiverMember = chatRoomMemberRepository.findByUserIdAndChatRoomId(receiver.getId(), chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[메시지 전송] 상대방이 채팅방에 없습니다. opponentId = {}, chatRoomId = {}", receiver.getId(), chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                });

        // 상대방이 채팅방 탈퇴했는지 점검
        if(receiverMember.isExited()) {
            log.warn("[메시지 전송] 상대방이 채팅방을 나갔습니다.");
            throw new CustomException(ChatErrorCode.CHAT_OPPONENT_EXITED);
        }

        // 둘 중 한명이라도 차단했으면 거부
        if (userBlockRepository.isBlocked(senderId, receiver.getId()) ||
                userBlockRepository.isBlocked(receiver.getId(), senderId)) {
            throw new CustomException(ChatErrorCode.CHAT_BLOCKED);
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

        // 채팅방 마지막 메시지 업데이트 (미리보기 메시지)
        String lastMessage = request.getMessageType() == MessageType.IMAGE
                ? MessageType.IMAGE.toLastMessagePreview() : request.getContent();

        chatRoom.updateLastMessage(lastMessage, chatMessage.getCreatedAt());


        // 현재 채팅방 구독중(접속중) 확인
        SimpUser user = simpUserRegistry.getUser(receiver.getId().toString());
        boolean isOnline = isOnline(chatRoomId, user);

        // 채팅방 접속중이 아닐경우 안읽음 카운트 + 1;
        if(!isOnline) {
            receiverMember.incrementUnRead();
        } else {
            receiverMember.updateLastRead(chatMessage);  // 접속중인경우: DB에도 즉시 읽음 반영
        }

        // 접속중인경우 0, 접속중이 아닌경우(안읽은 경우) 1
        int unreadCount = isOnline ? 0 : 1;

        Item item = chatRoom.getItem();

        // db 트랜젝션 이후에 브로드 캐스팅 실행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 메시지 전송(채팅방 내 실시간)
                ChatMessageResponse response = ChatMessageResponse.from(chatMessage, unreadCount);
                messagingTemplate.convertAndSend("/sub/chats/" + chatRoomId, response);

                if(!isOnline) {
                    // 채팅방 목록에 실시간 안읽음 카운트 반영
                    ChatListUnReadCountUpdateEvent chatUnReadCountUpdateEvent = ChatListUnReadCountUpdateEvent.from(chatRoom, receiverMember);
                    messagingTemplate.convertAndSend("/sub/users/" + receiver.getId(), chatUnReadCountUpdateEvent);

                    Notification chatNotification = notificationFactory
                            .chatMessage(receiver, sender, chatRoom, item, lastMessage);

                    notificationService.save(chatNotification);
                }
            }
        });
    }

    // 카톡방 접속중(구독중)인지 확인 매서드
    private static boolean isOnline(Long chatRoomId, SimpUser user) {
        return user != null && user.getSessions().stream().
                flatMap(session -> session.getSubscriptions().stream())
                .anyMatch(sub -> sub.getDestination().equals("/sub/chats/" + chatRoomId));
    }

}
