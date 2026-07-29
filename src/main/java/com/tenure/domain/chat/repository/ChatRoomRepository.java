package com.tenure.domain.chat.repository;

import com.tenure.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    //아이템, 판매자, 구매자로 채팅방 조회
    Optional<ChatRoom> findByItemIdAndSellerIdAndBuyerId(Long itemId, Long sellerId, Long buyerId);

    @Query("select c from ChatRoom c " +
            "join fetch c.item join fetch c.buyer join fetch c.seller " +
            "where c.id = :chatRoomId")
    Optional<ChatRoom> findByIdWithItem(Long chatRoomId);
}
