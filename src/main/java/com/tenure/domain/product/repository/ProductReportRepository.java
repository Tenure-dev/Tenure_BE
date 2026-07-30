package com.tenure.domain.product.repository;

import com.tenure.domain.product.entity.ProductReport;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReportRepository extends JpaRepository<ProductReport, Long> {

    // 같은 신고자 + 같은 상품 조합의 기존 신고 조회 (중복 신고 확인용)
    Optional<ProductReport> findByReporter_IdAndProduct_Id(Long reporterId, Long productId);
}
