package com.tenure.domain.ootd.repository;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OotdRepository extends JpaRepository<Ootd, Long> {

    @Query("""
            select ootd
            from Ootd ootd
            join fetch ootd.owner owner
            where ootd.id = :ootdId
              and ootd.publicationStatus = :publicationStatus
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId)
                  )
            """)
    Optional<Ootd> findVisibleActiveById(
            @Param("ootdId") Long ootdId,
            @Param("currentUserId") Long currentUserId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus
    );

    @Query("""
            select ootd
            from Ootd ootd
            join fetch ootd.owner owner
            where ootd.owner.id = :ownerUserId
              and ootd.id <> :excludedOotdId
              and ootd.publicationStatus = :publicationStatus
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId)
                  )
            order by ootd.createdAt desc, ootd.id desc
            """)
    List<Ootd> findRelatedBySameOwner(
            @Param("currentUserId") Long currentUserId,
            @Param("ownerUserId") Long ownerUserId,
            @Param("excludedOotdId") Long excludedOotdId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            Pageable pageable
    );

    @Query("""
            select ootd
            from Ootd ootd
            join fetch ootd.owner owner
            where ootd.publicationStatus = :publicationStatus
              and ootd.id not in :excludedOotdIds
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId)
                  )
            order by ootd.createdAt desc, ootd.id desc
            """)
    List<Ootd> findLatestVisible(
            @Param("currentUserId") Long currentUserId,
            @Param("excludedOotdIds") Collection<Long> excludedOotdIds,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            Pageable pageable
    );
}
