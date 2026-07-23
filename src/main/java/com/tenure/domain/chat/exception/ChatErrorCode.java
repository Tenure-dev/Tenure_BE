package com.tenure.domain.chat.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_ROOM_NOT_FOUND("CHAT_404", "채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_FORBIDDEN("CHAT_403", "채팅방에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    CHAT_BLOCKED("CHAT_403_BLOCKED", "차단된 사용자와는 채팅할 수 없습니다.", HttpStatus.FORBIDDEN),
    CHAT_CREATION_NOT_ALLOWED("CHAT_400", "채팅방을 생성할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE("CHAT_400_IMAGE", "지원하지 않는 이미지 형식입니다. (jpeg, png, gif, webp, heic만 허용)", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}