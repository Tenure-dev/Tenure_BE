package com.tenure.domain.search.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SearchErrorCode implements ErrorCode {
    KEYWORD_NOT_FOUND("SEARCH_404", "검색어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    KEYWORD_FORBIDDEN("SEARCH_403", "삭제 권한이 없습니다.", HttpStatus.FORBIDDEN),
    RECENT_USER_NOT_FOUND("SEARCH_404_2", "최근 본 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RECENT_USER_FORBIDDEN("SEARCH_403_2", "삭제 권한이 없습니다.", HttpStatus.FORBIDDEN),
    KEYWORD_OR_CATEGORY_REQUIRED("SEARCH_400", "검색어 또는 카테고리를 선택해주세요", HttpStatus.BAD_REQUEST),
    INVALID_OOTD_ID("SEARCH_400_2", "유효하지 않은 OOTD ID입니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_ID("SEARCH_400_3", "유효하지 않은 사용자 ID입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}