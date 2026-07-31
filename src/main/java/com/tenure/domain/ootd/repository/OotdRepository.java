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
            "and exists (select 1 from OotdTag ot2 where ot2.ootd.id = o.id " +
            "    and ot2.item is not null and ot2.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "    and ot2.item.itemStatus = com.tenure.domain.item.enums.ItemStatus.ON_SALE) " +
            "and not exists (select 1 from OotdTag ot2 where ot2.ootd.id = o.id " +
            "    and ot2.item is not null and ot2.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "    and ot2.item.itemStatus <> com.tenure.domain.item.enums.ItemStatus.ON_SALE) ")
    Long searchOotdsTotalCountOnSaleOnly(
            @Param("keyword") String keyword,
            @Param("gender") UserGender gender,
            @Param("heightMin") Integer heightMin,
            @Param("heightMax") Integer heightMax,
            @Param("weightMin") Integer weightMin,
            @Param("weightMax") Integer weightMax,
            @Param("categoryIds") List<Long> categoryIds
    );


    //모든 태그된 아이템이 "판매중"인 ootd 필터링
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
            "and exists (select 1 from OotdTag ot2 where ot2.ootd.id = o.id " +
            "    and ot2.item is not null and ot2.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "    and ot2.item.itemStatus = com.tenure.domain.item.enums.ItemStatus.ON_SALE) " +
            "and not exists (select 1 from OotdTag ot2 where ot2.ootd.id = o.id " +
            "    and ot2.item is not null and ot2.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "    and ot2.item.itemStatus <> com.tenure.domain.item.enums.ItemStatus.ON_SALE) " +
            "and (o.createdAt < :cursor or (o.createdAt = :cursor and o.id < :cursorId)) " +
            "order by o.createdAt desc, o.id desc ")
    Slice<Ootd> searchOotdsByLatestOnSaleOnly(
            @Param("keyword") String keyword,
            @Param("gender") UserGender gender,
            @Param("heightMin") Integer heightMin,
            @Param("heightMax") Integer heightMax,
            @Param("weightMin") Integer weightMin,
            @Param("weightMax") Integer weightMax,
            @Param("categoryIds") List<Long> categoryIds,
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
            "and exists (select 1 from OotdTag ot2 where ot2.ootd.id = o.id " +
            "    and ot2.item is not null and ot2.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "    and ot2.item.itemStatus = com.tenure.domain.item.enums.ItemStatus.ON_SALE) " +
            "and not exists (select 1 from OotdTag ot2 where ot2.ootd.id = o.id " +
            "    and ot2.item is not null and ot2.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " +
            "    and ot2.item.itemStatus <> com.tenure.domain.item.enums.ItemStatus.ON_SALE) " +
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
    Slice<Ootd> searchOotdsByCountOnSaleOnly(
            @Param("keyword") String keyword,
            @Param("gender") UserGender gender,
            @Param("heightMin") Integer heightMin,
            @Param("heightMax") Integer heightMax,
            @Param("weightMin") Integer weightMin,
            @Param("weightMax") Integer weightMax,
            @Param("categoryIds") List<Long> categoryIds,
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
            order by ootd.createdAt desc, ootd.id desc
            """)
    List<Ootd> findFeedFirstPage(
            @Param("currentUserId") Long currentUserId,
            @Param("followingOnly") boolean followingOnly,
            @Param("followingUserId") Long followingUserId,
            @Param("publicationStatus") OotdPublicationStatus publicationStatus,
            @Param("followStatus") FollowStatus followStatus,
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
                    ootd.createdAt < :cursorCreatedAt
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
              and ootd.publicationStatus <> com.tenure.domain.ootd.enums.OotdPublicationStatus.DELETED
            order by ootd.createdAt desc, ootd.id desc
            """)
    List<Ootd> findMyPostsFirstPage(
            @Param("ownerUserId") Long ownerUserId,
            Pageable pageable
    );

    @Query("""
            select ootd
            from Ootd ootd
            where ootd.owner.id = :ownerUserId
              and ootd.publicationStatus <> com.tenure.domain.ootd.enums.OotdPublicationStatus.DELETED
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

    // 추천순 — 검색어 있는 경우 (matchScore + hotScore 정렬)
    @Query(value = """
            SELECT sub.id AS id, sub.matchScore AS matchScore, sub.hotScore AS hotScore
            FROM (
                SELECT
                    o.id,
                    COALESCE(MAX(GREATEST(
                        CASE WHEN LOWER(i.item_name) = LOWER(:keyword) THEN 115
                             WHEN LOWER(i.item_name) LIKE LOWER(:keyword) || '%' THEN 85
                             WHEN LOWER(i.item_name) LIKE '%' || LOWER(:keyword) || '%' THEN 55
                             ELSE 0 END,
                        CASE WHEN LOWER(i.brand_name) = LOWER(:keyword) THEN 110
                             WHEN LOWER(i.brand_name) LIKE LOWER(:keyword) || '%' THEN 80
                             WHEN LOWER(i.brand_name) LIKE '%' || LOWER(:keyword) || '%' THEN 50
                             ELSE 0 END,
                        CASE WHEN c.name IS NOT NULL AND LOWER(c.name) = LOWER(:keyword) THEN 105
                             WHEN c.name IS NOT NULL AND LOWER(c.name) LIKE LOWER(:keyword) || '%' THEN 75
                             WHEN c.name IS NOT NULL AND LOWER(c.name) LIKE '%' || LOWER(:keyword) || '%' THEN 45
                             ELSE 0 END,
                        CASE WHEN pc.name IS NOT NULL AND LOWER(pc.name) = LOWER(:keyword) THEN 100
                             WHEN pc.name IS NOT NULL AND LOWER(pc.name) LIKE LOWER(:keyword) || '%' THEN 70
                             WHEN pc.name IS NOT NULL AND LOWER(pc.name) LIKE '%' || LOWER(:keyword) || '%' THEN 40
                             ELSE 0 END
                    )), 0) AS matchScore,
                    (COALESCE(o.save_count, 0) * 3.0 + COALESCE(o.heart_count, 0) * 2.0 + COALESCE(o.view_count, 0) * 0.05 + 1.0)
                    / SQRT(1.0 + EXTRACT(EPOCH FROM (NOW() - o.created_at)) / 86400.0) AS hotScore,
                    o.created_at AS createdAt
                FROM ootds o
                JOIN users u ON u.id = o.owner_user_id
                JOIN ootd_tags ot ON ot.ootd_id = o.id AND ot.item_id IS NOT NULL AND ot.status = 'CONFIRMED'
                JOIN items i ON i.id = ot.item_id
                LEFT JOIN categories c ON c.id = i.category_id
                LEFT JOIN categories pc ON pc.id = c.parent_id
                WHERE o.publication_status = 'ACTIVE'
                  AND (:gender IS NULL OR u.gender = :gender)
                  AND (:heightMin IS NULL OR u.height_cm >= :heightMin)
                  AND (:heightMax IS NULL OR u.height_cm <= :heightMax)
                  AND (:weightMin IS NULL OR u.weight_kg >= :weightMin)
                  AND (:weightMax IS NULL OR u.weight_kg <= :weightMax)
                  AND (:hasCat = false OR i.category_id IN (:catIds) OR c.parent_id IN (:catIds))
                  AND (:itemStatus IS NULL OR EXISTS (
                           SELECT 1 FROM ootd_tags ot2 JOIN items i2 ON i2.id = ot2.item_id
                           WHERE ot2.ootd_id = o.id AND ot2.item_id IS NOT NULL AND ot2.status = 'CONFIRMED'
                           AND i2.item_status = :itemStatus
                      ))
                  AND (:onSaleOnly = false OR (
                           EXISTS (SELECT 1 FROM ootd_tags ot2 JOIN items i2 ON i2.id = ot2.item_id
                                   WHERE ot2.ootd_id = o.id AND ot2.item_id IS NOT NULL AND ot2.status = 'CONFIRMED'
                                   AND i2.item_status = 'ON_SALE')
                           AND NOT EXISTS (SELECT 1 FROM ootd_tags ot2 JOIN items i2 ON i2.id = ot2.item_id
                                           WHERE ot2.ootd_id = o.id AND ot2.item_id IS NOT NULL AND ot2.status = 'CONFIRMED'
                                           AND i2.item_status <> 'ON_SALE')
                      ))
                GROUP BY o.id, o.save_count, o.heart_count, o.view_count, o.created_at
            ) sub
            WHERE sub.matchScore > 0
              AND (:cursorMatchScore IS NULL
                   OR sub.matchScore < :cursorMatchScore
                   OR (sub.matchScore = :cursorMatchScore AND sub.hotScore < :cursorHotScore)
                   OR (sub.matchScore = :cursorMatchScore AND sub.hotScore = :cursorHotScore AND sub.createdAt < :cursor)
                   OR (sub.matchScore = :cursorMatchScore AND sub.hotScore = :cursorHotScore AND sub.createdAt = :cursor AND sub.id < :cursorId))
            ORDER BY sub.matchScore DESC, sub.hotScore DESC, sub.createdAt DESC, sub.id DESC
            LIMIT :size
            """, nativeQuery = true)
    List<OotdRecommendProjection> searchOotdsByRecommendWithKeyword(
            @Param("keyword") String keyword,
            @Param("gender") String gender,
            @Param("heightMin") Integer heightMin,
            @Param("heightMax") Integer heightMax,
            @Param("weightMin") Integer weightMin,
            @Param("weightMax") Integer weightMax,
            @Param("hasCat") boolean hasCat,
            @Param("catIds") List<Long> catIds,
            @Param("itemStatus") String itemStatus,
            @Param("onSaleOnly") boolean onSaleOnly,
            @Param("cursorMatchScore") Double cursorMatchScore,
            @Param("cursorHotScore") Double cursorHotScore,
            @Param("cursor") LocalDateTime cursor,
            @Param("cursorId") Long cursorId,
            @Param("size") int size
    );

    // 추천순 — 필터만 있는 경우 (hotScore 정렬, matchScore 계산 없음)
    @Query(value = """
            SELECT sub.id AS id, CAST(0 AS DOUBLE PRECISION) AS matchScore, sub.hotScore AS hotScore
            FROM (
                SELECT
                    o.id,
                    (COALESCE(o.save_count, 0) * 3.0 + COALESCE(o.heart_count, 0) * 2.0 + COALESCE(o.view_count, 0) * 0.05 + 1.0)
                    / SQRT(1.0 + EXTRACT(EPOCH FROM (NOW() - o.created_at)) / 86400.0) AS hotScore,
                    o.created_at AS createdAt
                FROM ootds o
                JOIN users u ON u.id = o.owner_user_id
                WHERE o.publication_status = 'ACTIVE'
                  AND EXISTS (SELECT 1 FROM ootd_tags ot WHERE ot.ootd_id = o.id AND ot.item_id IS NOT NULL AND ot.status = 'CONFIRMED')
                  AND (:gender IS NULL OR u.gender = :gender)
                  AND (:heightMin IS NULL OR u.height_cm >= :heightMin)
                  AND (:heightMax IS NULL OR u.height_cm <= :heightMax)
                  AND (:weightMin IS NULL OR u.weight_kg >= :weightMin)
                  AND (:weightMax IS NULL OR u.weight_kg <= :weightMax)
                  AND (:hasCat = false OR EXISTS (
                           SELECT 1 FROM ootd_tags ot JOIN items i ON i.id = ot.item_id
                           LEFT JOIN categories c ON c.id = i.category_id
                           WHERE ot.ootd_id = o.id AND ot.item_id IS NOT NULL AND ot.status = 'CONFIRMED'
                           AND (i.category_id IN (:catIds) OR c.parent_id IN (:catIds))
                      ))
                  AND (:itemStatus IS NULL OR EXISTS (
                           SELECT 1 FROM ootd_tags ot2 JOIN items i2 ON i2.id = ot2.item_id
                           WHERE ot2.ootd_id = o.id AND ot2.item_id IS NOT NULL AND ot2.status = 'CONFIRMED'
                           AND i2.item_status = :itemStatus
                      ))
                  AND (:onSaleOnly = false OR (
                           EXISTS (SELECT 1 FROM ootd_tags ot2 JOIN items i2 ON i2.id = ot2.item_id
                                   WHERE ot2.ootd_id = o.id AND ot2.item_id IS NOT NULL AND ot2.status = 'CONFIRMED'
                                   AND i2.item_status = 'ON_SALE')
                           AND NOT EXISTS (SELECT 1 FROM ootd_tags ot2 JOIN items i2 ON i2.id = ot2.item_id
                                           WHERE ot2.ootd_id = o.id AND ot2.item_id IS NOT NULL AND ot2.status = 'CONFIRMED'
                                           AND i2.item_status <> 'ON_SALE')
                      ))
            ) sub
            WHERE (:cursorHotScore IS NULL
                   OR sub.hotScore < :cursorHotScore
                   OR (sub.hotScore = :cursorHotScore AND sub.createdAt < :cursor)
                   OR (sub.hotScore = :cursorHotScore AND sub.createdAt = :cursor AND sub.id < :cursorId))
            ORDER BY sub.hotScore DESC, sub.createdAt DESC, sub.id DESC
            LIMIT :size
            """, nativeQuery = true)
    List<OotdRecommendProjection> searchOotdsByRecommendFilterOnly(
            @Param("gender") String gender,
            @Param("heightMin") Integer heightMin,
            @Param("heightMax") Integer heightMax,
            @Param("weightMin") Integer weightMin,
            @Param("weightMax") Integer weightMax,
            @Param("hasCat") boolean hasCat,
            @Param("catIds") List<Long> catIds,
            @Param("itemStatus") String itemStatus,
            @Param("onSaleOnly") boolean onSaleOnly,
            @Param("cursorHotScore") Double cursorHotScore,
            @Param("cursor") LocalDateTime cursor,
            @Param("cursorId") Long cursorId,
            @Param("size") int size
    );
}
