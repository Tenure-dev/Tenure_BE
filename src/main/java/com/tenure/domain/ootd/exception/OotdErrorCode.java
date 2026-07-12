package com.tenure.domain.ootd.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OotdErrorCode implements ErrorCode {
    OOTD_IMAGE_REQUIRED("OOTD_001", "대표 이미지는 필수입니다.", HttpStatus.BAD_REQUEST),
    OOTD_SOURCE_INVALID("OOTD_002", "앱 내 카메라로 촬영한 사진만 게시할 수 있습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
