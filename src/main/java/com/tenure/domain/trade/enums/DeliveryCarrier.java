package com.tenure.domain.trade.enums;

import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.global.exception.CustomException;
import java.util.regex.Pattern;

public enum DeliveryCarrier {
    CJ_LOGISTICS(Pattern.compile("\\d{10}|\\d{12}")),
    KOREA_POST(Pattern.compile("\\d{13}")),
    GS_POSTBOX(Pattern.compile("\\d{10,12}")),
    CU_POST(Pattern.compile("\\d{10,12}")),
    OTHER(null);

    private final Pattern trackingNumberPattern;

    DeliveryCarrier(Pattern trackingNumberPattern) {
        this.trackingNumberPattern = trackingNumberPattern;
    }

    public void validateTrackingNumber(String trackingNumber) {
        if (trackingNumberPattern == null) {
            return;
        }
        if (!trackingNumberPattern.matcher(trackingNumber).matches()) {
            throw new CustomException(TradeErrorCode.TRADE_INVALID_TRACKING);
        }
    }
}
