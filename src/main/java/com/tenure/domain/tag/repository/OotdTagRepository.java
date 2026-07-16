package com.tenure.domain.tag.repository;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OotdTagRepository extends JpaRepository<OotdTag, Long> {

    List<OotdTag> findAllByOotdId(Long ootdId);

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
            select count(tag) > 0
            from OotdTag tag
            join tag.ootd ootd
            where tag.item.id = :itemId
              and ootd.publicationStatus = :publicationStatus
              and tag.status = :tagStatus
            """)
    boolean existsVisibleTagByItemId(
            @Param("itemId") Long itemId,
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

    // 검색 홈 유사 OOTD — 우선순위 1: 같은 아이템
    @Query("""
            select distinct ootd
            from OotdTag tag
            join tag.ootd ootd
            join fetch ootd.owner owner
            where tag.item.id in :itemIds
              and tag.status = :tagStatus
              and ootd.publicationStatus = :publicationStatus
              and ootd.id <> :sourceOotdId
              and ootd.id < :cursorId
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId)
                  )
            order by ootd.id desc
            """)
    Slice<Ootd> findSimilarOotdsByItemIds(
            @Param("itemIds") Collection<Long> itemIds,
            @Param("sourceOotdId") Long sourceOotdId,
            @Param("cursorId") Long cursorId,
            @Param("currentUserId") Long currentUserId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus,
            Pageable pageable
    );

    // 검색 홈 유사 OOTD — 우선순위 2: 같은 상세 카테고리
    @Query("""
            select distinct ootd
            from OotdTag tag
            join tag.ootd ootd
            join fetch ootd.owner owner
            join tag.item item
            join item.category category
            where category.id in :categoryIds
              and tag.status = :tagStatus
              and ootd.publicationStatus = :publicationStatus
              and ootd.id not in :excludedOotdIds
              and ootd.id < :cursorId
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId)
                  )
            order by ootd.id desc
            """)
    Slice<Ootd> findSimilarOotdsByCategoryIds(
            @Param("categoryIds") Collection<Long> categoryIds,
            @Param("excludedOotdIds") Collection<Long> excludedOotdIds,
            @Param("cursorId") Long cursorId,
            @Param("currentUserId") Long currentUserId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus,
            Pageable pageable
    );

    // 검색 홈 유사 OOTD — 우선순위 3: 같은 상위 카테고리
    @Query("""
            select distinct ootd
            from OotdTag tag
            join tag.ootd ootd
            join fetch ootd.owner owner
            join tag.item item
            join item.category category
            join category.parent parentCategory
            where parentCategory.id in :parentCategoryIds
              and tag.status = :tagStatus
              and ootd.publicationStatus = :publicationStatus
              and ootd.id not in :excludedOotdIds
              and ootd.id < :cursorId
              and not exists (
                    select 1
                    from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId)
                  )
            order by ootd.id desc
            """)
    Slice<Ootd> findSimilarOotdsByParentCategoryIds(
            @Param("parentCategoryIds") Collection<Long> parentCategoryIds,
            @Param("excludedOotdIds") Collection<Long> excludedOotdIds,
            @Param("cursorId") Long cursorId,
            @Param("currentUserId") Long currentUserId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus,
            Pageable pageable
    );
}
