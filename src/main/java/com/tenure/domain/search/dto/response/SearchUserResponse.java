package com.tenure.domain.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchUserResponse {

    private Long id;
    private String username;
    private String profileImageUrl;
    private Long followerCount;
    private Long ootdCount;
    private boolean isFollowing;

    public static SearchUserResponse from(SearchUserQueryDto searchUserQueryDto, Set<Long> followingIds) {

        boolean isFollowing = followingIds.contains(searchUserQueryDto.getId());

        return new SearchUserResponse(
                searchUserQueryDto.getId(), searchUserQueryDto.getUsername(),
                searchUserQueryDto.getProfileImageUrl(), searchUserQueryDto.getFollowerCount(),
                searchUserQueryDto.getOotdCount(), isFollowing);
    }


}
