package com.tenure.global.security;

import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private static final String CURRENT_USER_ID_HEADER = "X-USER-ID";

    private final HttpServletRequest request;

    public Long getCurrentUserId() {
        Long securityContextUserId = getFromSecurityContext();
        if (securityContextUserId != null) {
            return securityContextUserId;
        }

        String headerValue = request.getHeader(CURRENT_USER_ID_HEADER);
        if (!StringUtils.hasText(headerValue)) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        try {
            return Long.valueOf(headerValue);
        } catch (NumberFormatException e) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }
    }

    private Long getFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        if (principal instanceof String value && value.chars().allMatch(Character::isDigit)) {
            return Long.valueOf(value);
        }
        return null;
    }
}
