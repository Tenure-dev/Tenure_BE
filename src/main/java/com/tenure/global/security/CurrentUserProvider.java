package com.tenure.global.security;

import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private static final String CURRENT_USER_ID_HEADER = "X-USER-ID";

    private final HttpServletRequest request;
    private final Environment environment;

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticatedUser(authentication)) {
            return getFromAuthenticatedPrincipal(authentication);
        }

        return getFromDevelopmentHeader();
    }

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private Long getFromAuthenticatedPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        if (principal instanceof String value && value.chars().allMatch(Character::isDigit)) {
            return Long.valueOf(value);
        }
        throw new CustomException(CommonErrorCode.UNAUTHORIZED);
    }

    private Long getFromDevelopmentHeader() {
        if (isProdProfile()) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
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

    private boolean isProdProfile() {
        return environment.acceptsProfiles(Profiles.of("prod"));
    }
}
