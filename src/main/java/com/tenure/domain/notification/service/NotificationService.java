package com.tenure.domain.notification.service;

import com.tenure.domain.notification.dto.response.NotificationCursorResponse;
import com.tenure.domain.notification.dto.response.NotificationMarkReadResponse;
import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationCategory;
import com.tenure.domain.notification.exception.NotificationErrorCode;
import com.tenure.domain.notification.repository.NotificationRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 단건 저장
    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    // 알림 여러건 저장
    @Transactional
    public void saveAll(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) return;
        notificationRepository.saveAll(notifications);
    }

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

    //단건 읽음 처리
    @Transactional
    public NotificationMarkReadResponse markAsRead(Long notificationId, Long currentUserId) {

        log.info("[단건 읽기 처리 api 호출]");

        //알림 조회
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.warn("[알림 단건 조회] 해당 알림을 찾을 수 없습니다. notificationId = {}", notificationId);
                    return new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
                });
        log.debug("[단건 읽기 처리] notificationId = {}", notificationId);

        // 수신자와 유저가 다르다면
        if(!notification.getReceiver().getId().equals(currentUserId)) {
            log.warn("[단건 읽기 처리] 본인 알림만 처리할 수 있습니다. currentUserId = {}", currentUserId);
            throw new CustomException(NotificationErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        // 읽음 처리
        notification.markRead();
        log.debug("[단건 읽기 처리] notificationId = {} 읽음처리", notificationId);
        return NotificationMarkReadResponse.from(notification);

    }

    //전체 읽음 처리
    @Transactional
    public void markReadAll(Long currentUserId) {
        log.info("[알림 모두 읽기 처리 api 호출] currentId = {}", currentUserId);

        //안읽은 게시물 모두 읽음 처리
        int count = notificationRepository.markAllRead(currentUserId, LocalDateTime.now());
        log.debug("[알림 모두 읽기 처리 완료] 총 {}건", count);
    }

    // 채팅 전송 기존 알림이 있다면 현재 알림으로 업데이트, 없다면 생성
    // afterCommit() 내에서 호출되므로 REQUIRES_NEW로 새 트랜잭션 강제 생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateChatNotification(Notification notification) {

        log.info("[채팅 알림 업데이트]");

        if(notification == null) {
            log.warn("[채팅 알림 업데이트] Notification을 찾을 수 없습니다. ");
            throw new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
        }

        notificationRepository.findByReceiverIdAndTargetIdAndType(
                notification.getReceiver().getId(), notification.getTargetId(), notification.getType())
                .ifPresentOrElse(
                        present -> present.update(notification.getBody()),
                        () -> save(notification)
                );
    }
}
