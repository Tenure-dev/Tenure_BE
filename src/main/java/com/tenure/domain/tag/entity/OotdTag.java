package com.tenure.domain.tag.entity;

import com.tenure.domain.tag.enums.TagStatus;

import com.tenure.domain.tag.enums.TagSource;

import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.ootd.entity.Ootd;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ootd_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OotdTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ootd_id", nullable = false)
    private Ootd ootd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "bbox_x", nullable = false, precision = 6, scale = 5)
    private BigDecimal bboxX;

    @Column(name = "bbox_y", nullable = false, precision = 6, scale = 5)
    private BigDecimal bboxY;

    @Column(name = "bbox_width", nullable = false, precision = 6, scale = 5)
    private BigDecimal bboxWidth;

    @Column(name = "bbox_height", nullable = false, precision = 6, scale = 5)
    private BigDecimal bboxHeight;

    @Column(name = "label_text", nullable = false, length = 100)
    private String labelText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TagSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TagStatus status;

    @Column(precision = 5, scale = 4)
    private BigDecimal confidence;

    public static OotdTag createAiTag(
            Ootd ootd,
            String labelText,
            BigDecimal bboxX,
            BigDecimal bboxY,
            BigDecimal bboxWidth,
            BigDecimal bboxHeight,
            BigDecimal confidence
    ) {
        OotdTag tag = new OotdTag();
        tag.ootd = ootd;
        tag.labelText = labelText;
        tag.bboxX = bboxX;
        tag.bboxY = bboxY;
        tag.bboxWidth = bboxWidth;
        tag.bboxHeight = bboxHeight;
        tag.confidence = confidence;
        tag.source = TagSource.AI;
        tag.status = TagStatus.AUTO_UNCONFIRMED;
        return tag;
    }

    public static OotdTag createManualTag(
            Ootd ootd,
            Item item,
            String labelText,
            BigDecimal bboxX,
            BigDecimal bboxY,
            BigDecimal bboxWidth,
            BigDecimal bboxHeight
    ) {
        OotdTag tag = new OotdTag();
        tag.ootd = ootd;
        tag.item = item;
        tag.labelText = labelText;
        tag.bboxX = bboxX;
        tag.bboxY = bboxY;
        tag.bboxWidth = bboxWidth;
        tag.bboxHeight = bboxHeight;
        tag.source = TagSource.MANUAL;
        tag.status = TagStatus.CONFIRMED;
        return tag;
    }

    public void confirm() {
        this.status = TagStatus.CONFIRMED;
    }

    public void updateContent(
            Item item,
            String labelText,
            BigDecimal bboxX,
            BigDecimal bboxY,
            BigDecimal bboxWidth,
            BigDecimal bboxHeight
    ) {
        this.item = item;
        this.labelText = labelText;
        this.bboxX = bboxX;
        this.bboxY = bboxY;
        this.bboxWidth = bboxWidth;
        this.bboxHeight = bboxHeight;
    }
}
