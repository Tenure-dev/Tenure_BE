package com.tenure.domain.user.dto.response;

import com.tenure.domain.common.enums.ReportStatus;
import com.tenure.domain.user.entity.UserReport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "사용자 신고 응답")
public record UserReportCreateResponse(

        @Schema(description = "신고 ID", example = "1")
        Long reportId,

        @Schema(description = "신고 대상 유저 ID", example = "2")
        Long reportedUserId,

        @Schema(description = "처리 상태", example = "RECEIVED")
        ReportStatus status,

        @Schema(description = "신고 접수 시각", example = "2026-07-30T12:00:00")
        LocalDateTime createdAt
) {

    public static UserReportCreateResponse from(UserReport report) {
        return new UserReportCreateResponse(
                report.getId(),
                report.getReported().getId(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }
}
