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

    @Column(name = "image_object_key", length = 700)
    private String imageObjectKey;

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

    @Column(name = "hot_score", nullable = false)
    private Double hotScore = 0.0;

    public static Ootd create(User owner, String imageUrl, OotdSource source) {
        return create(owner, imageUrl, null, source);
    }

    public static Ootd create(User owner, String imageUrl, String imageObjectKey, OotdSource source) {
        Ootd ootd = new Ootd();
        ootd.owner = owner;
        ootd.imageUrl = imageUrl;
        ootd.imageObjectKey = imageObjectKey;
        ootd.source = source;
        return ootd;
    }

    // 수동 태그 작성 흐름 전용. 태그를 다 작성하기 전까지는 다른 사용자에게 노출되면 안 되므로
    // 임시 비공개(ARCHIVED)로 생성한다. confirmTags()가 호출되어야 ACTIVE로 전환된다.
    public static Ootd createArchived(User owner, String imageUrl, OotdSource source) {
        return createArchived(owner, imageUrl, null, source);
    }

    public static Ootd createArchived(User owner, String imageUrl, String imageObjectKey, OotdSource source) {
        Ootd ootd = create(owner, imageUrl, imageObjectKey, source);
        ootd.publicationStatus = OotdPublicationStatus.ARCHIVED;
        ootd.archivedAt = LocalDateTime.now();
        return ootd;
    }

    // AI 자동분석이 끝났을 때 호출한다. auto-tag 플로우는 사용자가 별도로 확인하는 화면이 없으므로
    // 분석이 끝나면 바로 확정(CONFIRMED) 처리해서, OOTD 상세/관련/검색 등 CONFIRMED 태그만 노출하는
    // 화면에서도 자동 태그가 바로 보이게 한다. 이미 ANALYZING을 벗어난 상태라면 되돌리지 않는다.
    public void markAutoTagsReady() {
        if (this.tagStatus == OotdTagStatus.ANALYZING) {
            this.tagStatus = OotdTagStatus.CONFIRMED;
            this.tagConfirmedAt = LocalDateTime.now();
        }
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
