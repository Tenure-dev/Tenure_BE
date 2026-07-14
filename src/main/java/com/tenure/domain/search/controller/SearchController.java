package com.tenure.domain.search.controller;

import com.tenure.domain.search.dto.response.SearchRecentResponse;
import com.tenure.domain.search.dto.response.SearchSuggestionResponse;
import com.tenure.domain.search.service.SearchService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Search", description = "검색 API")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "검색 홈 추천 조회", description = "전체 사용자 기준 가장 많이 검색된 키워드 TOP 10을 반환합니다.")
    @GetMapping("/suggestions")
    public BaseResponse<SearchSuggestionResponse> getSuggestions() {
        SearchSuggestionResponse suggestions = searchService.getSuggestions();

        return BaseResponse.success(suggestions);
    }

    @Operation(
            summary = "최근 검색어 및 최근 본 사용자 조회",
            description = "내 최근 검색어 TOP 3과 최근 본 사용자 TOP 4을 반환합니다.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID", example = "1")
            })
    @GetMapping("/recent")
    public BaseResponse<SearchRecentResponse> getSearchRecent() {
        SearchRecentResponse recentSearch = searchService
                .getRecent(currentUserProvider.getCurrentUserId());

        return BaseResponse.success(recentSearch);
    }

    @Operation(
            summary = "최근 검색어 삭제",
            description = "내 최근 검색어를 삭제합니다. 동일 키워드의 검색 기록이 모두 삭제됩니다.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID", example = "1")
            })
    @DeleteMapping("/recent-keywords/{keywordId}")
    public BaseResponse<Void> deleteKeyword(@PathVariable Long keywordId) {

        searchService.deleteRecentKeyword(currentUserProvider.getCurrentUserId(), keywordId);

        return BaseResponse.success(null);
    }
}
