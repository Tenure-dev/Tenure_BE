package com.tenure.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchUserQueryDto {

    private Long id;
    private String username;
    private String profileImageUrl;
    private Long followerCount;
    private Long ootdCount;

}
