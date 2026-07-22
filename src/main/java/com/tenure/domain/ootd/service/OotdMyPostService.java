package com.tenure.domain.ootd.service;

import com.tenure.domain.ootd.dto.OotdMyPostItemResponse;
import com.tenure.domain.ootd.dto.OotdMyPostsResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.repository.OotdRepository;
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
public class OotdMyPostService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final OotdRepository ootdRepository;

    @Transactional(readOnly = true)
    public OotdMyPostsResponse getMyPosts(
            Long currentUserId,
            LocalDateTime cursorCreatedAt,
            Long cursorId,
            Integer size
    ) {
        validateCursor(cursorCreatedAt, cursorId);
        int resolvedSize = resolveSize(size);

        PageRequest pageRequest = PageRequest.of(0, resolvedSize + 1);
        List<Ootd> ootds = cursorCreatedAt == null
                ? ootdRepository.findMyPostsFirstPage(currentUserId, pageRequest)
                : ootdRepository.findMyPosts(currentUserId, cursorCreatedAt, cursorId, pageRequest);

        boolean hasNext = ootds.size() > resolvedSize;
        List<Ootd> pageItems = hasNext ? ootds.subList(0, resolvedSize) : ootds;
        List<OotdMyPostItemResponse> content = pageItems.stream()
                .map(OotdMyPostItemResponse::of)
                .toList();

        Ootd nextCursorSource = hasNext ? pageItems.get(pageItems.size() - 1) : null;
        return new OotdMyPostsResponse(
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
