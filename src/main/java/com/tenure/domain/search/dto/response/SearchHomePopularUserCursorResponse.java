package com.tenure.domain.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHomePopularUserCursorResponse {

    private List<SearchHomePopularUserResponse> content;
    private boolean hasNext;
    private Long nextCursorFollowerCount;
    private Long nextCursorId;

    public static SearchHomePopularUserCursorResponse from(Slice<SearchUserQueryDto> slice) {
        List<SearchUserQueryDto> users = slice.getContent();
        boolean hasNext = slice.hasNext();

        Long nextCursorFollowerCount = null;
        Long nextCursorId = null;
        if (hasNext && !users.isEmpty()) {
            SearchUserQueryDto last = users.get(users.size() - 1);
            nextCursorFollowerCount = last.getFollowerCount();
            nextCursorId = last.getId();
        }

        List<SearchHomePopularUserResponse> content = users.stream()
                .map(SearchHomePopularUserResponse::from)
                .toList();

        return new SearchHomePopularUserCursorResponse(content, hasNext, nextCursorFollowerCount, nextCursorId);
    }
}
