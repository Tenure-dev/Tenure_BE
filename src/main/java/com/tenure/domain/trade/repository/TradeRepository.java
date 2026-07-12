package com.tenure.domain.trade.repository;

import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query("""
            select trade
            from Trade trade
            where trade.buyer.id = :userId
              and (:status is null or trade.status = :status)
            """)
    Page<Trade> findAllByBuyer(
            @Param("userId") Long userId,
            @Param("status") TradeStatus status,
            Pageable pageable
    );

    @Query("""
            select trade
            from Trade trade
            where trade.seller.id = :userId
              and (:status is null or trade.status = :status)
            """)
    Page<Trade> findAllBySeller(
            @Param("userId") Long userId,
            @Param("status") TradeStatus status,
            Pageable pageable
    );

    @Query("""
            select trade
            from Trade trade
            where (trade.buyer.id = :userId or trade.seller.id = :userId)
              and (:status is null or trade.status = :status)
            """)
    Page<Trade> findAllByParticipant(
            @Param("userId") Long userId,
            @Param("status") TradeStatus status,
            Pageable pageable
    );

    @Query("""
            select trade
            from Trade trade
            where trade.sourceType = :sourceType
              and trade.sourceId in :sourceIds
            """)
    List<Trade> findAllBySourceTypeAndSourceIdIn(
            @Param("sourceType") TradeSourceType sourceType,
            @Param("sourceIds") Collection<Long> sourceIds
    );

}
