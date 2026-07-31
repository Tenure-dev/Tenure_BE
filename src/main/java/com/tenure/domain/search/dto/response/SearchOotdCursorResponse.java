package com.tenure.domain.search.dto.response;

import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.repository.OotdRecommendProjection;
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
    private boolean hasNext;
    private Long count;
    private Double nextCursorMatchScore;  // RECOMMEND + 검색어 있을 때
    private Double nextCursorHotScore;    // RECOMMEND일 때

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
                nextCursorValue, nextCursorId, hasNext, count, null, null);
    }

    // RECOMMEND 전용 팩토리
    public static SearchOotdCursorResponse fromRecommend(
            List<Ootd> ootds, List<OotdRecommendProjection> projections,
            boolean hasNext, Long count,
            Set<Long> heartedOotdIds, Set<Long> saveOotdIds) {

        Long nextCursorId = null;
        Double nextCursorMatchScore = null;
        Double nextCursorHotScore = null;

        if (hasNext && !projections.isEmpty()) {
            OotdRecommendProjection last = projections.get(projections.size() - 1);
            nextCursorId = last.getId();
            nextCursorMatchScore = last.getMatchScore();
            nextCursorHotScore = last.getHotScore();
        }

        List<SearchOotdResponse> content = ootds.stream()
                .map(ootd -> SearchOotdResponse.from(ootd, heartedOotdIds, saveOotdIds))
                .toList();

        return new SearchOotdCursorResponse(
                content, null, null, nextCursorId, hasNext, count,
                nextCursorMatchScore, nextCursorHotScore
        );
    }

}
