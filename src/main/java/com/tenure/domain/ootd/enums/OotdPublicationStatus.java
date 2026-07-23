package com.tenure.domain.ootd.enums;

public enum OotdPublicationStatus {
    ACTIVE,

    // AI 태그 리뷰 대기 중 임시 비공개. 태그 확정(Ootd.confirmTags())되면 자동으로 ACTIVE로 복귀한다.
    ARCHIVED,

    // 작성자가 게시물을 삭제한 상태(soft delete, 복원 기능 없음). ARCHIVED와는 완전히 별개의 상태다.
    DELETED
}
