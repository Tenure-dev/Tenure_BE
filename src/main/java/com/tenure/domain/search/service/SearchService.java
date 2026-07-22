package com.tenure.domain.search.service;

import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.search.dto.response.*;
import com.tenure.domain.search.entity.RecentSearchKeyword;
import com.tenure.domain.search.entity.RecentViewUser;
import com.tenure.domain.search.enums.SearchSortType;
import com.tenure.domain.search.exception.SearchErrorCode;
import com.tenure.domain.search.repository.RecentSearchKeywordRepository;
import com.tenure.domain.search.repository.RecentViewOotdRepository;
import com.tenure.domain.search.repository.RecentViewUserRepository;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import org.springframework.data.domain.Pageable;
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
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final RecentSearchKeywordRepository recentSearchKeywordRepository;
    private final RecentViewUserRepository recentViewUserRepository;
    private final RecentViewOotdRepository recentViewOotdRepository;
    private final OotdRepository ootdRepository;
    private final OotdTagRepository ootdTagRepository;
    private final FollowRelationshipRepository followRelationshipRepository;
    private final UserRepository userRepository;

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
    public void deleteAllRecentKeyword(Long currentUserId) {
        log.info("[최근 검색어 전체 삭제] currentUserId = {}", currentUserId);
        recentSearchKeywordRepository.deleteAllByUserId(currentUserId);
        log.info("[최근 검색어 전체 삭제] 완료");
    }

    @Transactional
    public void deleteAllRecentUser(Long currentUserId) {
        log.info("[최근 본 사용자 전체 삭제] currentUserId = {}", currentUserId);
        recentViewUserRepository.deleteAllByViewerId(currentUserId);
        log.info("[최근 본 사용자 전체 삭제] 완료");
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
            LocalDateTime cursor, Long cursorId,
            Integer cursorValue,
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

    // 검색 홈 — 4개 섹션 통합 초기 로딩
    public SearchHomeResponse getHome(Long currentUserId) {
        log.info("[검색 홈 api] currentUserId = {}", currentUserId);

        //방금 본 게시물과 유사 게시물
        SearchHomeSimilarOotdCursorResponse similarOotds = getSimilarOotds(currentUserId, 1, Long.MAX_VALUE, 10);
        //7일간 인기 스타일( 좋아요 -> 저장 -> 아이디 순)
        SearchHomePopularOotdCursorResponse popularOotds = getPopularOotds(Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, currentUserId, 10);
        //새로운 ootd(생성일 순 desc)
        SearchHomeNewOotdCursorResponse newOotds = getNewOotds(LocalDateTime.now(), Long.MAX_VALUE, currentUserId, 10);
        //인기 사용자
        SearchHomePopularUserCursorResponse popularUsers = getPopularUsers(Long.MAX_VALUE, Long.MAX_VALUE, currentUserId, 10);

        return SearchHomeResponse.of(similarOotds, popularOotds, newOotds, popularUsers);
    }

    // 검색 홈 — 유사 OOTD 더보기 (워터폴: 우선순위 1→2→3 순으로 size 채울 때까지 조회)
    public SearchHomeSimilarOotdCursorResponse getSimilarOotds(
            Long currentUserId, int cursorPriority, Long cursorId, int size) {

        if (cursorId == null) cursorId = Long.MAX_VALUE;

        log.info("[유사 OOTD] currentUserId = {}, cursorPriority = {}, cursorId = {}", currentUserId, cursorPriority, cursorId);

        // 가장 최근에 본 OOTD id 조회
        Optional<Long> latestOotdId = recentViewOotdRepository.findLatestViewedOotdId(currentUserId);
        if (latestOotdId.isEmpty()) {
            log.debug("[유사 OOTD] 최근 본 OOTD 없음 → 빈 결과 반환");
            return SearchHomeSimilarOotdCursorResponse.empty();
        }

        Long sourceOotdId = latestOotdId.get();
        log.debug("[유사 OOTD] sourceOotdId = {}", sourceOotdId);

        // 최근 본 OOTD에 달린 CONFIRMED 태그에서 아이템 정보 추출
        List<OotdTag> tags = ootdTagRepository.findConfirmedItemTagsByOotdId(sourceOotdId, TagStatus.CONFIRMED);
        if (tags.isEmpty()) {
            log.debug("[유사 OOTD] 태그 없음 → 빈 결과 반환");
            return SearchHomeSimilarOotdCursorResponse.empty();
        }

        // 아이템 id 목록 (우선순위 1 기준)
        List<Long> itemIds = tags.stream()
                .map(t -> t.getItem().getId())
                .distinct().toList();

        // 아이템의 상세 카테고리 id 목록 (우선순위 2 기준)
        List<Long> categoryIds = tags.stream()
                .filter(t -> t.getItem().getCategory() != null)
                .map(t -> t.getItem().getCategory().getId())
                .distinct().toList();

        // 아이템의 상위 카테고리 id 목록 (우선순위 3 기준)
        List<Long> parentCategoryIds = tags.stream()
                .filter(t -> t.getItem().getCategory() != null && t.getItem().getCategory().getParent() != null)
                .map(t -> t.getItem().getCategory().getParent().getId())
                .distinct().toList();

        log.debug("[유사 OOTD] itemIds={}건, categoryIds={}건, parentCategoryIds={}건",
                itemIds.size(), categoryIds.size(), parentCategoryIds.size());

        List<Ootd> result = new ArrayList<>();
        int lastPriority = cursorPriority;

        // 우선순위 1: 같은 아이템 — size 채워지면 우선순위 2, 3 생략
        if (cursorPriority <= 1 && result.size() < size) {
            Long p1Cursor = (cursorPriority == 1) ? cursorId : Long.MAX_VALUE;
            Slice<Ootd> p1 = ootdTagRepository.findSimilarOotdsByItemIds(
                    itemIds, sourceOotdId, p1Cursor, currentUserId,
                    OotdPublicationStatus.ACTIVE, TagStatus.CONFIRMED,
                    PageRequest.of(0, size - result.size()));
            result.addAll(p1.getContent());
            lastPriority = 1;
            log.debug("[유사 OOTD] priority=1 조회 {}건, hasNext={}", p1.getNumberOfElements(), p1.hasNext());
            if (p1.hasNext()) {
                return SearchHomeSimilarOotdCursorResponse.from(result, true, 1);
            }
        }

        // 우선순위 2: 같은 상세 카테고리 — 상세 카테고리 없으면 생략
        if (cursorPriority <= 2 && result.size() < size && !categoryIds.isEmpty()) {
            Long p2Cursor = (cursorPriority == 2) ? cursorId : Long.MAX_VALUE;
            Slice<Ootd> p2 = ootdTagRepository.findSimilarOotdsByCategoryIds(
                    categoryIds, buildExcludeIds(result, sourceOotdId), p2Cursor, currentUserId,
                    OotdPublicationStatus.ACTIVE, TagStatus.CONFIRMED,
                    PageRequest.of(0, size - result.size()));
            result.addAll(p2.getContent());
            lastPriority = 2;
            log.debug("[유사 OOTD] priority=2 조회 {}건, hasNext={}", p2.getNumberOfElements(), p2.hasNext());
            if (p2.hasNext()) {
                return SearchHomeSimilarOotdCursorResponse.from(result, true, 2);
            }
        }

        // 우선순위 3: 같은 상위 카테고리 — 상위 카테고리 없으면 생략
        if (cursorPriority <= 3 && result.size() < size && !parentCategoryIds.isEmpty()) {
            Long p3Cursor = (cursorPriority == 3) ? cursorId : Long.MAX_VALUE;
            Slice<Ootd> p3 = ootdTagRepository.findSimilarOotdsByParentCategoryIds(
                    parentCategoryIds, buildExcludeIds(result, sourceOotdId), p3Cursor, currentUserId,
                    OotdPublicationStatus.ACTIVE, TagStatus.CONFIRMED,
                    PageRequest.of(0, size - result.size()));
            result.addAll(p3.getContent());
            lastPriority = 3;
            log.debug("[유사 OOTD] priority=3 조회 {}건, hasNext={}", p3.getNumberOfElements(), p3.hasNext());
            if (p3.hasNext()) {
                return SearchHomeSimilarOotdCursorResponse.from(result, true, 3);
            }
        }

        log.debug("[유사 OOTD] 최종 {}건, hasNext=false", result.size());
        return SearchHomeSimilarOotdCursorResponse.from(result, false, lastPriority);
    }

    // 검색 홈 — 인기 스타일 더보기
    public SearchHomePopularOotdCursorResponse getPopularOotds(
            Integer cursorValue, Integer cursorSaveValue, Long cursorId, Long currentUserId, int size) {

        if (cursorValue == null) cursorValue = Integer.MAX_VALUE;
        if (cursorSaveValue == null) cursorSaveValue = Integer.MAX_VALUE;
        if (cursorId == null) cursorId = Long.MAX_VALUE;

        log.debug("[인기 스타일] cursorValue = {}, cursorId = {}", cursorValue, cursorId);

        //최근 7일간의 인기 스타일을 가지고옴
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        PageRequest pageRequest = PageRequest.of(0, size);

        //하트 순 7일치 조회
        Slice<Ootd> slice = ootdRepository
                .findPopularOotds(from, cursorValue, cursorSaveValue, cursorId, currentUserId, pageRequest);

        log.debug("[인기 스타일] 조회 {}건, hasNext = {}", slice.getNumberOfElements(), slice.hasNext());
        return SearchHomePopularOotdCursorResponse.from(slice);
    }

    // 검색 홈 — 새로 올라온 OOTD 더보기
    public SearchHomeNewOotdCursorResponse getNewOotds(LocalDateTime cursor, Long cursorId, Long currentUserId, int size) {

        if (cursor == null) cursor = LocalDateTime.now();
        if (cursorId == null) cursorId = Long.MAX_VALUE;

        log.debug("[새 OOTD] cursor = {}, cursorId = {}", cursor, cursorId);

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Ootd> slice = ootdRepository.findNewOotds(cursor, cursorId, currentUserId, pageRequest);

        log.debug("[새 OOTD] 조회 {}건, hasNext = {}", slice.getNumberOfElements(), slice.hasNext());
        return SearchHomeNewOotdCursorResponse.from(slice);
    }

    // 검색 홈 — 인기 사용자 더보기
    public SearchHomePopularUserCursorResponse getPopularUsers(
            Long cursorFollowerCount, Long cursorId, Long currentUserId, int size) {

        if (cursorFollowerCount == null) cursorFollowerCount = Long.MAX_VALUE;
        if (cursorId == null) cursorId = Long.MAX_VALUE;

        log.debug("[인기 사용자] cursorFollowerCount = {}, cursorId = {}", cursorFollowerCount, cursorId);

        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<SearchUserQueryDto> slice = userRepository
                .findPopularUsers(cursorFollowerCount, cursorId, currentUserId, pageRequest);

        log.debug("[인기 사용자] 조회 {}건, hasNext = {}", slice.getNumberOfElements(), slice.hasNext());
        return SearchHomePopularUserCursorResponse.from(slice);
    }

    private Set<Long> buildExcludeIds(List<Ootd> result, Long sourceOotdId) {
        Set<Long> excludeIds = result.stream().map(Ootd::getId).collect(Collectors.toSet());
        excludeIds.add(sourceOotdId);
        return excludeIds;
    }
}
