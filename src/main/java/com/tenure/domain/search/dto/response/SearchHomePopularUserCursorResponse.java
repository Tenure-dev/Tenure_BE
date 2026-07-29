package com.tenure.domain.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHomePopularUserCursorResponse {

    private List<SearchUserResponse> content;
    private boolean hasNext;
    private Long nextCursorFollowerCount;
    private Long nextCursorId;

    public static SearchHomePopularUserCursorResponse from(Slice<SearchUserQueryDto> slice, Set<Long> followingIds) {
        List<SearchUserQueryDto> users = slice.getContent();
        boolean hasNext = slice.hasNext();

        Long nextCursorFollowerCount = null;
        Long nextCursorId = null;
        if (hasNext && !users.isEmpty()) {
            SearchUserQueryDto last = users.get(users.size() - 1);
            nextCursorFollowerCount = last.getFollowerCount();
            nextCursorId = last.getId();
        }

        List<SearchUserResponse> content = users.stream()
                .map(u -> SearchUserResponse.from(u, followingIds))
                .toList();

        return new SearchHomePopularUserCursorResponse(content, hasNext, nextCursorFollowerCount, nextCursorId);
    }
}
