package com.tenure.domain.tag.repository;

import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OotdTagRepository extends JpaRepository<OotdTag, Long> {

    @Query("""
            select count(distinct ootd.id)
            from OotdTag tag
            join tag.ootd ootd
            where tag.item.id = :itemId
              and ootd.owner.id = :ownerUserId
              and ootd.id in :ootdIds
              and ootd.publicationStatus = :publicationStatus
              and tag.status = :tagStatus
            """)
    long countValidProductAttachedOotds(
            @Param("itemId") Long itemId,
            @Param("ownerUserId") Long ownerUserId,
            @Param("ootdIds") Collection<Long> ootdIds,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus
    );

    @Query("""
            select distinct tag
            from OotdTag tag
            join fetch tag.item item
            left join fetch item.category category
            left join fetch category.parent
            where tag.ootd.id = :ootdId
              and tag.status = :tagStatus
              and tag.item is not null
            order by tag.id asc
            """)
    List<OotdTag> findConfirmedItemTagsByOotdId(
            @Param("ootdId") Long ootdId,
            @Param("tagStatus") TagStatus tagStatus
    );

    @Query("""
            select distinct ootd
            from OotdTag tag
            join tag.ootd ootd
            join fetch ootd.owner owner
            where tag.item.id = :itemId
              and tag.status = :tagStatus
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
    List<com.tenure.domain.ootd.entity.Ootd> findRelatedOotdsByItemId(
            @Param("itemId") Long itemId,
            @Param("currentUserId") Long currentUserId,
            @Param("excludedOotdId") Long excludedOotdId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus,
            Pageable pageable
    );

    @Query("""
            select distinct ootd
            from OotdTag tag
            join tag.ootd ootd
            join fetch ootd.owner owner
            join tag.item item
            join item.category category
            where category.id in :categoryIds
              and tag.status = :tagStatus
              and ootd.id not in :excludedOotdIds
              and ootd.publicationStatus = :publicationStatus
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId)
                  )
            order by ootd.createdAt desc, ootd.id desc
            """)
    List<com.tenure.domain.ootd.entity.Ootd> findRelatedOotdsByCategoryIds(
            @Param("categoryIds") Collection<Long> categoryIds,
            @Param("currentUserId") Long currentUserId,
            @Param("excludedOotdIds") Collection<Long> excludedOotdIds,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus,
            Pageable pageable
    );
}
