package com.tenure.domain.notification.dto.response;

import com.tenure.domain.notification.entity.Notification;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationCursorResponse {

    private List<NotificationResponse> content;
    private LocalDateTime nextCursorCreatedAt;
    private Long nextCursorId;
    private boolean hasNext;

    public static NotificationCursorResponse from(Slice<Notification> slice) {
        List<Notification> content = slice.getContent();
        boolean hasNext = slice.hasNext();

        LocalDateTime nextCursorCreatedAt = null;
        Long nextCursorId = null;

        if (hasNext && !content.isEmpty()) {
            Notification last = content.get(content.size() - 1);
            nextCursorCreatedAt = last.getCreatedAt();
            nextCursorId = last.getId();
        }

        return new NotificationCursorResponse(
                content.stream().map(NotificationResponse::from).toList(),
                nextCursorCreatedAt,
                nextCursorId,
                hasNext
        );
    }
}
