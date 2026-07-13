package com.tenure.domain.notification.dto.response;

import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationCategory;
import com.tenure.domain.notification.enums.NotificationType;
import com.tenure.domain.notification.enums.TargetType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {

    private Long id;
    private NotificationCategory category;    // 탭 분류
    private NotificationType type;            // 세부 알림 유형
    private String title;                     // 알림 제목
    private String body;                      // 알림 내용
    private TargetType targetType;            // 클릭 시 이동할 화면
    private Long targetId;                    // 이동할 리소스 ID
    private boolean isRead;                   // readAt != null
    private LocalDateTime createdAt;          // 날짜 그룹핑용

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getCategory(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getTargetType(),
                notification.getTargetId(),
                notification.getReadAt() != null,
                notification.getCreatedAt()
        );
    }
}
