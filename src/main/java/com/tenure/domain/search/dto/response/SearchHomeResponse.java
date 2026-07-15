package com.tenure.domain.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHomeResponse {

    private SearchHomeSimilarOotdCursorResponse similarOotds;
    private SearchHomePopularOotdCursorResponse popularOotds;
    private SearchHomeNewOotdCursorResponse newOotds;
    private SearchHomePopularUserCursorResponse popularUsers;

    public static SearchHomeResponse of(
            SearchHomeSimilarOotdCursorResponse similarOotds,
            SearchHomePopularOotdCursorResponse popularOotds,
            SearchHomeNewOotdCursorResponse newOotds,
            SearchHomePopularUserCursorResponse popularUsers) {
        return new SearchHomeResponse(similarOotds, popularOotds, newOotds, popularUsers);
    }
}
