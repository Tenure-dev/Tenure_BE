package com.tenure.domain.notification.repository;

import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    // 전체 알림 조회
    @Query("select n from Notification n " +
            "where n.receiver.id = :receiver_id " +
            "and (:category is null or n.category = :category) " +
            "and (:unReadOnly = false or n.readAt is null) " +
            "and (n.createdAt < :cursor or (n.createdAt = :cursor and n.id < :cursorId))")
    Slice<Notification> findNotification(
            @Param("receiver_id") Long receiver_id,
            @Param("category")NotificationCategory category,
            @Param("unReadOnly") boolean unReadOnly,
            @Param("cursor") LocalDateTime cursor,
            @Param("cursorId") Long cursorId,
            Pageable pageable);

    // 전체 읽음 수정
    // readAt = null (읽지 않은 알림들) 모두 조회
    @Query("select n from Notification n " +
            "where n.receiver.id = :currentUserId " +
            "and n.readAt is NULL")
    List<Notification> findNotRead(@Param("currentUserId") Long currentUserId);
}
