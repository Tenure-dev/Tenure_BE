package com.tenure.domain.product.exception;

import com.tenure.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND("PRODUCT_404", "판매 상품 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRIVATE_PRODUCT_ACCESS_DENIED("PRIVATE_403", "비공개 계정입니다. 팔로우 요청 후 확인할 수 있어요.", HttpStatus.FORBIDDEN),
    ITEM_NOT_FOUND("ITEM_404", "아이템 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_OWNER_ONLY("PRODUCT_403", "본인 아이템만 판매 전환할 수 있습니다.", HttpStatus.FORBIDDEN),
    PRODUCT_ITEM_STATUS_INVALID("PRODUCT_001", "보유 중인 아이템만 판매 전환할 수 있습니다.", HttpStatus.BAD_REQUEST),
    BASIC_USER_FEE_POLICY_INVALID("PRODUCT_002", "기본 사용자는 판매자 수수료 부담으로 고정됩니다.", HttpStatus.FORBIDDEN),
    BASIC_USER_SHIPPING_FEE_INVALID("PRODUCT_003", "기본 사용자는 배송비 설정을 사용할 수 없습니다.", HttpStatus.FORBIDDEN),
    ATTACHED_OOTD_DUPLICATED("PRODUCT_004", "공개할 OOTD가 중복 선택되었습니다.", HttpStatus.BAD_REQUEST),
    ATTACHED_OOTD_INVALID("PRODUCT_005", "공개할 수 없는 OOTD가 포함되어 있습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_JSON_INVALID("PRODUCT_006", "판매 상품 입력값을 저장할 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
