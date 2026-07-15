package com.tenure.domain.tag.repository;

import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import java.util.Collection;
import java.util.List;
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
}
