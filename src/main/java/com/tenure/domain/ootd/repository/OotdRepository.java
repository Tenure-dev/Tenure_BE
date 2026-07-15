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

}
