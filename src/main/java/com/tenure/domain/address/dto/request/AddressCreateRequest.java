package com.tenure.domain.address.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Schema(description = "배송지 등록 요청")
public record AddressCreateRequest(

        @Schema(description = "받는 사람", example = "박유진")
        @NotBlank(message = "받는 사람은 필수입니다.")
        @Size(max = 50)
        String receiverName,

        @Schema(description = "연락처", example = "010-1234-5678")
        @NotBlank(message = "연락처는 필수입니다.")
        @Size(max = 20)
        String phone,

        @Schema(description = "주소", example = "서울 동작구 사당로 50")
        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 255)
        String addressLine1,

        @Schema(description = "상세 주소", example = "104동 501호")
        @NotBlank(message = "상세 주소는 필수입니다.")
        @Size(max = 255)
        String addressLine2,

        @Schema(description = "우편번호", example = "07027")
        @Size(max = 10)
        String postalCode,

        @Schema(description = "배송 요청사항", example = "부재 시 경비실에 맡겨주세요")
        @Size(max = 300)
        String requestNote,

        @Schema(description = "기본 배송지 여부", example = "true")
        @NotNull(message = "기본 배송지 여부는 필수입니다.")
        Boolean isDefault
) {
}