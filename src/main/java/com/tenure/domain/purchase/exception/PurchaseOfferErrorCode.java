package com.tenure.domain.purchase.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PurchaseOfferErrorCode implements ErrorCode {
    ITEM_NOT_FOUND("ITEM_404", "아이템 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PURCHASE_OFFER_NOT_FOUND("PURCHASE_OFFER_404", "구매 제안 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PURCHASE_OFFER_ACCESS_DENIED("PURCHASE_OFFER_403", "구매 제안을 처리할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    PURCHASE_REQUEST_EXPIRED("PURCHASE_REQUEST_EXPIRED", "응답 시간이 지나 자동 취소된 요청입니다.", HttpStatus.CONFLICT),
    PURCHASE_OFFER_NOT_SENT("PURCHASE_OFFER_NOT_SENT", "응답 대기 중인 구매 제안만 거절할 수 있습니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
