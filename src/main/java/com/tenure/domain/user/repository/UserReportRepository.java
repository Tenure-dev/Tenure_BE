package com.tenure.domain.user.repository;

import com.tenure.domain.user.entity.UserReport;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    // 같은 신고자 + 같은 대상 조합의 기존 신고 조회 (중복 신고 확인용)
    Optional<UserReport> findByReporter_IdAndReported_Id(Long reporterId, Long reportedId);
}
