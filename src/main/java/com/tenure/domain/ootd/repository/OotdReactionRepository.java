package com.tenure.domain.ootd.repository;

import com.tenure.domain.ootd.entity.OotdReaction;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OotdReactionRepository extends JpaRepository<OotdReaction, Long> {

    @Query("""
            select reaction.ootd.id
            from OotdReaction reaction
            where reaction.user.id = :userId
              and reaction.ootd.id in :ootdIds
              and reaction.reactionType = :reactionType
            """)
    Set<Long> findReactedOotdIds(
            @Param("userId") Long userId,
            @Param("ootdIds") Collection<Long> ootdIds,
            @Param("reactionType") OotdReactionType reactionType
    );

    boolean existsByUser_IdAndOotd_IdAndReactionType(Long userId, Long ootdId, OotdReactionType reactionType);

    @Modifying(clearAutomatically = true)
    @Query("""
            delete from OotdReaction r
            where r.user.id = :userId
              and r.ootd.id = :ootdId
              and r.reactionType = :reactionType
            """)
    int deleteByUserAndOotdAndReactionType(
            @Param("userId") Long userId,
            @Param("ootdId") Long ootdId,
            @Param("reactionType") OotdReactionType reactionType
    );

    @Query("""
            select reaction
            from OotdReaction reaction
            join fetch reaction.ootd ootd
            join ootd.owner owner
            where reaction.user.id = :userId
              and reaction.reactionType = :reactionType
              and ootd.publicationStatus = :publicationStatus
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :userId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :userId)
                  )
            order by reaction.createdAt desc, reaction.id desc
            """)
    List<OotdReaction> findReactedOotdsFirstPage(
            @Param("userId") Long userId,
            @Param("reactionType") OotdReactionType reactionType,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            Pageable pageable
    );

    @Query("""
            select reaction
            from OotdReaction reaction
            join fetch reaction.ootd ootd
            join ootd.owner owner
            where reaction.user.id = :userId
              and reaction.reactionType = :reactionType
              and ootd.publicationStatus = :publicationStatus
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :userId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :userId)
                  )
              and (
                    :cursorCreatedAt is null
                    or reaction.createdAt < :cursorCreatedAt
                    or (reaction.createdAt = :cursorCreatedAt and reaction.id < :cursorId)
                  )
            order by reaction.createdAt desc, reaction.id desc
            """)
    List<OotdReaction> findReactedOotds(
            @Param("userId") Long userId,
            @Param("reactionType") OotdReactionType reactionType,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
