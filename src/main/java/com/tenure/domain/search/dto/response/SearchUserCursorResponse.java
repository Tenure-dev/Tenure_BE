package com.tenure.domain.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchUserCursorResponse {

    private List<SearchUserResponse> content;
    private Long nextCursorId;
    private boolean hasNext;

    public static SearchUserCursorResponse empty() {
        return new SearchUserCursorResponse(List.of(), null, false);
    }

    public static SearchUserCursorResponse from(Slice<SearchUserQueryDto> slice, Set<Long> followingIds) {

        List<SearchUserQueryDto> searchUsers = slice.getContent();
        boolean hasNext = slice.hasNext();

        Long nextCursorId = null;

        if(hasNext && !searchUsers.isEmpty()) {
            SearchUserQueryDto lastSearchUser = searchUsers.get(searchUsers.size() - 1);
            nextCursorId = lastSearchUser.getId();
        }

        List<SearchUserResponse> content = searchUsers.stream()
                .map(searchUser -> SearchUserResponse.from(searchUser, followingIds))
                .toList();

        return new SearchUserCursorResponse(content, nextCursorId, hasNext);
    }
}

