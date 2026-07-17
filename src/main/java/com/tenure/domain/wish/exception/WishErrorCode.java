package com.tenure.domain.wish.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WishErrorCode implements ErrorCode {

    USER_NOT_FOUND("USER_404", "사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND("ITEM_404", "아이템 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WISH_ALREADY_EXISTS("WISH_409", "이미 위시 등록한 아이템입니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}