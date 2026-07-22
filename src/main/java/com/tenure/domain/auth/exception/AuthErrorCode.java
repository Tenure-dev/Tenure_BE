package com.tenure.domain.auth.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 인증 에러
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    EMAIL_ALREADY_EXISTS("AUTH_1001", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    EMAIL_VERIFICATION_FAILED("AUTH_1002", "인증번호가 올바르지 않거나 만료되었습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED("AUTH_1003", "이메일 인증이 완료되지 않았습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}