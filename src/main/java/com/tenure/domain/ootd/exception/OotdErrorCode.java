package com.tenure.domain.ootd.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OotdErrorCode implements ErrorCode {
    OOTD_IMAGE_REQUIRED("OOTD_001", "대표 이미지는 필수입니다.", HttpStatus.BAD_REQUEST),
    OOTD_SOURCE_INVALID("OOTD_002", "앱 내 카메라로 촬영한 사진만 게시할 수 있습니다.", HttpStatus.BAD_REQUEST),
    OOTD_NOT_FOUND("OOTD_003", "OOTD를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRIVATE_OOTD_ACCESS_DENIED("OOTD_004", "비공개 계정입니다. 팔로우 요청 후 확인할 수 있어요.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
