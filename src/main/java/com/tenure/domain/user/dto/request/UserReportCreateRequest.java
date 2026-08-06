package com.tenure.domain.user.dto.request;

import com.tenure.domain.user.enums.UserReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 신고 요청")
public record UserReportCreateRequest(

        @Schema(description = "신고 사유", example = "ABUSIVE_LANGUAGE")
        @NotNull(message = "신고 사유는 필수입니다.")
        UserReportReason reasonType,

        @Schema(description = "상세 사유", example = "욕설을 지속적으로 보냈습니다.")
        @Size(max = 500, message = "상세 사유는 500자 이내여야 합니다.")
        String reasonDetail
) {
}
