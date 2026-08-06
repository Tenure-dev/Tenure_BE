package com.tenure.domain.product.dto;

import com.tenure.domain.product.enums.ProductReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 신고 요청")
public record ProductReportCreateRequest(

        @Schema(description = "신고 사유", example = "FAKE_PRODUCT")
        @NotNull(message = "신고 사유는 필수입니다.")
        ProductReportReason reasonType,

        @Schema(description = "상세 사유", example = "짝퉁으로 의심됩니다.")
        @Size(max = 500, message = "상세 사유는 500자 이내여야 합니다.")
        String reasonDetail
) {
}
