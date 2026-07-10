package com.tenure.domain.user.repository;

import com.tenure.domain.user.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
