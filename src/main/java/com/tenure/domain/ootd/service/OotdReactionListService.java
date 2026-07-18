package com.tenure.domain.ootd.service;

import com.tenure.domain.ootd.dto.OotdReactedItemResponse;
import com.tenure.domain.ootd.dto.OotdReactionListResponse;
import com.tenure.domain.ootd.entity.OotdReaction;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OotdReactionListService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final OotdReactionRepository ootdReactionRepository;

    @Transactional(readOnly = true)
    public OotdReactionListResponse getHeartedOotds(
            Long currentUserId,
            LocalDateTime cursorCreatedAt,
            Long cursorId,
            Integer size
    ) {
        return getReactedOotds(currentUserId, OotdReactionType.HEART, cursorCreatedAt, cursorId, size);
    }

    private OotdReactionListResponse getReactedOotds(
            Long currentUserId,
            OotdReactionType reactionType,
            LocalDateTime cursorCreatedAt,
            Long cursorId,
            Integer size
    ) {
        validateCursor(cursorCreatedAt, cursorId);
        int resolvedSize = resolveSize(size);

        List<OotdReaction> reactions = ootdReactionRepository.findReactedOotds(
                currentUserId,
                reactionType,
                OotdPublicationStatus.ACTIVE,
                cursorCreatedAt,
                cursorId,
                PageRequest.of(0, resolvedSize + 1)
        );

        boolean hasNext = reactions.size() > resolvedSize;
        List<OotdReaction> pageItems = hasNext ? reactions.subList(0, resolvedSize) : reactions;
        List<OotdReactedItemResponse> content = pageItems.stream()
                .map(OotdReactedItemResponse::from)
                .toList();

        OotdReaction nextCursorSource = hasNext ? pageItems.get(pageItems.size() - 1) : null;
        return new OotdReactionListResponse(
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
}
