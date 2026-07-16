package com.tenure.domain.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchRecentResponse {

    private List<RecentUserResponse> recentUsers;
    private List<RecentKeywordResponse> recentKeywords;
    private List<String> suggestions;

    public static SearchRecentResponse from(List<RecentUserResponse> users, List<RecentKeywordResponse> keywords, List<String> suggestions) {
        return new SearchRecentResponse(users, keywords, suggestions);
    }
}
