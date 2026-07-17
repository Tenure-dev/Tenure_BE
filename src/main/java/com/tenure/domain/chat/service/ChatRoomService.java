package com.tenure.domain.chat.service;

import com.tenure.domain.chat.dto.response.ChatRoomResponse;
import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.chat.entity.ChatRoomMember;
import com.tenure.domain.chat.exception.ChatErrorCode;
import com.tenure.domain.chat.repository.ChatRoomMemberRepository;
import com.tenure.domain.chat.repository.ChatRoomRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.exception.ItemErrorCode;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserBlockRepository;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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



        return ChatRoomResponse.from(chatRoom, owner, item, product);
    }

    //채팅방 생성 매서드
    private ChatRoom createChatRoom(Item item, User buyer, User owner) {
        ChatRoom newRoom = chatRoomRepository.save(ChatRoom.of(item, buyer, owner));
        chatRoomMemberRepository.save(ChatRoomMember.of(newRoom, buyer));
        chatRoomMemberRepository.save(ChatRoomMember.of(newRoom, owner));
        return newRoom;
    }


}
