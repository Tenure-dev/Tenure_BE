package com.tenure.domain.notification.dto.response;

import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.TargetType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMarkReadResponse {

    private Long id;
    private TargetType targetType;
    private Long targetId;

    public static NotificationMarkReadResponse from(Notification notification) {
        return new NotificationMarkReadResponse(
                notification.getId(),
                notification.getTargetType(),
                notification.getTargetId()
        );
    }
}
