package com.tenure.domain.notification.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND("NOTIFICATION_404", "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_ACCESS_DENIED("NOTIFICATION_403", "본인 알림만 처리할 수 있습니다.", HttpStatus.FORBIDDEN);


    private final String code;
    private final String message;
    private final HttpStatus status;
}
