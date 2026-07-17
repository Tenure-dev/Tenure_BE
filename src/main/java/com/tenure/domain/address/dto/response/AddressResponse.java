package com.tenure.domain.address.dto.response;

import com.tenure.domain.address.entity.DeliveryAddress;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "배송지 응답")
public record AddressResponse(

        @Schema(description = "배송지 ID", example = "1")
        Long addressId,

        @Schema(description = "받는 사람", example = "박유진")
        String receiverName,

        @Schema(description = "연락처", example = "010-1234-5678")
        String phone,

        @Schema(description = "주소", example = "서울 동작구 사당로 50")
        String addressLine1,

        @Schema(description = "상세 주소", example = "104동 501호")
        String addressLine2,

        @Schema(description = "우편번호", example = "07027")
        String postalCode,

        @Schema(description = "배송 요청사항")
        String requestNote,

        @Schema(description = "기본 배송지 여부", example = "true")
        Boolean isDefault
) {
    public static AddressResponse from(DeliveryAddress address) {
        return new AddressResponse(
                address.getId(),
                address.getReceiverName(),
                address.getPhone(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getPostalCode(),
                address.getRequestNote(),
                address.getIsDefault()
        );
    }
}