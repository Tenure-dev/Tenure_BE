package com.tenure.domain.feed.enums;

import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.util.Locale;

public enum FeedTab {
    ALL,
    FOLLOWING;

    public static FeedTab from(String value) {
        if (value == null || value.isBlank()) {
            return ALL;
        }
        try {
            return FeedTab.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
    }
}
