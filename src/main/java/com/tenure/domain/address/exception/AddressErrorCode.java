package com.tenure.domain.address.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AddressErrorCode implements ErrorCode {

    ADDRESS_NOT_FOUND("ADDRESS_404", "배송지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ADDRESS_FORBIDDEN("ADDRESS_403", "본인의 배송지가 아닙니다.", HttpStatus.FORBIDDEN),
    DEFAULT_ADDRESS_CANNOT_BE_DELETED("ADDRESS_409", "기본 배송지는 삭제할 수 없습니다. 다른 배송지를 기본으로 지정한 후 삭제해주세요.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}