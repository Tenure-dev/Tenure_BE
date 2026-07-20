package com.tenure.domain.chat.repository;

import com.tenure.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("select cm from ChatMessage cm " +
            "where cm.chatRoom.id = :chatRoomId " +
            "order by cm.createdAt desc limit 1")
    Optional<ChatMessage> findByRecentMessage(@Param("chatRoomId") Long chatRoomId);


    // 채팅 내역 조회
    @Query("select cm from ChatMessage cm join fetch cm.sender " +
            "where cm.chatRoom.id = :chatRoomId " +
            "and (cm.createdAt < :cursor or (cm.createdAt = :cursor and cm.id < :cursorId)) " +
            "order by cm.createdAt desc , cm.id desc ")
    Slice<ChatMessage> findByChatMessages(@Param("chatRoomId") Long chatRoomId,
                                          @Param("cursor") LocalDateTime cursor,
                                          @Param("cursorId") Long cursorId,
                                          Pageable pageable
    );
}
