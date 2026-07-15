package com.tenure.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 최근 본 유저
@Getter
@AllArgsConstructor
public class RecentUserResponse {
    private Long userId;
    private String username;
    private String profileImageUrl;
}