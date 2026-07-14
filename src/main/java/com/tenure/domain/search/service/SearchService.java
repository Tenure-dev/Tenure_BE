package com.tenure.domain.search.service;

import com.tenure.domain.search.dto.response.RecentKeywordResponse;
import com.tenure.domain.search.dto.response.RecentUserResponse;
import com.tenure.domain.search.dto.response.SearchRecentResponse;
import com.tenure.domain.search.dto.response.SearchSuggestionResponse;
import com.tenure.domain.search.entity.RecentSearchKeyword;
import com.tenure.domain.search.exception.SearchErrorCode;
import com.tenure.domain.search.repository.RecentSearchKeywordRepository;
import com.tenure.domain.search.repository.RecentViewUserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final RecentSearchKeywordRepository recentSearchKeywordRepository;
    private final RecentViewUserRepository recentViewUserRepository;

    //추천 검색어
    public SearchSuggestionResponse getSuggestions() {
        log.info("[추천 검색어 api 호출]");
        List<String> topKeywords = recentSearchKeywordRepository.findTopKeywords(10);

        log.debug("[추천 검색어 api 호출] 조회 {}개", topKeywords.size());

        return SearchSuggestionResponse.from(topKeywords);

    }

    //최근 검색 and 최근 본 사용자 조회
    public SearchRecentResponse getRecent(Long currentUserId) {

        log.info("[최근 검색 / 최근 본 사용자 조회] currentUserId = {}", currentUserId);

        //최근 본 사용자 4건 조회
        List<RecentUserResponse> recentUser = recentViewUserRepository
                .findByRecentUserTop10(currentUserId, 4);

        //최근 검색한 검색어 3개 조회
        List<RecentKeywordResponse> recentKeyword = recentSearchKeywordRepository
                .findByUserTopKeywords(currentUserId, 3);

        //종합 응답 dto 변환
        return SearchRecentResponse.from(recentUser, recentKeyword);

    }

    @Transactional
    public void deleteRecentKeyword(Long currentUserId, Long keywordId) {

        log.info("[최근 검색어 삭제 api] currentUserId = {}", currentUserId);
        RecentSearchKeyword recentSearchKeyword = recentSearchKeywordRepository.findById(keywordId)
                .orElseThrow(() -> {
                    log.warn("[최근 검색어 삭제 api] 검색어를 찾을수 없습니다. keywordId = {}", keywordId);
                    return new CustomException(SearchErrorCode.KEYWORD_NOT_FOUND);
                });
        log.debug("[최근 검색어 삭제 api] keyword = {}", recentSearchKeyword.getKeyword());

        if(!recentSearchKeyword.getUser().getId().equals(currentUserId)) {
            log.warn("[최근 검색어 삭제 api] 본인의 검색 기록만 지울 수 있습니다. currentUserId = {}", currentUserId);
            throw new CustomException(SearchErrorCode.KEYWORD_FORBIDDEN);
        }

        recentSearchKeywordRepository
                .deleteRecentSearchKeywordByKeyword(currentUserId, recentSearchKeyword.getKeyword());
        log.info("[최근 검색어 삭제 api] keyword 삭제 완료");

    }
}
