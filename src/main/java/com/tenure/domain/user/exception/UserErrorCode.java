package com.tenure.domain.user.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND("USER_404", "사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_BLOCKED("USER_403", "차단된 사용자입니다.", HttpStatus.FORBIDDEN),

    // 회원가입, 인증 관련 추가
    EMAIL_ALREADY_EXISTS("USER_1001", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("USER_1002", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    PASSWORD_MISMATCH("USER_1003", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED("USER_1004", "이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_NOT_VERIFIED("USER_1005", "이메일 인증이 완료되지 않았습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
