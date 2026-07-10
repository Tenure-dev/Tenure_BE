package com.tenure.domain.ootd.repository;

import com.tenure.domain.ootd.entity.Ootd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OotdRepository extends JpaRepository<Ootd, Long> {
}
