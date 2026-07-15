package com.tenure.domain.item.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ItemErrorCode implements ErrorCode {

    USER_NOT_FOUND("USER_404", "사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("CATEGORY_404", "카테고리 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND("ITEM_404", "아이템 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ITEM_ACCESS_DENIED("ITEM_403", "아이템에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
