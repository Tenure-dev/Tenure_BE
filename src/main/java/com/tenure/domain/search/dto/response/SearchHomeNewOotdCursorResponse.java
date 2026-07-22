package com.tenure.domain.search.dto.response;

import com.tenure.domain.ootd.entity.Ootd;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHomeNewOotdCursorResponse {

    private List<SearchHomeOotdResponse> content;
    private boolean hasNext;
    private LocalDateTime nextCursor;
    private Long nextCursorId;

    public static SearchHomeNewOotdCursorResponse from(Slice<Ootd> slice) {
        List<Ootd> ootds = slice.getContent();
        boolean hasNext = slice.hasNext();

        LocalDateTime nextCursor = null;
        Long nextCursorId = null;
        if (hasNext && !ootds.isEmpty()) {
            Ootd last = ootds.get(ootds.size() - 1);
            nextCursor = last.getCreatedAt();
            nextCursorId = last.getId();
        }

        List<SearchHomeOotdResponse> content = ootds.stream()
                .map(SearchHomeOotdResponse::from)
                .toList();

        return new SearchHomeNewOotdCursorResponse(content, hasNext, nextCursor, nextCursorId);
    }
}
