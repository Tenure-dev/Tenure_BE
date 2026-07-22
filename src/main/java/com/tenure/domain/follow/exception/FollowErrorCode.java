package com.tenure.domain.follow.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum FollowErrorCode implements ErrorCode {

    CANNOT_FOLLOW_SELF("FOLLOW_400", "자기 자신은 팔로우할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOWING("FOLLOW_409", "이미 팔로우한 사용자입니다.", HttpStatus.CONFLICT),
    FOLLOW_NOT_FOUND("FOLLOW_404", "팔로우 관계를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}