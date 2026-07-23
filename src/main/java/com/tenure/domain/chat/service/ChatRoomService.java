package com.tenure.domain.chat.service;

import com.tenure.domain.chat.dto.response.ChatMessageCursorResponse;
import com.tenure.domain.chat.dto.response.ChatRoomListCursorResponse;
import com.tenure.domain.chat.dto.response.ChatRoomResponse;
import com.tenure.domain.chat.entity.ChatMessage;
import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.chat.entity.ChatRoomMember;
import com.tenure.domain.chat.enums.ChatRoomFilterType;
import com.tenure.domain.chat.exception.ChatErrorCode;
import com.tenure.domain.chat.repository.ChatMessageRepository;
import com.tenure.domain.chat.repository.ChatRoomMemberRepository;
import com.tenure.domain.chat.repository.ChatRoomRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.exception.ItemErrorCode;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserBlockRepository;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.tenure.domain.product.enums.ProductStatus.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserBlockRepository userBlockRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TradeRepository tradeRepository;

    // 채팅방 조회 / 생성
    @Transactional
    public ChatRoomResponse findOrCreateChatRoom(Long buyerId, Long itemId) {

        log.info("[채팅방 생성/조회] buyerId = {}, itemId = {}", buyerId, itemId);

        //아이템 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 생성/조회] 해당 아이템은 존재하지 않습니다. itemId = {}", itemId);
                    return new CustomException(ItemErrorCode.ITEM_NOT_FOUND);
                });

        //아이템 주인 조회
        User owner = userRepository.findById(item.getOwner().getId())
                .orElseThrow(() -> {
                    log.warn("[채팅방 생성/조회] 아이템 주인을 찾을 수 없습니다. ownerId = {}", item.getOwner().getId());
                    return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });

        //구매자(사용자) 조회
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 생성/조회] 해당 구매자는 존재하지 않습니다. buyerId = {}", buyerId);
                    return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });

        if(buyerId.equals(owner.getId())) {
            log.warn("[채팅방 생성 / 조회] 본인의 아이템에는 채팅방을 생성 할 수없습니다. buyerId = {}, ownerId = {}", buyerId, owner.getId());
            throw new CustomException(ChatErrorCode.CHAT_CREATION_NOT_ALLOWED);
        }

        if(userBlockRepository.isBlocked(buyerId, owner.getId()) || userBlockRepository.isBlocked(owner.getId(), buyerId)) {
            log.warn("[채팅방 생성 / 조회] 차단된 사용자와는 채팅 할 수 없습니다.");
            throw new CustomException(ChatErrorCode.CHAT_BLOCKED);
        }


        //product 중에서 해당 아이템이 판매중이거나 거래중인 항목을 찾음
        Product product = productRepository.findByItemIdAndProductStatusIn(itemId, List.of(ON_SALE, TRADING))
                .orElseThrow(() -> {
                    log.warn("[채팅방 생성/조회] 해당 상품은 판매중이거나 거래중이 아닙니다. itemId = {}", itemId);
                    return new CustomException(ProductErrorCode.PRODUCT_NOT_ON_SALE);
                });


        ChatRoom  chatRoom;

        // 사용자가 동시에 채팅방 생성을 할 경우 방지
        try {
            //체팅방을 조회 / 없으면 새로 만든 후 저장
            chatRoom = chatRoomRepository.findByItemIdAndSellerIdAndBuyerId(itemId, owner.getId(), buyerId)
                    .orElseGet(() -> createChatRoom(item, buyer, owner));
        } catch (DataIntegrityViolationException e) {
            chatRoom = chatRoomRepository.findByItemIdAndSellerIdAndBuyerId(itemId, owner.getId(), buyerId)
                    .orElseThrow(() -> {
                        log.warn("[채팅방 생성/조회] 채팅방을 찾을 수 없습니다. itemId = {}, ownerId = {}, buyerId = {}", itemId, owner.getId(), buyerId );
                        return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                    });
        }



        Long tradeId = tradeRepository.findByItemId(itemId).map(Trade::getId).orElse(null);
        return ChatRoomResponse.from(chatRoom, item, product, buyerId, tradeId);
    }

    // 채팅방 목록 조회
    public ChatRoomListCursorResponse chatRoomList(Long currentUserId, ChatRoomFilterType type,
                             LocalDateTime cursor, LocalDateTime createdAtCursor, Long cursorId, int size)
    {

        if(cursor == null) cursor = LocalDateTime.now();
        if(cursorId == null) cursorId = Long.MAX_VALUE;
        if(createdAtCursor == null) createdAtCursor = LocalDateTime.now();

        log.info("[채팅방 목록 조회] currentUserId = {}, type = {}, cursor = {}, createdAtCursor = {}, cursorId = {}, size = {}", currentUserId, type, cursor, createdAtCursor, cursorId, size);

        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<ChatRoomMember> chatRooms = switch (type) {
            case BUYING -> chatRoomMemberRepository.findBuyingChatRooms(currentUserId, cursor, createdAtCursor, cursorId, pageRequest);
            case SELLING -> chatRoomMemberRepository.findSellingChatRooms(currentUserId, cursor, createdAtCursor, cursorId, pageRequest);
            case UNREAD -> chatRoomMemberRepository.findUnreadChatRooms(currentUserId, cursor, createdAtCursor, cursorId, pageRequest);
            default -> chatRoomMemberRepository.findAllChatRooms(currentUserId, cursor, createdAtCursor, cursorId, pageRequest); //기본 전체 조회
        };

        log.info("[채팅방 목록 조회] 조회 결과 = {}건, hasNext = {}", chatRooms.getContent().size(), chatRooms.hasNext());
        return ChatRoomListCursorResponse.from(chatRooms, currentUserId);
    }

    //채팅방 목록에서 채팅방 접속
    public ChatRoomResponse enterChatroom(Long currentUserId, Long chatRoomId) {

        // 채팅방 존재 확인
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 조회] 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                });

        // 채팅방 권한 체크
        if(!chatRoomMemberRepository.existsByUserIdAndChatRoomId(currentUserId, chatRoomId)) {
            log.warn("[채팅방 조회] 채팅방 접근 권한이 없습니다. currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);
            throw  new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
        }

        Item item = chatRoom.getItem();

        //해당 아이템의 product 조회
        Product product = productRepository.findByItemId(item.getId())
                .orElseThrow(() -> {
                    log.warn("[채팅방 조회] 상품을 찾을 수 없습니다. itemId = {}", item.getId());
                    return new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND);
                });

        //아이템에 대한 거래가 성사됐는지 판단
        Long tradeId = tradeRepository
                .findByItemId(item.getId()).map(Trade::getId).orElse(null);

        return ChatRoomResponse.from(chatRoom, item, product, currentUserId, tradeId);
    }

    //채팅방 접속 시 unreadCount 업데이트
    @Transactional
    public void updateRead(Long currentUserId, Long chatRoomId) {

        //채팅방이 없으면
        if (!chatRoomRepository.existsById(chatRoomId)) {
            log.warn("[읽음 처리] 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        // 해당 채팅방 멤버가 아니면
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByUserIdAndChatRoomId(currentUserId, chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[읽음 처리] 채팅방 접근 권한이 없습니다. currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
                });

        // 해당 채팅방의 가장 최근 메시지 조회
        ChatMessage chatMessage = chatMessageRepository.findByRecentMessage(chatRoomId)
                .orElse(null);

        chatRoomMember.updateLastRead(chatMessage);

    }

    // 채팅 내역 조회
    public ChatMessageCursorResponse getMessages(Long currentUserId, Long chatRoomId
            , LocalDateTime cursor, Long cursorId, int size) {

        if(cursor == null) cursor = LocalDateTime.now();
        if(cursorId == null) cursorId = Long.MAX_VALUE;

        log.info("[채팅 내역 조회] currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);

        if (!chatRoomRepository.existsById(chatRoomId)) {
            log.warn("[채팅 내역 조회] 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        if (chatRoomMemberRepository.findByUserIdAndChatRoomId(currentUserId, chatRoomId).isEmpty()) {
            log.warn("[채팅 내역 조회] 해당 채팅방에 접근 권한이 없습니다. currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
        }

        
        PageRequest request = PageRequest.of(0, size);

        Slice<ChatMessage> chatMessages = chatMessageRepository
                .findByChatMessages(chatRoomId, cursor, cursorId, request);

        log.info("[채팅 내역 조회] 채팅 내역 조회 성공");
        return ChatMessageCursorResponse.from(chatMessages);
    }

    //채팅방 생성 매서드
    private ChatRoom createChatRoom(Item item, User buyer, User owner) {
        ChatRoom newRoom = chatRoomRepository.save(ChatRoom.of(item, buyer, owner));
        chatRoomMemberRepository.save(ChatRoomMember.of(newRoom, buyer));
        chatRoomMemberRepository.save(ChatRoomMember.of(newRoom, owner));
        return newRoom;
    }


}
