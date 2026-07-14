package com.tenure.domain.ootd.repository;

import com.tenure.domain.ootd.entity.Ootd;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OotdRepository extends JpaRepository<Ootd, Long> {

    @Query("""
            select ootd
            from Ootd ootd
            where ootd.owner.id = :ownerUserId
              and (
                    :cursorCreatedAt is null
                    or ootd.createdAt < :cursorCreatedAt
                    or (ootd.createdAt = :cursorCreatedAt and ootd.id < :cursorId)
                  )
            order by ootd.createdAt desc, ootd.id desc
            """)
    List<Ootd> findMyPosts(
            @Param("ownerUserId") Long ownerUserId,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
