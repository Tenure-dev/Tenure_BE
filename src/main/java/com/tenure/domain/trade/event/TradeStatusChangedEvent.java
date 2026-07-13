package com.tenure.domain.trade.event;

import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeStatus;

public record TradeStatusChangedEvent(
        Long tradeId,
        TradeStatus from,
        TradeStatus to,
        Long buyerUserId,
        Long sellerUserId
) {

    public static TradeStatusChangedEvent of(Trade trade, TradeStatus from, TradeStatus to) {
        return new TradeStatusChangedEvent(
                trade.getId(),
                from,
                to,
                trade.getBuyer().getId(),
                trade.getSeller().getId()
        );
    }
}
