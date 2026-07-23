package com.tenure.domain.ootd.entity;

import com.tenure.domain.ootd.enums.OotdPublicationStatus;

import com.tenure.domain.ootd.enums.OotdTagStatus;

import com.tenure.domain.ootd.enums.OotdSource;

import com.tenure.domain.common.entity.BaseTimeEntity;
import com.tenure.domain.user.entity.User;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ootds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ootd extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OotdSource source = OotdSource.CAMERA;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_status", nullable = false, length = 30)
    private OotdTagStatus tagStatus = OotdTagStatus.ANALYZING;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_status", nullable = false, length = 20)
    private OotdPublicationStatus publicationStatus = OotdPublicationStatus.ACTIVE;

    @Column(name = "review_required", nullable = false)
    private Boolean reviewRequired = false;

    @Column(name = "review_deadline_at")
    private LocalDateTime reviewDeadlineAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "tag_confirmed_at")
    private LocalDateTime tagConfirmedAt;

    @Column(name = "heart_count", nullable = false)
    private Integer heartCount = 0;

    @Column(name = "save_count", nullable = false)
    private Integer saveCount = 0;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    public static Ootd create(User owner, String imageUrl, OotdSource source) {
        Ootd ootd = new Ootd();
        ootd.owner = owner;
        ootd.imageUrl = imageUrl;
        ootd.source = source;
        return ootd;
    }

    public void confirmTags() {
        this.tagStatus = OotdTagStatus.CONFIRMED;
        this.tagConfirmedAt = LocalDateTime.now();
        this.reviewRequired = false;
        if (this.publicationStatus == OotdPublicationStatus.ARCHIVED) {
            this.publicationStatus = OotdPublicationStatus.ACTIVE;
            this.archivedAt = null;
        }
    }

    // 작성자가 게시물을 삭제할 때 호출한다(soft delete). ARCHIVED(AI 태그 리뷰 임시 비공개)와는
    // 무관한 별개 상태 전이이며, 자식 데이터(태그·반응·최근 조회 등)는 건드리지 않는다.
    public void delete() {
        this.publicationStatus = OotdPublicationStatus.DELETED;
    }
}
