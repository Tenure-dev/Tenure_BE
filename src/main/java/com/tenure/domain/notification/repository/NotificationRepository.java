package com.tenure.domain.notification.repository;

import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    // 전체 알림 조회
    @Query("select n from Notification n " +
            "where n.receiver.id = :receiver_id " +
            "and (:category is null or n.category = :category) " +
            "and (:unReadOnly = false or n.readAt is null) ")
    Page<Notification> findNotification(
            @Param("receiver_id") Long receiver_id,
            @Param("category")NotificationCategory category,
            @Param("unReadOnly") boolean unReadOnly,
            Pageable pageable);


}
