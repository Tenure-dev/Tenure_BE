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

    public static SearchRecentResponse from(List<RecentUserResponse> users, List<RecentKeywordResponse> keywords) {
        return new SearchRecentResponse(users, keywords);
    }
}
