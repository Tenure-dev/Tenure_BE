package com.tenure.domain.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchRecentResponse {

    private List<RecentKeywordResponse> recentKeywords;
    private List<RecentUserResponse> recentUsers;

    public static SearchRecentResponse from(List<RecentKeywordResponse> keywords, List<RecentUserResponse> users) {
        return new SearchRecentResponse(keywords, users);
    }
}
