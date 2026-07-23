package com.tenure.domain.chat.repository;

import com.tenure.domain.chat.entity.ChatRoomMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    // 전체 채팅방
    @Query("select crm from ChatRoomMember crm " +
            "join fetch crm.chatRoom c " +
            "join fetch c.buyer " +
            "join fetch c.seller " +
            "join fetch c.item " +
            "where crm.user.id = :userId " +
            "and ((c.lastMessageAt is null and (c.createdAt < :createdAtCursor or (c.createdAt = :createdAtCursor and c.id < :cursorId))) or c.lastMessageAt < :cursor or (c.lastMessageAt = :cursor and c.id < :cursorId))" +
            "order by c.lastMessageAt desc nulls last, c.createdAt desc, c.id desc") // lastMessageAt 기준 정렬, 채팅방에 대화내역이 없어서 lastMessageAt = null 인경우 맨 아래 배치
    Slice<ChatRoomMember> findAllChatRooms(@Param("userId") Long userId,
                                           @Param("cursor") LocalDateTime cursor,
                                           @Param("createdAtCursor") LocalDateTime createdAtCursor,
                                           @Param("cursorId") Long cursorId,
                                           Pageable pageable);

    // 구매 채팅방
    @Query("select crm from ChatRoomMember crm " +
            "join fetch crm.chatRoom c " +
            "join fetch c.buyer " +
            "join fetch c.seller " +
            "join fetch c.item " +
            "where crm.user.id = :userId " +
            "and c.buyer.id = :userId " +
            "and ((c.lastMessageAt is null and (c.createdAt < :createdAtCursor or (c.createdAt = :createdAtCursor and c.id < :cursorId))) or c.lastMessageAt < :cursor or (c.lastMessageAt = :cursor and c.id < :cursorId))" +
            "order by c.lastMessageAt desc nulls last, c.createdAt desc, c.id desc")
    Slice<ChatRoomMember> findBuyingChatRooms(@Param("userId") Long userId,
                                              @Param("cursor") LocalDateTime cursor,
                                              @Param("createdAtCursor") LocalDateTime createdAtCursor,
                                              @Param("cursorId") Long cursorId,
                                              Pageable pageable);

    // 판매 채팅방
    @Query("select crm from ChatRoomMember crm " +
            "join fetch crm.chatRoom c " +
            "join fetch c.buyer " +
            "join fetch c.seller " +
            "join fetch c.item " +
            "where crm.user.id = :userId " +
            "and c.seller.id = :userId " +
            "and ((c.lastMessageAt is null and (c.createdAt < :createdAtCursor or (c.createdAt = :createdAtCursor and c.id < :cursorId))) or c.lastMessageAt < :cursor or (c.lastMessageAt = :cursor and c.id < :cursorId))" +
            "order by c.lastMessageAt desc nulls last, c.createdAt desc, c.id desc")
    Slice<ChatRoomMember> findSellingChatRooms(@Param("userId") Long userId,
                                               @Param("cursor") LocalDateTime cursor,
                                               @Param("createdAtCursor") LocalDateTime createdAtCursor,
                                               @Param("cursorId") Long cursorId,
                                               Pageable pageable);

    // 읽지 않은 채팅방
    @Query("select crm from ChatRoomMember crm " +
            "join fetch crm.chatRoom c " +
            "join fetch c.buyer " +
            "join fetch c.seller " +
            "join fetch c.item " +
            "where crm.user.id = :userId " +
            "and crm.unreadCount > 0 " +
            "and ((c.lastMessageAt is null and (c.createdAt < :createdAtCursor or (c.createdAt = :createdAtCursor and c.id < :cursorId))) or c.lastMessageAt < :cursor or (c.lastMessageAt = :cursor and c.id < :cursorId))" +
            "order by c.lastMessageAt desc nulls last, c.createdAt desc, c.id desc")
    Slice<ChatRoomMember> findUnreadChatRooms(@Param("userId") Long userId,
                                              @Param("cursor") LocalDateTime cursor,
                                              @Param("createdAtCursor") LocalDateTime createdAtCursor,
                                              @Param("cursorId") Long cursorId,
                                              Pageable pageable);

    //해당 채팅방의 사용자 조회
    Optional<ChatRoomMember> findByUserIdAndChatRoomId(Long user_id, Long chatRoom_id);

    boolean existsByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
