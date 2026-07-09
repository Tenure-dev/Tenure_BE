package com.tenure.domain.product.repository;

import com.tenure.domain.product.entity.ProductReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReportRepository extends JpaRepository<ProductReport, Long> {
}
