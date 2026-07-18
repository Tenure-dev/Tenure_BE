package com.tenure.global.security;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// JWT 인증 관련 에러 코드
@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {

    INVALID_TOKEN("SECURITY_1001", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("SECURITY_1002", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;
}