package com.tenure.domain.search.controller;

import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.search.dto.response.SearchOotdCursorResponse;
import com.tenure.domain.search.dto.response.SearchRecentResponse;
import com.tenure.domain.search.dto.response.SearchSuggestionResponse;
import com.tenure.domain.search.enums.SearchSortType;
import com.tenure.domain.search.service.SearchService;
import com.tenure.domain.user.enums.UserGender;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
    public BaseResponse<Void> deleteRecentKeyword(@PathVariable Long keywordId) {

        searchService.deleteRecentKeyword(currentUserProvider.getCurrentUserId(), keywordId);

        return BaseResponse.success(null);
    }

    @Operation(
            summary = "최근 본 사용자 삭제",
            description = "최근 본 사용자 목록에서 특정 사용자를 삭제합니다.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID", example = "1")
            })
    @DeleteMapping("/recent-users/{userId}")
    public BaseResponse<Void> deleteRecentUser(@PathVariable Long userId) {

        searchService.deleteRecentUser(currentUserProvider.getCurrentUserId(), userId);

        return BaseResponse.success(null);

    }

    @Operation(
            summary = "OOTD 검색",
            description = "키워드, 필터, 정렬 조건으로 공개된 OOTD를 검색합니다. " +
                    "keyword와 categoryIds 모두 생략 시 조건에 맞는 전체 OOTD를 반환합니다. " +
                    "정렬 기본값은 LATEST(최신순)이며 HEART(좋아요순), SAVE(저장순), VIEW(조회수순), RECOMMEND(추천순) 선택 가능합니다.",
            parameters = {
                    @Parameter(name = "X-USER-ID", in = ParameterIn.HEADER, required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID", example = "1")
            })
    @GetMapping("/ootds")
    public BaseResponse<SearchOotdCursorResponse> searchOotd(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserGender gender,
            @RequestParam(required = false) Integer heightMin,
            @RequestParam(required = false) Integer heightMax,
            @RequestParam(required = false) Integer weightMin,
            @RequestParam(required = false) Integer weightMax,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) ItemStatus itemStatus,
            @RequestParam(defaultValue = "LATEST") SearchSortType sort, //기본 값: '최신순'
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer cursorValue,
            @RequestParam(defaultValue = "20") int size)
    {
        log.info("[OOTD 검색 api 호출] keyword = {}, sort = {}", keyword, sort);

        SearchOotdCursorResponse searchOotdCursorResponse = searchService.
                searchOotds(keyword, gender, heightMin, heightMax,
                        weightMin, weightMax, categoryIds, itemStatus, sort,
                        cursor, cursorId, cursorValue, size);

        return BaseResponse.success(searchOotdCursorResponse);
    }
}
