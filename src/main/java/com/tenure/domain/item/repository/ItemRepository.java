package com.tenure.domain.item.repository;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ItemRepository extends JpaRepository<Item, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select item from Item item where item.id = :itemId")
    Optional<Item> findByIdForUpdate(@Param("itemId") Long itemId);

    @Query("""
            select item
            from Item item
            where item.owner.id = :ownerUserId 
              and (:query = '' or lower(item.brandName) like lower(concat('%', :query, '%'))
                   or lower(item.itemName) like lower(concat('%', :query, '%')))
              and (:itemStatus is null or item.itemStatus = :itemStatus)
            order by item.createdAt desc
            """)
    Page<Item> findMyItems(
            @Param("ownerUserId") Long ownerUserId,
            @Param("query") String query,
            @Param("itemStatus") ItemStatus itemStatus,
            Pageable pageable
    );
}
