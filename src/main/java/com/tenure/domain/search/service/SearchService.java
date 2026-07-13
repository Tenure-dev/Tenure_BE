package com.tenure.domain.search.service;

import com.tenure.domain.search.dto.response.SearchSuggestionResponse;
import com.tenure.domain.search.repository.RecentSearchKeywordRepository;
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

    //추천 검색어
    public SearchSuggestionResponse getSuggestions() {
        log.info("[추천 검색어 api 호출]");
        List<String> topKeywords = recentSearchKeywordRepository.findTopKeywords(10);

        log.debug("[추천 검색어 api 호출] 조회 {}개", topKeywords.size());

        return SearchSuggestionResponse.from(topKeywords);

    }
}
