package com.tenure.domain.wish.repository;

import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.wish.entity.Wish;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    Optional<Wish> findByUserIdAndItemId(Long userId, Long itemId);

    @Query("select w.item.id from Wish w where w.user.id = :userId and w.item.id in :itemIds")
    Set<Long> findWishedItemIds(@Param("userId") Long userId, @Param("itemIds") Collection<Long> itemIds);

    @Modifying
    @Query("""
            delete from Wish wish
            where wish.user.id = :userId
              and wish.item.id = :itemId
            """)
    int deleteByUserIdAndItemId(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId
    );

    @Query(
            value = """
                    select wish
                    from Wish wish
                    join fetch wish.item item
                    where wish.user.id = :userId
                      and (:query is null
                           or item.brandName like concat('%', :query, '%')
                           or item.itemName like concat('%', :query, '%'))
                      and (:saleStatus is null
                           or exists (
                               select 1
                               from Product product
                               where product.item.id = item.id
                                 and product.productStatus = :saleStatus
                           ))
                    order by wish.createdAt desc
                    """,
            countQuery = """
                    select count(wish)
                    from Wish wish
                    join wish.item item
                    where wish.user.id = :userId
                      and (:query is null
                           or item.brandName like concat('%', :query, '%')
                           or item.itemName like concat('%', :query, '%'))
                      and (:saleStatus is null
                           or exists (
                               select 1
                               from Product product
                               where product.item.id = item.id
                                 and product.productStatus = :saleStatus
                           ))
                    """
    )
    Page<Wish> findMyWishes(
            @Param("userId") Long userId,
            @Param("query") String query,
            @Param("saleStatus") ProductStatus saleStatus,
            Pageable pageable
    );

    long countByUser_Id(Long userId);
}
