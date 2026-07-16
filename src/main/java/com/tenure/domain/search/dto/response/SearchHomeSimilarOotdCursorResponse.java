package com.tenure.domain.search.dto.response;

import com.tenure.domain.ootd.entity.Ootd;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHomeSimilarOotdCursorResponse {

    private List<SearchOotdResponse> content;
    private boolean hasNext;
    private Integer nextCursorPriority;
    private Long nextCursorId;

    //가장 최근에 본 ootd에 아무런 태그도 달려있지 않은 경우 빈 껍데기 반환
    public static SearchHomeSimilarOotdCursorResponse empty() {
        return new SearchHomeSimilarOotdCursorResponse(List.of(), false, null, null);
    }

    // 워터폴 결과 조합용
    public static SearchHomeSimilarOotdCursorResponse from(
            List<Ootd> ootds, boolean hasNext, int lastPriority) {

        Integer nextCursorPriority = null;
        Long nextCursorId = null;
        if (hasNext && !ootds.isEmpty()) {
            nextCursorPriority = lastPriority;
            nextCursorId = ootds.get(ootds.size() - 1).getId();
        }

        List<SearchOotdResponse> content = ootds.stream()
                .map(SearchOotdResponse::from)
                .toList();

        return new SearchHomeSimilarOotdCursorResponse(content, hasNext, nextCursorPriority, nextCursorId);
    }
}
