package com.tenure.domain.search.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SearchErrorCode implements ErrorCode {
    KEYWORD_NOT_FOUND("SEARCH_404", "검색어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    KEYWORD_FORBIDDEN("SEARCH_403", "삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}