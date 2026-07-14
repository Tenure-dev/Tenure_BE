package com.tenure.domain.ootd.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OotdErrorCode implements ErrorCode {
    OOTD_IMAGE_REQUIRED("OOTD_001", "OOTD 이미지가 필요합니다.", HttpStatus.BAD_REQUEST),
    OOTD_SOURCE_INVALID("OOTD_002", "서비스 내 카메라로 촬영한 사진만 게시할 수 있습니다.", HttpStatus.BAD_REQUEST),
    OOTD_NOT_FOUND("OOTD_404", "OOTD 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
