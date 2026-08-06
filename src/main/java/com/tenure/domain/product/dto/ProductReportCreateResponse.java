package com.tenure.domain.product.dto;

import com.tenure.domain.common.enums.ReportStatus;
import com.tenure.domain.product.entity.ProductReport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "상품 신고 응답")
public record ProductReportCreateResponse(

        @Schema(description = "신고 ID", example = "1")
        Long reportId,

        @Schema(description = "신고 대상 상품 ID", example = "10")
        Long productId,

        @Schema(description = "처리 상태", example = "RECEIVED")
        ReportStatus status,

        @Schema(description = "신고 접수 시각", example = "2026-07-30T12:00:00")
        LocalDateTime createdAt
) {

    public static ProductReportCreateResponse from(ProductReport report) {
        return new ProductReportCreateResponse(
                report.getId(),
                report.getProduct().getId(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }
}
