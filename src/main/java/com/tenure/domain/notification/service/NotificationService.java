package com.tenure.domain.notification.service;

import com.tenure.domain.notification.dto.response.NotificationCursorResponse;
import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationCategory;
import com.tenure.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    //모든 알림 조회
    public NotificationCursorResponse findAllNotification(
            Long currentUserId, NotificationCategory category, boolean unReadOnly,
            int size, Long cursorId, LocalDateTime cursor)
    {

        log.info("[모든 알림 조회 api 호출] currentUserId = {}", currentUserId);


        if(cursor == null) {
            cursor = LocalDateTime.now();
        }

        if (cursorId == null) {
            cursorId = Long.MAX_VALUE;
        }

        log.debug("[모든 알림 조회] category = {}, unReadOnly = {}, size = {}, cursorId = {}, cursor = {}", category, unReadOnly, size, cursorId, cursor);

        PageRequest pageRequest = PageRequest
                .of(0, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));

        Slice<Notification> sliceNotification = notificationRepository
                .findNotification(currentUserId, category, unReadOnly, cursor, cursorId, pageRequest);

        log.debug("[모든 알림 조회] 조회 {}건, hasNext = {}", sliceNotification.getNumberOfElements(), sliceNotification.hasNext());

        return NotificationCursorResponse.from(sliceNotification);

    }



}
