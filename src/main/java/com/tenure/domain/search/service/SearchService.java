package com.tenure.domain.search.service;

import com.tenure.domain.search.dto.response.RecentKeywordResponse;
import com.tenure.domain.search.dto.response.RecentUserResponse;
import com.tenure.domain.search.dto.response.SearchRecentResponse;
import com.tenure.domain.search.dto.response.SearchSuggestionResponse;
import com.tenure.domain.search.entity.RecentSearchKeyword;
import com.tenure.domain.search.entity.RecentViewUser;
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
            log.warn("[최근 검색어 삭제 api] 삭제 권한이 없습니다. currentUserId = {}", currentUserId);
            throw new CustomException(SearchErrorCode.KEYWORD_FORBIDDEN);
        }

        recentSearchKeywordRepository
                .deleteRecentSearchKeywordByKeyword(currentUserId, recentSearchKeyword.getKeyword());
        log.info("[최근 검색어 삭제 api] keyword 삭제 완료");
    }

    @Transactional
    public void deleteRecentUser(Long currentUserId, Long recentViewedUserId) {

        log.debug("[최근 본 사용자 삭제 api] currentUserId = {}, recentViewedUserId = {}", currentUserId, recentViewedUserId);
        RecentViewUser recentViewUser = recentViewUserRepository.findByViewed_Id(currentUserId, recentViewedUserId)
                .orElseThrow(() -> {
                    log.warn("[최근 본 사용자 삭제 api] 최근 사용자를 찾을 수 업습니다. recentViewedUserId = {}", recentViewedUserId);
                    return new CustomException(SearchErrorCode.RECENT_USER_NOT_FOUND);
                });
        // findByViewed_Id의 실행으로 currentUserId와 recentViewUser.viewer.id가 같음 -> 별도의 권한 검증 필요 X

        log.debug("[최근 본 사용자 삭제 api] 최근 본 사용자 id = {}", recentViewedUserId);

        recentViewUserRepository.deleteRecentViewedUser(currentUserId, recentViewedUserId);
        log.info("[최근 본 사용자 api] 최근 본 사용자 삭제 완료.");

    }
}
