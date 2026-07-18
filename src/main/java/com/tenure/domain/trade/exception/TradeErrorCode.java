package com.tenure.domain.trade.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TradeErrorCode implements ErrorCode {
    TRADE_NOT_FOUND("TRADE_404", "거래 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRADE_INVALID_TRANSITION("TRADE_409_INVALID_TRANSITION", "현재 상태에서 불가능한 상태 변경입니다.", HttpStatus.CONFLICT),
    TRADE_FORBIDDEN_TRANSITION("TRADE_403_FORBIDDEN_TRANSITION", "해당 상태 변경 권한이 없습니다.", HttpStatus.FORBIDDEN),
    TRADE_TRACKING_REQUIRED("TRADE_400_TRACKING_REQUIRED", "택배사와 운송장 번호를 입력해주세요.", HttpStatus.BAD_REQUEST),
    TRADE_INVALID_TRACKING("TRADE_400_INVALID_TRACKING", "운송장 번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    TRADE_ALREADY_EXISTS_FOR_ITEM("TRADE_409_ALREADY_EXISTS", "이미 진행 중인 거래가 있는 아이템입니다.", HttpStatus.CONFLICT),
    TRADE_STATUS_FILTER_NOT_ALLOWED("TRADE_400_STATUS_FILTER_NOT_ALLOWED", "TRANSFERRED는 조회 가능한 상태 필터가 아닙니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
