package com.tenure.domain.ootd.repository;

import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.user.enums.UserGender;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OotdRepository extends JpaRepository<Ootd, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Ootd o set o.heartCount = o.heartCount + 1 where o.id = :ootdId")
    int increaseHeartCount(@Param("ootdId") Long ootdId);

    @Modifying(clearAutomatically = true)
    @Query("update Ootd o set o.heartCount = o.heartCount - 1 where o.id = :ootdId and o.heartCount > 0")
    int decreaseHeartCount(@Param("ootdId") Long ootdId);

    @Modifying(clearAutomatically = true)
    @Query("update Ootd o set o.saveCount = o.saveCount + 1 where o.id = :ootdId")
    int increaseSaveCount(@Param("ootdId") Long ootdId);

    @Modifying(clearAutomatically = true)
    @Query("update Ootd o set o.saveCount = o.saveCount - 1 where o.id = :ootdId and o.saveCount > 0")
    int decreaseSaveCount(@Param("ootdId") Long ootdId);

    @Query("select count(o) from Ootd o " +
            "where o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE " +
            "and (:gender is null or o.owner.gender = :gender) " +
            "and (:heightMin is null or o.owner.heightCm >= :heightMin) " +
            "and (:heightMax is null or o.owner.heightCm <= :heightMax) " +
            "and (:weightMin is null or o.owner.weightKg >= :weightMin) " +
            "and (:weightMax is null or o.owner.weightKg <= :weightMax) " +
            "and o.id in (select ot.ootd.id from OotdTag ot " +
            "where ot.item is not null " +
            "and ot.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "and (:keyword is null or (lower(ot.item.itemName) like lower(concat('%', :keyword, '%')) " +
            "or lower(ot.item.brandName) like lower(concat('%', :keyword, '%')))) " +
            "and (:categoryIds is null or ot.item.category.id in :categoryIds or ot.item.category.parent.id in :categoryIds)) " +
            "and (:itemStatus is null or o.id in (select ot2.ootd.id from OotdTag ot2 where ot2.item.itemStatus = :itemStatus)) ")
    Long searchOotdsTotalCount(
            @Param("keyword") String keyword,
            @Param("gender") UserGender gender,
            @Param("heightMin") Integer heightMin,
            @Param("heightMax") Integer heightMax,
            @Param("weightMin") Integer weightMin,
            @Param("weightMax") Integer weightMax,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("itemStatus") ItemStatus itemStatus
    );

    @Query("select o from Ootd o " +
            "where o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE " +
            "and (:gender is null or o.owner.gender = :gender) " +
            "and (:heightMin is null or o.owner.heightCm >= :heightMin) " +
            "and (:heightMax is null or o.owner.heightCm <= :heightMax) " +
            "and (:weightMin is null or o.owner.weightKg >= :weightMin) " +
            "and (:weightMax is null or o.owner.weightKg <= :weightMax) " +
            "and o.id in (select ot.ootd.id from OotdTag ot " +
            "where ot.item is not null " +
            "and ot.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "and (:keyword is null or (lower(ot.item.itemName) like lower(concat('%', :keyword, '%')) " +
            "or lower(ot.item.brandName) like lower(concat('%', :keyword, '%')))) " +
            "and (:categoryIds is null or ot.item.category.id in :categoryIds or ot.item.category.parent.id in :categoryIds)) " +
            "and (:itemStatus is null or o.id in (select ot2.ootd.id from OotdTag ot2 where ot2.item.itemStatus = :itemStatus)) " +
            "and (o.createdAt < :cursor or (o.createdAt = :cursor and o.id < :cursorId)) " +
            "order by o.createdAt desc, o.id desc ")
    Slice<Ootd> searchOotdsByLatest(
            @Param("keyword") String keyword,
            @Param("gender") UserGender gender,
            @Param("heightMin") Integer heightMin,
            @Param("heightMax") Integer heightMax,
            @Param("weightMin") Integer weightMin,
            @Param("weightMax") Integer weightMax,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("itemStatus") ItemStatus itemStatus,
            @Param("cursor") LocalDateTime cursor,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("select o from Ootd o " +
            "where o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE " +
            "and (:gender is null or o.owner.gender = :gender) " +
            "and (:heightMin is null or o.owner.heightCm >= :heightMin) " +
            "and (:heightMax is null or o.owner.heightCm <= :heightMax) " +
            "and (:weightMin is null or o.owner.weightKg >= :weightMin) " +
            "and (:weightMax is null or o.owner.weightKg <= :weightMax) " +
            "and o.id in (select ot.ootd.id from OotdTag ot " +
            "where ot.item is not null " +
            "and ot.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "and (:keyword is null or (lower(ot.item.itemName) like lower(concat('%', :keyword, '%')) " +
            "or lower(ot.item.brandName) like lower(concat('%', :keyword, '%')))) " +
            "and (:categoryIds is null or ot.item.category.id in :categoryIds or ot.item.category.parent.id in :categoryIds)) " +
            "and (:itemStatus is null or o.id in (select ot2.ootd.id from OotdTag ot2 where ot2.item.itemStatus = :itemStatus)) " +
            "and (case :sort" +
            "           when 'HEART' then o.heartCount" +
            "           when 'SAVE' then o.saveCount" +
            "           else o.viewCount end < :cursorValue" +
            "     or (case :sort" +
            "           when 'HEART' then o.heartCount" +
            "           when 'SAVE' then o.saveCount" +
            "           else o.viewCount end = :cursorValue and o.id < :cursorId)) " +
            "order by case :sort" +
            "           when 'HEART' then o.heartCount" +
            "           when 'SAVE' then o.saveCount" +
            "           else o.viewCount end desc, o.id desc")
    Slice<Ootd> searchOotdsByCount(
            @Param("keyword") String keyword,
            @Param("gender") UserGender gender,
            @Param("heightMin") Integer heightMin,
            @Param("heightMax") Integer heightMax,
            @Param("weightMin") Integer weightMin,
            @Param("weightMax") Integer weightMax,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("itemStatus") ItemStatus itemStatus,
            @Param("sort") String sort,
            @Param("cursorValue") Integer cursorValue,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

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

    // 검색 홈 — 인기 스타일 (7일 이내, heartCount 내림차순)
    @Query("""
            select o from Ootd o
            join fetch o.owner owner
            where o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE
              and o.createdAt >= :from
              and (o.heartCount < :cursorValue
                   or (o.heartCount = :cursorValue and o.saveCount < :cursorSaveValue)
                   or (o.heartCount = :cursorValue and o.saveCount = :cursorSaveValue and o.id < :cursorId))
              and not exists (
                    select 1 from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId))
            order by o.heartCount desc, o.saveCount desc, o.id desc
            """)
    Slice<Ootd> findPopularOotds(
            @Param("from") LocalDateTime from,
            @Param("cursorValue") Integer cursorValue,
            @Param("cursorSaveValue") Integer cursorSaveValue,
            @Param("cursorId") Long cursorId,
            @Param("currentUserId") Long currentUserId,
            Pageable pageable
    );

    // 검색 홈 — 새로 올라온 OOTD (createdAt 내림차순)
    @Query("""
            select o from Ootd o
            join fetch o.owner owner
            where o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE
              and (o.createdAt < :cursor or (o.createdAt = :cursor and o.id < :cursorId))
              and not exists (
                    select 1 from UserBlock block
                    where (block.blocker.id = :currentUserId and block.blocked.id = owner.id)
                       or (block.blocker.id = owner.id and block.blocked.id = :currentUserId))
            order by o.createdAt desc, o.id desc
            """)
    Slice<Ootd> findNewOotds(
            @Param("cursor") LocalDateTime cursor,
            @Param("cursorId") Long cursorId,
            @Param("currentUserId") Long currentUserId,
            Pageable pageable
    );

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

    long countByOwner_IdAndPublicationStatus(Long ownerUserId, OotdPublicationStatus publicationStatus);
}
