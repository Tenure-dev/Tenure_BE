package com.tenure.domain.ootd.repository;

import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
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
            join fetch ootd.owner owner
            where ootd.publicationStatus = :publicationStatus
              and (
                    :followingOnly = false
                    or exists (
                        select 1
                        from FollowRelationship follow
                        where follow.follower.id = :currentUserId
                          and follow.following.id = owner.id
                          and follow.status = :followStatus
                    )
                  )
              and (:followingUserId is null or owner.id = :followingUserId)
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId)
                  )
              and (
                    :cursorCreatedAt is null
                    or ootd.createdAt < :cursorCreatedAt
                    or (ootd.createdAt = :cursorCreatedAt and ootd.id < :cursorId)
                  )
            order by ootd.createdAt desc, ootd.id desc
            """)
    List<Ootd> findFeed(
            @Param("currentUserId") Long currentUserId,
            @Param("followingOnly") boolean followingOnly,
            @Param("followingUserId") Long followingUserId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("followStatus") FollowStatus followStatus,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
