package com.tenure.domain.search.dto.response;

import com.tenure.domain.notification.dto.response.NotificationResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.search.enums.SearchSortType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchOotdCursorResponse {

    private List<SearchOotdResponse> content;
    private LocalDateTime nextCursorCreatedAt;
    private Integer nextCursorValue;
    private Long nextCursorId;
    private boolean hasNext;
    private Long count;

    public static SearchOotdCursorResponse from(Slice<Ootd> slice, SearchSortType sort, Long count) {
        List<Ootd> ootds = slice.getContent();

        boolean hasNext = slice.hasNext();


        LocalDateTime nextCursorCreatedAt = null;
        Integer nextCursorValue = null;
        Long nextCursorId = null;


        if(hasNext && !ootds.isEmpty()) {
            Ootd ootd = ootds.get(ootds.size() - 1); // 현재 가지고 온 항목의 마지막
            nextCursorId = ootd.getId();
            switch (sort){
                case HEART -> nextCursorValue = ootd.getHeartCount();
                case SAVE  -> nextCursorValue = ootd.getSaveCount();
                case VIEW  -> nextCursorValue = ootd.getViewCount();
                default    -> nextCursorCreatedAt = ootd.getCreatedAt(); // LATEST, RECOMMEND
            }
        }

        List<SearchOotdResponse> content = ootds.stream()
                .map(ootd -> new SearchOotdResponse(ootd.getId(), ootd.getImageUrl()))
                .toList();

        return new SearchOotdCursorResponse(content, nextCursorCreatedAt,
                nextCursorValue, nextCursorId, hasNext, count);
    }

}
