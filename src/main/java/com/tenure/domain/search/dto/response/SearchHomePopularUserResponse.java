package com.tenure.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchHomePopularUserResponse {

    private Long id;
    private String username;
    private String profileImageUrl;

    public static SearchHomePopularUserResponse from(SearchUserQueryDto dto) {
        return new SearchHomePopularUserResponse(
                dto.getId(), dto.getUsername(), dto.getProfileImageUrl());
    }
}
