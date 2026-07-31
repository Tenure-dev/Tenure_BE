package com.tenure.domain.search.dto.response;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.search.enums.SearchSortType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchOotdCursorResponse {

    private List<SearchOotdResponse> content;
    private LocalDateTime nextCursorCreatedAt;
    private Integer nextCursorValue;
    private Long nextCursorId;
    private Double nextCursorHotScore;
    private boolean hasNext;
    private Long count;

    public static SearchOotdCursorResponse from(Slice<Ootd> slice, SearchSortType sort,
                                                        Long count, Set<Long> heartedOotdIds, Set<Long> saveOotdIds) {
        List<Ootd> ootds = slice.getContent();

        boolean hasNext = slice.hasNext();

        LocalDateTime nextCursorCreatedAt = null;
        Integer nextCursorValue = null;
        Long nextCursorId = null;

        if(hasNext && !ootds.isEmpty()) {
            Ootd ootd = ootds.get(ootds.size() - 1);
            nextCursorId = ootd.getId();
            switch (sort){
                case HEART -> nextCursorValue = ootd.getHeartCount();
                case SAVE  -> nextCursorValue = ootd.getSaveCount();
                case VIEW  -> nextCursorValue = ootd.getViewCount();
                default    -> nextCursorCreatedAt = ootd.getCreatedAt(); // LATEST
            }
        }

        List<SearchOotdResponse> content = ootds.stream()
                .map(ootd -> SearchOotdResponse.from(ootd, heartedOotdIds, saveOotdIds))
                .toList();

        return new SearchOotdCursorResponse(content, nextCursorCreatedAt,
                nextCursorValue, nextCursorId, null, hasNext, count);
    }

    // RECOMMEND 전용 팩토리
    public static SearchOotdCursorResponse fromRecommend(
            List<Ootd> ootds, boolean hasNext, Long count,
            Set<Long> heartedOotdIds, Set<Long> saveOotdIds) {

        Double nextCursorHotScore = null;
        Long nextCursorId = null;

        if (hasNext && !ootds.isEmpty()) {
            Ootd last = ootds.get(ootds.size() - 1);
            nextCursorHotScore = last.getHotScore();
            nextCursorId = last.getId();
        }

        List<SearchOotdResponse> content = ootds.stream()
                .map(ootd -> SearchOotdResponse.from(ootd, heartedOotdIds, saveOotdIds))
                .toList();

        return new SearchOotdCursorResponse(
                content, null, null, nextCursorId, nextCursorHotScore, hasNext, count
        );
    }

}
