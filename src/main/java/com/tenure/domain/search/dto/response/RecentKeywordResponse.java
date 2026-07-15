package com.tenure.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 최근 검색어 아이템
@Getter
@AllArgsConstructor
public class RecentKeywordResponse {
    private Long id; // 삭제용 id
    private String keyword;
}