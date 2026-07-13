package com.tenure.domain.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchSuggestionResponse {

    private List<String> keywords;

    public static SearchSuggestionResponse from(List<String> keywords) {
        return new SearchSuggestionResponse(keywords);
    }
}
