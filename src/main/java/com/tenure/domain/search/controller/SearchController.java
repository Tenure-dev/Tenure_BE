package com.tenure.domain.search.controller;

import com.tenure.domain.search.enums.ItemStatusFilter;
import com.tenure.domain.search.dto.response.*;
import com.tenure.domain.search.enums.SearchSortType;
import com.tenure.domain.search.service.SearchService;
import com.tenure.domain.user.enums.UserGender;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

    @Operation(
            summary = "최근 검색어 및 최근 본 사용자 조회",
            description = "내 최근 검색어 TOP 3, 최근 본 사용자 TOP 4, 추천 검색어 TOP 10을 반환합니다."
    )
    @GetMapping("/recent")
    public BaseResponse<SearchRecentResponse> getSearchRecent() {
        SearchRecentResponse recentSearch = searchService
                .getRecent(currentUserProvider.getCurrentUserId());

        return BaseResponse.success(recentSearch);
    }

    @Operation(
            summary = "최근 검색어 삭제",
            description = "내 최근 검색어를 삭제합니다. 동일 키워드의 검색 기록이 모두 삭제됩니다."
    )
    @DeleteMapping("/recent-keywords/{keywordId}")
    public BaseResponse<Void> deleteRecentKeyword(@PathVariable Long keywordId) {
        searchService.deleteRecentKeyword(currentUserProvider.getCurrentUserId(), keywordId);
        return BaseResponse.success(null);
    }

    @Operation(
            summary = "최근 본 사용자 삭제",
            description = "최근 본 사용자 목록에서 특정 사용자를 삭제합니다."
    )
    @DeleteMapping("/recent-users/{userId}")
    public BaseResponse<Void> deleteRecentUser(@PathVariable Long userId) {
        searchService.deleteRecentUser(currentUserProvider.getCurrentUserId(), userId);
        return BaseResponse.success(null);
    }

    @Operation(
            summary = "최근 검색어 전체 삭제",
            description = "내 최근 검색어를 모두 삭제합니다."
    )
    @DeleteMapping("/recent-keywords")
    public BaseResponse<Void> deleteAllRecentKeywords() {
        searchService.deleteAllRecentKeyword(currentUserProvider.getCurrentUserId());
        return BaseResponse.success(null);
    }

    @Operation(
            summary = "최근 본 사용자 전체 삭제",
            description = "최근 본 사용자 목록을 모두 삭제합니다."
    )
    @DeleteMapping("/recent-users")
    public BaseResponse<Void> deleteAllRecentUsers() {
        searchService.deleteAllRecentUser(currentUserProvider.getCurrentUserId());
        return BaseResponse.success(null);
    }

    @Operation(
            summary = "OOTD 검색",
            description = "키워드, 필터, 정렬 조건으로 공개된 OOTD를 검색합니다. " +
                    "keyword 또는 categoryIds 중 하나는 필수입니다. 모두 생략 시 에러를 반환합니다. " +
                    "정렬 기본값은 LATEST(최신순)이며 HEART(좋아요순), SAVE(저장순), VIEW(조회수순), RECOMMEND(추천순) 선택 가능합니다."
    )
    @GetMapping("/ootds")
    public BaseResponse<SearchOotdCursorResponse> searchOotd(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserGender gender,
            @RequestParam(required = false) Integer heightMin,
            @RequestParam(required = false) Integer heightMax,
            @RequestParam(required = false) Integer weightMin,
            @RequestParam(required = false) Integer weightMax,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) ItemStatusFilter itemStatusFilter,
            @RequestParam(defaultValue = "LATEST") SearchSortType sort,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer cursorValue,
            @RequestParam(defaultValue = "20") int size)
    {
        log.info("[OOTD 검색 api 호출] keyword = {}, sort = {}", keyword, sort);

        SearchOotdCursorResponse searchOotdCursorResponse = searchService
                .searchOotds(currentUserProvider.getCurrentUserId(),
                        keyword, gender, heightMin, heightMax,
                        weightMin, weightMax, categoryIds, itemStatusFilter, sort,
                        cursor, cursorId, cursorValue, size);

        return BaseResponse.success(searchOotdCursorResponse);
    }

    @Operation(
            summary = "최근 본 OOTD 저장",
            description = "검색 결과에서 OOTD 클릭 시 최근 본 기록을 저장합니다. 이미 본 OOTD면 lastViewedAt만 갱신합니다."
    )
    @PostMapping("/recent-ootds/{ootdId}")
    public BaseResponse<Void> saveRecentOotd(@PathVariable Long ootdId) {
        searchService.saveRecentOotd(currentUserProvider.getCurrentUserId(), ootdId);
        return BaseResponse.success(null);
    }

    @Operation(
            summary = "최근 본 유저 저장",
            description = "검색 결과에서 유저 클릭 시 최근 본 기록을 저장합니다. 이미 본 유저면 lastViewedAt만 갱신합니다."
    )
    @PostMapping("/recent-users/{userId}")
    public BaseResponse<Void> saveRecentUser(@PathVariable Long userId) {
        searchService.saveRecentUser(currentUserProvider.getCurrentUserId(), userId);
        return BaseResponse.success(null);
    }


    @Operation(
            summary = "유저 검색",
            description = "username으로 사용자를 검색합니다. keyword는 필수이며 prefix 방식으로 검색됩니다."
    )
    @GetMapping("/users")
    public BaseResponse<SearchUserCursorResponse> searchUser(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        SearchUserCursorResponse searchUserCursorResponse = searchService
                .searchUser(currentUserProvider.getCurrentUserId(), keyword, cursorId, size);

        return BaseResponse.success(searchUserCursorResponse);
    }

    @Operation(
            summary = "검색 홈 조회",
            description = "검색 홈 화면의 4개 섹션(유사 OOTD, 인기 스타일, 새 OOTD, 인기 사용자) 데이터를 반환합니다."
    )
    @GetMapping("/home")
    public BaseResponse<SearchHomeResponse> getHome() {
        SearchHomeResponse response = searchService.getHome(currentUserProvider.getCurrentUserId());
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "유사 OOTD 더보기",
            description = "방금 보신 OOTD와 유사한 게시물을 추가로 조회합니다. " +
                    "cursorPriority: 1=같은아이템, 2=같은상세카테고리, 3=같은상위카테고리"
    )
    @GetMapping("/home/similar-ootds")
    public BaseResponse<SearchHomeSimilarOotdCursorResponse> getSimilarOotds(
            @RequestParam(defaultValue = "1") int cursorPriority,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {

        SearchHomeSimilarOotdCursorResponse response = searchService
                .getSimilarOotds(currentUserProvider.getCurrentUserId(), cursorPriority, cursorId, size);
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "인기 스타일 더보기",
            description = "최근 7일 이내 좋아요 수 기준 인기 OOTD를 추가로 조회합니다."
    )
    @GetMapping("/home/popular-ootds")
    public BaseResponse<SearchHomePopularOotdCursorResponse> getPopularOotds(
            @RequestParam(required = false) Integer cursorValue,
            @RequestParam(required = false) Integer cursorSaveValue,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {

        SearchHomePopularOotdCursorResponse response = searchService
                .getPopularOotds(cursorValue, cursorSaveValue, cursorId, currentUserProvider.getCurrentUserId(), size);
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "새로 올라온 OOTD 더보기",
            description = "최신 공개 OOTD를 추가로 조회합니다."
    )
    @GetMapping("/home/new-ootds")
    public BaseResponse<SearchHomeNewOotdCursorResponse> getNewOotds(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {

        SearchHomeNewOotdCursorResponse response = searchService.getNewOotds(cursor, cursorId, currentUserProvider.getCurrentUserId(), size);
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "인기 사용자 더보기",
            description = "팔로워 수 기준 인기 사용자를 추가로 조회합니다."
    )
    @GetMapping("/home/popular-users")
    public BaseResponse<SearchHomePopularUserCursorResponse> getPopularUsers(
            @RequestParam(required = false) Long cursorFollowerCount,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {

        SearchHomePopularUserCursorResponse response = searchService
                .getPopularUsers(cursorFollowerCount, cursorId, currentUserProvider.getCurrentUserId(), size);
        return BaseResponse.success(response);
    }
}
