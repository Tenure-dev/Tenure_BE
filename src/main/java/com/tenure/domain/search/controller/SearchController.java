package com.tenure.domain.search.controller;

import com.tenure.domain.search.dto.response.SearchSuggestionResponse;
import com.tenure.domain.search.service.SearchService;
import com.tenure.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Search", description = "검색 API")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색 홈 추천 조회", description = "전체 사용자 기준 가장 많이 검색된 키워드 TOP 10을 반환합니다.")
    @GetMapping("/suggestions")
    public BaseResponse<SearchSuggestionResponse> getSuggestions() {
        SearchSuggestionResponse suggestions = searchService.getSuggestions();

        return BaseResponse.success(suggestions);
    }
}
