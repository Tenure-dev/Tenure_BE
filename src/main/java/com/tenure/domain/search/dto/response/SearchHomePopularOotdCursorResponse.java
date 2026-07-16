package com.tenure.domain.search.dto.response;

import com.tenure.domain.ootd.entity.Ootd;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHomePopularOotdCursorResponse {

    private List<SearchOotdResponse> content;
    private boolean hasNext;
    private Integer nextCursorValue;   // 하트 순
    private Integer nextCursorSaveValue; // 저장 순
    private Long nextCursorId;

    public static SearchHomePopularOotdCursorResponse from(Slice<Ootd> slice) {
        List<Ootd> ootds = slice.getContent();
        boolean hasNext = slice.hasNext();

        Integer nextCursorValue = null;
        Integer nextCursorSaveValue = null;
        Long nextCursorId = null;

        if (hasNext && !ootds.isEmpty()) {
            Ootd last = ootds.get(ootds.size() - 1);
            nextCursorValue = last.getHeartCount();
            nextCursorSaveValue = last.getSaveCount();
            nextCursorId = last.getId();
        }

        List<SearchOotdResponse> content = ootds.stream()
                .map(SearchOotdResponse::from)
                .toList();

        return new SearchHomePopularOotdCursorResponse(content, hasNext, nextCursorValue, nextCursorSaveValue, nextCursorId);
    }
}
