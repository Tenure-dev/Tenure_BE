package com.tenure.domain.chat.repository;

import com.tenure.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("select cm from ChatMessage cm " +
            "where cm.chatRoom.id = :chatRoomId " +
            "order by cm.createdAt desc limit 1")
    Optional<ChatMessage> findByRecentMessage(@Param("chatRoomId") Long chatRoomId);
}
