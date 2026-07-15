package com.tenure.domain.ootd.repository;

import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.user.enums.UserGender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OotdRepository extends JpaRepository<Ootd, Long> {


    //검색 조건에 맞는 게시물의 총 수(totalCount)
    @Query("select count(o) from Ootd o " +
            "where o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE " + // 공개 된 ootd만
            "and (:gender is null or o.owner.gender =:gender) " +
            "and (:heightMin is null or (o.owner.heightCm >= :heightMin)) " +
            "and (:heightMax is null or (o.owner.heightCm <= :heightMax)) " +
            "and (:weightMin is null or (o.owner.weightKg >= :weightMin)) " +
            "and (:weightMax is null or (o.owner.weightKg <= :weightMax)) " +
            "and o.id in (select ot.ootd.id from OotdTag ot " +
            "where ot.item is not null " +
            "and ot.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " + //확정된 태그만
            "and (:keyword is null or (lower(ot.item.itemName) like lower(concat('%', :keyword, '%')) " + // 제품명에서 검색
            "or lower(ot.item.brandName) like lower(concat('%', :keyword, '%')))) " + // 브랜드에서 검색
            "and (:categoryIds is null or ot.item.category.id in :categoryIds or ot.item.category.parent.id in :categoryIds)) " + //카테고리 조건 1차 카테고리 혹은 2차 카테고리 포함 여부 판단
            "and (:itemStatus is null or o.id in (select ot2.ootd.id from OotdTag ot2 where ot2.item.itemStatus = :itemStatus)) ") // 아이템 상태)
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


    //ootd 최신순 검색(공개된 ootd, 카테고리조건, 제품 명 or 브랜드 명에서 키워드 검색)
    @Query("select o from Ootd o " +
            "where o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE " + // 공개 된 ootd만
            "and (:gender is null or o.owner.gender =:gender) " +
            "and (:heightMin is null or (o.owner.heightCm >= :heightMin)) " +
            "and (:heightMax is null or (o.owner.heightCm <= :heightMax)) " +
            "and (:weightMin is null or (o.owner.weightKg >= :weightMin)) " +
            "and (:weightMax is null or (o.owner.weightKg <= :weightMax)) " +
            "and o.id in (select ot.ootd.id from OotdTag ot " +
                "where ot.item is not null " +
                "and ot.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " + //확정된 태그만
                "and (:keyword is null or (lower(ot.item.itemName) like lower(concat('%', :keyword, '%')) " + // 제품명에서 검색
                    "or lower(ot.item.brandName) like lower(concat('%', :keyword, '%')))) " + // 브랜드에서 검색
                "and (:categoryIds is null or ot.item.category.id in :categoryIds or ot.item.category.parent.id in :categoryIds)) " + //카테고리 조건 1차 카테고리 혹은 2차 카테고리 포함 여부 판단
            "and (:itemStatus is null or o.id in (select ot2.ootd.id from OotdTag ot2 where ot2.item.itemStatus = :itemStatus)) " + // 아이템 상태
            "and (o.createdAt < :cursor or (o.createdAt = :cursor and o.id < :cursorId)) " +
            "order by o.createdAt desc, o.id desc ")  //커서 조건
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

    //ootd 좋아요, 저장, 조회순(공개된 ootd, 카테고리조건, 제품 명 or 브랜드 명에서 키워드 검색)
    @Query("select o from Ootd o " +
            "where o.publicationStatus = com.tenure.domain.ootd.enums.OotdPublicationStatus.ACTIVE " + // 공개 된 ootd만
            "and (:gender is null or o.owner.gender =:gender) " +
            "and (:heightMin is null or (o.owner.heightCm >= :heightMin)) " +
            "and (:heightMax is null or (o.owner.heightCm <= :heightMax)) " +
            "and (:weightMin is null or (o.owner.weightKg >= :weightMin)) " +
            "and (:weightMax is null or (o.owner.weightKg <= :weightMax)) " +
            "and o.id in (select ot.ootd.id from OotdTag ot " +
            "where ot.item is not null " +
            "and ot.status = com.tenure.domain.tag.enums.TagStatus.CONFIRMED " + //확정된 태그만
            "and (:keyword is null or (lower(ot.item.itemName) like lower(concat('%', :keyword, '%')) " + // 제품명에서 검색
            "or lower(ot.item.brandName) like lower(concat('%', :keyword, '%')))) " + // 브랜드에서 검색
            "and (:categoryIds is null or ot.item.category.id in :categoryIds or ot.item.category.parent.id in :categoryIds)) " + //카테고리 조건 1차 카테고리 혹은 2차 카테고리 포함 여부 판단
            "and (:itemStatus is null or o.id in (select ot2.ootd.id from OotdTag ot2 where ot2.item.itemStatus = :itemStatus)) " + // 아이템 상태
            "and (case :sort" +
            "           when 'HEART' then o.heartCount" +
            "           when 'SAVE' then o.saveCount" +
            "           else o.viewCount end < :cursorValue" +
            "     or (case :sort" +
            "           when 'HEART' then o.heartCount" +
            "           when 'SAVE' then o.saveCount" +
            "           else o.viewCount end = :cursorValue and o.id < :cursorId))" +
            "     order by case :sort" +
            "           when 'HEART' then o.heartCount" +
            "           when 'SAVE' then o.saveCount" +
            "           else o.viewCount end desc, o.id desc"
    )
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
            Pageable pageable);

import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import java.time.LocalDateTime;
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
}
