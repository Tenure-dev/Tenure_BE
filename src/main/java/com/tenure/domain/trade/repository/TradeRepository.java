package com.tenure.domain.trade.repository;

import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.DeliveryCarrier;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.enums.TradeStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // ... findAllByBuyer / findAllBySeller / findAllByParticipant 그대로 ...

    // flushAutomatically: 벌크 UPDATE는 trades 테이블만 건드리므로, 같은 트랜잭션에 다른 테이블(products 등)의
    // dirty 엔티티가 있어도 Hibernate auto-flush가 걸리지 않는다. 그 상태로 clearAutomatically가 영속성 컨텍스트를
    // 비우면 flush되지 않은 변경이 조용히 유실되므로, 반드시 flush를 먼저 강제한다.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Trade t
               set t.status = :to,
                   t.deliveryCarrier = :deliveryCarrier,
                   t.customDeliveryCarrierName = :customDeliveryCarrierName,
                   t.trackingNumber = :trackingNumber,
                   t.shippedAt = :shippedAt
             where t.id = :id
               and t.status = :from
            """)
    int updateToShipped(
            @Param("id") Long id,
            @Param("from") TradeStatus from,
            @Param("to") TradeStatus to,
            @Param("deliveryCarrier") DeliveryCarrier deliveryCarrier,
            @Param("customDeliveryCarrierName") String customDeliveryCarrierName,
            @Param("trackingNumber") String trackingNumber,
            @Param("shippedAt") LocalDateTime shippedAt
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Trade t
               set t.status = :to,
                   t.deliveredAt = :deliveredAt
             where t.id = :id
               and t.status = :from
            """)
    int updateToDelivered(
            @Param("id") Long id,
            @Param("from") TradeStatus from,
            @Param("to") TradeStatus to,
            @Param("deliveredAt") LocalDateTime deliveredAt
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Trade t
               set t.status = :to,
                   t.confirmedAt = :confirmedAt
             where t.id = :id
               and t.status = :from
            """)
    int updateToConfirmed(
            @Param("id") Long id,
            @Param("from") TradeStatus from,
            @Param("to") TradeStatus to,
            @Param("confirmedAt") LocalDateTime confirmedAt
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Trade t
               set t.status = :to,
                   t.settledAt = :settledAt
             where t.id = :id
               and t.status = :from
            """)
    int updateToSettled(
            @Param("id") Long id,
            @Param("from") TradeStatus from,
            @Param("to") TradeStatus to,
            @Param("settledAt") LocalDateTime settledAt
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Trade t
               set t.status = :to
             where t.id = :id
               and t.status = :from
            """)
    int updateStatus(
            @Param("id") Long id,
            @Param("from") TradeStatus from,
            @Param("to") TradeStatus to
    );

    @Query("""
            select trade.id
            from Trade trade
            where trade.status = :status
              and trade.deliveredAt <= :threshold
            """)
    List<Long> findIdsByStatusAndDeliveredAtBefore(
            @Param("status") TradeStatus status,
            @Param("threshold") LocalDateTime threshold
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

    boolean existsByItemIdAndStatusNotIn(Long itemId, Collection<TradeStatus> statuses);
}
