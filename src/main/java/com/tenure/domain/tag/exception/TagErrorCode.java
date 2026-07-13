package com.tenure.domain.tag.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagErrorCode implements ErrorCode {
    OOTD_NOT_FOUND("TAG_001", "OOTD 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND("TAG_002", "아이템 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TAG_OWNER_ONLY("TAG_003", "본인이 게시한 OOTD에만 태그를 등록할 수 있습니다.", HttpStatus.FORBIDDEN),
    TAG_STATUS_INVALID("TAG_004", "직접 등록하는 태그는 CONFIRMED 상태만 허용됩니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
