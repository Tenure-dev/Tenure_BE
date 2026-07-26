package com.tenure.domain.feed.service;

import com.tenure.domain.feed.dto.FeedItemResponse;
import com.tenure.domain.feed.dto.FeedResponse;
import com.tenure.domain.feed.enums.FeedTab;
import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final OotdRepository ootdRepository;
    private final OotdReactionRepository ootdReactionRepository;
    private final FollowRelationshipRepository followRelationshipRepository;

    @Transactional(readOnly = true)
    public FeedResponse getFeed(
            Long currentUserId,
            String tab,
            Long userId,
            LocalDateTime cursorCreatedAt,
            Long cursorId,
            Integer size
    ) {
        FeedTab resolvedTab = FeedTab.from(tab);
        int resolvedSize = resolveSize(size);
        validateCursor(cursorCreatedAt, cursorId);
        validateFollowingFilter(currentUserId, resolvedTab, userId);

        List<Ootd> ootds = ootdRepository.findFeed(
                currentUserId,
                resolvedTab == FeedTab.FOLLOWING,
                userId,
                OotdPublicationStatus.ACTIVE,
                FollowStatus.ACCEPTED,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, resolvedSize + 1)
        );

        boolean hasNext = ootds.size() > resolvedSize;
        List<Ootd> pageItems = hasNext ? ootds.subList(0, resolvedSize) : ootds;
        List<Long> ootdIds = pageItems.stream()
                .map(Ootd::getId)
                .toList();

        Set<Long> heartedOotdIds = findReactedOotdIds(currentUserId, ootdIds, OotdReactionType.HEART);
        Set<Long> savedOotdIds = findReactedOotdIds(currentUserId, ootdIds, OotdReactionType.SAVE);

        List<FeedItemResponse> content = pageItems.stream()
                .map(ootd -> FeedItemResponse.of(ootd, heartedOotdIds, savedOotdIds))
                .toList();

        Ootd nextCursorSource = hasNext ? pageItems.get(pageItems.size() - 1) : null;
        return new FeedResponse(
                content,
                nextCursorSource == null ? null : nextCursorSource.getCreatedAt(),
                nextCursorSource == null ? null : nextCursorSource.getId(),
                hasNext
        );
    }

    private int resolveSize(Integer size) {
        if (size == null) {
            return DEFAULT_SIZE;
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        return size;
    }

    private void validateCursor(LocalDateTime cursorCreatedAt, Long cursorId) {
        if ((cursorCreatedAt == null && cursorId != null) || (cursorCreatedAt != null && cursorId == null)) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
    }

    private void validateFollowingFilter(Long currentUserId, FeedTab tab, Long userId) {
        if (userId == null) {
            return;
        }
        if (tab != FeedTab.FOLLOWING) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        boolean acceptedFollowing = followRelationshipRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                currentUserId,
                userId,
                FollowStatus.ACCEPTED
        );
        if (!acceptedFollowing) {
            throw new CustomException(CommonErrorCode.FORBIDDEN);
        }
    }

    private Set<Long> findReactedOotdIds(Long currentUserId, List<Long> ootdIds, OotdReactionType reactionType) {
        if (ootdIds.isEmpty()) {
            return Set.of();
        }
        return ootdReactionRepository.findReactedOotdIds(currentUserId, ootdIds, reactionType);
    }
}
