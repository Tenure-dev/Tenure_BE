package com.tenure.domain.notification.service;

import com.tenure.domain.notification.dto.response.NotificationMarkReadResponse;
import com.tenure.domain.notification.dto.response.NotificationResponse;
import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationCategory;
import com.tenure.domain.notification.exception.NotificationErrorCode;
import com.tenure.domain.notification.repository.NotificationRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    //모든 알림 조회
    public PageResponse<NotificationResponse> findAllNotification(Long currentUserId, NotificationCategory category, boolean unReadOnly, int page, int size) {
        log.info("[모든 알림 조회 api 호출] currentUserId = {}", currentUserId);
        log.debug("[모든 알림 조회] category = {}, unReadOnly = {}, page = {}, size = {}", category, unReadOnly, page, size);

        PageRequest pageRequest = PageRequest
                .of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Notification> notifications = notificationRepository
                .findNotification(currentUserId, category, unReadOnly, pageRequest);

        log.debug("[모든 알림 조회] 총 {}건 (전체 {}건)", notifications.getNumberOfElements(), notifications.getTotalElements());


        Page<NotificationResponse> pageNotifications = notifications.map(NotificationResponse::from);
        return PageResponse.from(pageNotifications);
    }



}
