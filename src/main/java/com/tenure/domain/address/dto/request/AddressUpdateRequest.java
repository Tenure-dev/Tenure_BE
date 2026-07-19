package com.tenure.domain.address.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;


@Schema(description = "배송지 수정 요청 (보낸 필드만 수정)")
public record AddressUpdateRequest(

        @Schema(description = "받는 사람")
        @Size(max = 50)
        String receiverName,

        @Schema(description = "연락처")
        @Size(max = 20)
        String phone,

        @Schema(description = "주소")
        @Size(max = 255)
        String addressLine1,

        @Schema(description = "상세 주소")
        @Size(max = 255)
        String addressLine2,

        @Schema(description = "우편번호")
        @Size(max = 10)
        String postalCode,

        @Schema(description = "배송 요청사항")
        @Size(max = 300)
        String requestNote,

        @Schema(description = "기본 배송지로 지정 (true로 보내면 이 배송지가 기본이 됨)")
        Boolean isDefault
) {
}