package com.tenure.domain.follow.repository;

import com.tenure.domain.follow.entity.FollowRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {
}
