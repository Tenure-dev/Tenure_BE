package com.tenure.domain.user.repository;

import com.tenure.domain.user.entity.UserBlock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    // 차단 여부 확인
    @Query("SELECT COUNT(*) > 0 " +
            "FROM UserBlock ub " +
            "WHERE ub.blocker.id = :blockerId AND ub.blocked.id = :blockedId")
    boolean isBlocked(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);

    // 차단 관계 조회 (차단 해제 시 사용)
    Optional<UserBlock> findByBlocker_IdAndBlocked_Id(Long blockerId, Long blockedId);

    // 내가 차단한 사용자 목록 조회 (최근 차단순, 탈퇴한 사용자 제외)
    @Query(
            value = "SELECT ub FROM UserBlock ub JOIN FETCH ub.blocked b "
                    + "WHERE ub.blocker.id = :blockerId AND b.deletedAt IS NULL ORDER BY ub.createdAt DESC",
            countQuery = "SELECT COUNT(ub) FROM UserBlock ub JOIN ub.blocked b "
                    + "WHERE ub.blocker.id = :blockerId AND b.deletedAt IS NULL"
    )
    Page<UserBlock> findByBlockerId(@Param("blockerId") Long blockerId, Pageable pageable);
    // 양방향 차단 목록 조회
    @Query("""
            select ub from UserBlock ub
            where (ub.blocker.id = :userId1 and ub.blocked.id = :userId2)
               or (ub.blocker.id = :userId2 and ub.blocked.id = :userId1)
            """)
    List<UserBlock> findBlocksBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
