package com.tenure.domain.purchase.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PurchaseIntentErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND("PRODUCT_404", "판매 상품 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND("ITEM_404", "아이템 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BUYER_NOT_FOUND("USER_404", "사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DELIVERY_ADDRESS_NOT_FOUND("DELIVERY_ADDRESS_NOT_FOUND", "배송지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AGREEMENT_REQUIRED("AGREEMENT_REQUIRED", "거래 조건에 동의해주세요.", HttpStatus.BAD_REQUEST),
    SELF_PURCHASE_NOT_ALLOWED("PURCHASE_001", "본인 상품에는 거래 의사를 보낼 수 없습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_ON_SALE("PRODUCT_409", "거래 의사를 보낼 수 없는 상품입니다.", HttpStatus.CONFLICT),
    ACTIVE_INTENT_EXISTS("ACTIVE_INTENT_EXISTS", "이미 응답 대기 중인 거래 의사가 있습니다.", HttpStatus.CONFLICT),
    PRIVATE_PRODUCT_ACCESS_DENIED("PRIVATE_403", "비공개 계정입니다. 팔로우 요청 후 확인할 수 있어요.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
