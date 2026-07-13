package com.tenure.domain.tag.dto;

import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagSource;
import com.tenure.domain.tag.enums.TagStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "OOTD 태그 응답")
public record OotdTagResponse(

        @Schema(description = "태그 ID", example = "1")
        Long tagId,

        @Schema(description = "OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "연결된 아이템 ID", example = "10")
        Long itemId,

        @Schema(description = "bbox X", example = "0.12")
        BigDecimal bboxX,

        @Schema(description = "bbox Y", example = "0.08")
        BigDecimal bboxY,

        @Schema(description = "bbox 너비", example = "0.40")
        BigDecimal bboxWidth,

        @Schema(description = "bbox 높이", example = "0.55")
        BigDecimal bboxHeight,

        @Schema(description = "태그 라벨", example = "블루종 자켓")
        String labelText,

        @Schema(description = "태그 생성 경로", example = "MANUAL")
        TagSource source,

        @Schema(description = "태그 상태", example = "CONFIRMED")
        TagStatus status,

        @Schema(description = "생성 시각")
        LocalDateTime createdAt
) {

    public static OotdTagResponse of(OotdTag tag) {
        return new OotdTagResponse(
                tag.getId(),
                tag.getOotd().getId(),
                tag.getItem() != null ? tag.getItem().getId() : null,
                tag.getBboxX(),
                tag.getBboxY(),
                tag.getBboxWidth(),
                tag.getBboxHeight(),
                tag.getLabelText(),
                tag.getSource(),
                tag.getStatus(),
                tag.getCreatedAt()
        );
    }
}
