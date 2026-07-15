package com.tenure.domain.search.service;

import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.search.dto.response.*;
import com.tenure.domain.search.entity.RecentSearchKeyword;
import com.tenure.domain.search.entity.RecentViewUser;
import com.tenure.domain.search.enums.SearchSortType;
import com.tenure.domain.search.exception.SearchErrorCode;
import com.tenure.domain.search.repository.RecentSearchKeywordRepository;
import com.tenure.domain.search.repository.RecentViewUserRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGender;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final RecentSearchKeywordRepository recentSearchKeywordRepository;
    private final RecentViewUserRepository recentViewUserRepository;
    private final OotdRepository ootdRepository;
    private final FollowRelationshipRepository followRelationshipRepository;
    private final UserRepository userRepository;

    //추천 검색어
    public SearchSuggestionResponse getSuggestions() {
            return null;
    }

    //최근 검색 and 최근 본 사용자 조회
    public SearchRecentResponse getRecent(Long currentUserId) {

        log.info("[최근 검색 / 최근 본 사용자 조회 / 추천 검색어 ] currentUserId = {}", currentUserId);

        //최근 본 사용자 4건 조회
        List<RecentUserResponse> recentUser = recentViewUserRepository
                .findByRecentUserTop10(currentUserId, 4);

        //최근 검색한 검색어 3개 조회
        List<RecentKeywordResponse> recentKeyword = recentSearchKeywordRepository
                .findByUserTopKeywords(currentUserId, 3);

        //추천 검색어 TOP 10 조회
        List<String> suggestions = recentSearchKeywordRepository.findTopKeywords(10);

        //종합 응답 dto 변환
        return SearchRecentResponse.from(recentUser, recentKeyword, suggestions);

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


    // ootd 검색(공개된 ootd, 카테고리조건, 제품 명 or 브랜드 명에서 키워드 검색)
    @Transactional
    public SearchOotdCursorResponse searchOotds(
            Long currentUserId,
            String keyword, UserGender gender,
            Integer heightMin, Integer heightMax,
            Integer weightMin, Integer weightMax,
            List<Long> categoryIds, ItemStatus itemStatus, SearchSortType sort,
            LocalDateTime cursor, Long cursorId,   // LATEST용
            Integer cursorValue,    // HEART/SAVE/VIEW용
            int size
    ) {

        log.info("[OOTD 검색 api 호출] keyword = {}", keyword);

        // 키워드가 빈칸으로 들어오는경우
        if (keyword == null || keyword.isBlank()) keyword = "";

        // 유효한 검색어면 최근 검색어 저장
        if (!keyword.isBlank()) {
            User user = userRepository.getReferenceById(currentUserId);
            recentSearchKeywordRepository.save(RecentSearchKeyword.of(user, keyword));
        }

        if (categoryIds != null && categoryIds.isEmpty()) {
            categoryIds = null;
        }

        if (keyword.isBlank() && (categoryIds == null || categoryIds.isEmpty())) {
            log.warn("[OOTD 검색 api] 검색어 또는 카테고리를 선택해주세요");
            throw new CustomException(SearchErrorCode.KEYWORD_OR_CATEGORY_REQUIRED);
        }


        log.debug("[OOTD 검색] gender = {}, heightMin = {}, heightMax = {}, weightMin = {}, weightMax = {}, categoryIds = {}, itemStatus = {}, cursor = {}, cursorId = {}",
                gender, heightMin, heightMax, weightMin, weightMax, categoryIds, itemStatus, cursor, cursorId);

        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<Ootd> ootds = null;

        // 추천 기준이 명확치 않아 일단 최신순으로 처리
        // 정렬기준: 최신 순, 추천 순
        if(sort.equals(SearchSortType.LATEST) || sort.equals(SearchSortType.RECOMMEND)) {

            if(cursor == null) cursor = LocalDateTime.now();
            if(cursorId == null) cursorId = Long.MAX_VALUE;

            //repository 조회 후 최신순 정렬
            ootds = ootdRepository
                    .searchOotdsByLatest(keyword, gender, heightMin, heightMax,
                            weightMin, weightMax, categoryIds,
                            itemStatus, cursor, cursorId, pageRequest);

        } else { // 정렬 기준: 조회수 순, 좋아요 순,
            if(cursorValue == null) cursorValue = Integer.MAX_VALUE;
            if(cursorId == null) cursorId = Long.MAX_VALUE;

            //repository 조회 후 조회수 or 좋아요 정렬
            ootds = ootdRepository.searchOotdsByCount(keyword, gender, heightMin, heightMax,
                    weightMin, weightMax, categoryIds,
                    itemStatus, sort.name(), cursorValue, cursorId, pageRequest);
        }

        //검색 total count 조회
        Long count = ootdRepository.searchOotdsTotalCount(keyword, gender, heightMin, heightMax,
                weightMin, weightMax, categoryIds, itemStatus);
        log.debug("[OOTD 검색] 전체 조회 결과(total count) = {}건", count);

        log.debug("[OOTD 검색] 조회 {}건, hasNext = {}", ootds.getNumberOfElements(), ootds.hasNext());

        return SearchOotdCursorResponse.from(ootds, sort, count);
    }

    //유저 검색
    @Transactional
    public SearchUserCursorResponse searchUser(Long currentUserId, String keyword, Long cursorId, int size) {

        log.info("[유저 검색 api] keyword = {}", keyword);

        if (keyword == null || keyword.isBlank()) {
            log.debug("[유저 검색 api] 검색어 없음 → 빈 결과 반환");
            return SearchUserCursorResponse.empty();
        }

        // 최근 검색어 저장
        User user = userRepository.getReferenceById(currentUserId);
        recentSearchKeywordRepository.save(RecentSearchKeyword.of(user, keyword));

        if (cursorId == null) cursorId = Long.MAX_VALUE;

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<SearchUserQueryDto> searchUsers = userRepository.searchUsers(keyword, cursorId, pageRequest);

        log.debug("[유저 검색 api] 조회 {}건, hasNext = {}", searchUsers.getNumberOfElements(), searchUsers.hasNext());

        List<Long> targetIds = searchUsers.getContent()
                .stream().map(SearchUserQueryDto::getId).toList();

        //내가 팔로우 한 유저의 id
        //팔로우 한 유저가 없다면 그냥 빈 set 반환
        HashSet<Long> followingIds = targetIds.isEmpty() ? new HashSet<>() : new HashSet<>(followRelationshipRepository.findFollowingIds(currentUserId, targetIds));

        log.info("[유저 검색 api] 유저 검색 완료");
        return SearchUserCursorResponse.from(searchUsers, followingIds);
    }

}
