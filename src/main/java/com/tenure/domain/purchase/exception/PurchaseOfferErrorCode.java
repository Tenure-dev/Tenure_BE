package com.tenure.domain.purchase.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PurchaseOfferErrorCode implements ErrorCode {
    ITEM_NOT_FOUND("ITEM_404", "아이템 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PROPOSER_NOT_FOUND("USER_404", "사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DELIVERY_ADDRESS_NOT_FOUND("DELIVERY_ADDRESS_NOT_FOUND", "배송지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PURCHASE_OFFER_NOT_FOUND("PURCHASE_OFFER_404", "구매 제안 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PURCHASE_OFFER_ACCESS_DENIED("PURCHASE_OFFER_403", "구매 제안을 처리할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    AGREEMENT_REQUIRED("AGREEMENT_REQUIRED", "거래 조건에 동의해주세요.", HttpStatus.BAD_REQUEST),
    OFFER_PRICE_TOO_LOW("PURCHASE_OFFER_PRICE_TOO_LOW", "최소 제안 금액은 1,000원입니다.", HttpStatus.BAD_REQUEST),
    SELF_OFFER_NOT_ALLOWED("PURCHASE_OFFER_SELF_NOT_ALLOWED", "본인 아이템에는 구매 제안을 보낼 수 없습니다.", HttpStatus.BAD_REQUEST),
    ITEM_NOT_OWNED("PURCHASE_OFFER_ITEM_NOT_OWNED", "미판매 보유 아이템에만 구매 제안을 보낼 수 있습니다.", HttpStatus.BAD_REQUEST),
    PURCHASE_OFFER_DISABLED("PURCHASE_OFFER_DISABLED", "이 아이템은 구매 제안을 받지 않습니다.", HttpStatus.BAD_REQUEST),
    PURCHASE_OFFER_ALREADY_USED("PURCHASE_OFFER_ALREADY_USED", "이미 이 아이템에 구매 제안을 보냈습니다.", HttpStatus.CONFLICT),
    PURCHASE_REQUEST_EXPIRED("PURCHASE_REQUEST_EXPIRED", "응답 시간이 지나 자동 취소된 요청입니다.", HttpStatus.CONFLICT),
    PURCHASE_OFFER_NOT_SENT("PURCHASE_OFFER_NOT_SENT", "응답 대기 중인 구매 제안만 거절할 수 있습니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
