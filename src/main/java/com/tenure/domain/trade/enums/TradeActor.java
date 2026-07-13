package com.tenure.domain.trade.enums;

public enum TradeActor {
    BUYER,
    SELLER,
    SYSTEM;

    public static TradeActor from(TradeViewerMode viewerMode) {
        return viewerMode == TradeViewerMode.BUYER ? BUYER : SELLER;
    }
}
