package com.tenure.domain.ootd.repository;

import com.tenure.domain.ootd.entity.OotdReaction;
import com.tenure.domain.ootd.enums.OotdReactionType;
import java.util.Collection;
import java.util.Set;
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
}
