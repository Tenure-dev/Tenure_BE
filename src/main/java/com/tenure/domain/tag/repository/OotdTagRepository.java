package com.tenure.domain.tag.repository;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OotdTagRepository extends JpaRepository<OotdTag, Long> {

    List<OotdTag> findAllByOotdId(Long ootdId);

    long deleteAllByOotdId(Long ootdId);

    interface FrequentlyWornTogetherItemProjection {
        Long getItemId();
        String getBrandName();
        String getItemName();
        String getRepresentativeImageUrl();
        Long getTogetherCount();
    }

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

    // 기준 아이템과 같은 OOTD에 함께 태그된 아이템을 함께 등장한 횟수 순으로 조회 (자주 같이 입은 옷 조회)
    @Query("""
            select item.id as itemId,
                   item.brandName as brandName,
                   item.itemName as itemName,
                   item.representativeImageUrl as representativeImageUrl,
                   count(distinct ootd.id) as togetherCount
            from OotdTag baseTag, OotdTag togetherTag
            join baseTag.ootd ootd
            join togetherTag.item item
            where baseTag.item.id = :itemId
              and togetherTag.ootd.id = ootd.id
              and togetherTag.item.id <> :itemId
              and ootd.publicationStatus = :publicationStatus
              and baseTag.status = :tagStatus
              and togetherTag.status = :tagStatus
            group by item.id, item.brandName, item.itemName, item.representativeImageUrl
            order by count(distinct ootd.id) desc, item.id asc
            """)
    List<FrequentlyWornTogetherItemProjection> findFrequentlyWornTogetherItems(
            @Param("itemId") Long itemId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus,
            Pageable pageable
    );

    // 종료되지 않은 히스토리: startedAt 이후 OOTD 조회
    @Query("""
        select distinct ootd
        from OotdTag tag
        join tag.ootd ootd
        where tag.item.id = :itemId
          and ootd.owner.id = :ownerUserId
          and tag.status = :tagStatus
          and ootd.publicationStatus = :publicationStatus
          and ootd.createdAt >= :startedAt
        order by ootd.createdAt desc, ootd.id desc
        """)
    Page<Ootd> findCurrentItemHistoryOotds(
            @Param("itemId") Long itemId,
            @Param("ownerUserId") Long ownerUserId,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus,
            Pageable pageable
    );

    // 종료된 히스토리: startedAt ~ endedAt 사이 OOTD 조회
    @Query("""
        select distinct ootd
        from OotdTag tag
        join tag.ootd ootd
        where tag.item.id = :itemId
          and ootd.owner.id = :ownerUserId
          and tag.status = :tagStatus
          and ootd.publicationStatus = :publicationStatus
          and ootd.createdAt >= :startedAt
          and ootd.createdAt <= :endedAt
        order by ootd.createdAt desc, ootd.id desc
        """)
    Page<Ootd> findClosedItemHistoryOotds(
            @Param("itemId") Long itemId,
            @Param("ownerUserId") Long ownerUserId,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("endedAt") LocalDateTime endedAt,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("tagStatus") TagStatus tagStatus,
            Pageable pageable
    );

        // 판매 전환 대표 OOTD 후보 조회
    @Query("""
            select distinct ootd
            from OotdTag tag
            join tag.ootd ootd
            where tag.item.id = :itemId
              and ootd.owner.id = :ownerUserId
              and tag.status = :tagStatus
              and ootd.publicationStatus = :publicationStatus
            order by ootd.createdAt desc, ootd.id desc
            """)
    Page<Ootd> findItemOotdCandidates(
            @Param("itemId") Long itemId,
            @Param("ownerUserId") Long ownerUserId,
            @Param("tagStatus") TagStatus tagStatus,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            Pageable pageable
    );
}
