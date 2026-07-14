package com.tenure.domain.ootd.service;

import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.ootd.dto.OotdRelatedCardResponse;
import com.tenure.domain.ootd.dto.OotdRelatedItemSectionResponse;
import com.tenure.domain.ootd.dto.OotdRelatedResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.global.exception.CustomException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OotdRelatedService {

    private static final int SECTION_LIMIT = 10;

    private final OotdRepository ootdRepository;
    private final OotdTagRepository ootdTagRepository;

    @Transactional(readOnly = true)
    public OotdRelatedResponse getRelatedOotds(Long currentUserId, Long ootdId) {
        Ootd sourceOotd = ootdRepository.findVisibleActiveById(
                        ootdId,
                        currentUserId,
                        OotdPublicationStatus.ACTIVE
                )
                .orElseThrow(() -> new CustomException(OotdErrorCode.OOTD_NOT_FOUND));

        List<Item> sourceItems = findDistinctTaggedItems(ootdId);
        List<OotdRelatedCardResponse> similarMood = findSimilarMood(currentUserId, sourceOotd);
        List<OotdRelatedItemSectionResponse> sameItems = findSameItems(currentUserId, ootdId, sourceItems);
        List<OotdRelatedCardResponse> recommended = findRecommended(currentUserId, ootdId, sourceItems);

        return new OotdRelatedResponse(similarMood, sameItems, recommended);
    }

    private List<Item> findDistinctTaggedItems(Long ootdId) {
        List<OotdTag> tags = ootdTagRepository.findConfirmedItemTagsByOotdId(ootdId, TagStatus.CONFIRMED);
        Map<Long, Item> itemsById = new LinkedHashMap<>();
        for (OotdTag tag : tags) {
            Item item = tag.getItem();
            if (item != null) {
                itemsById.putIfAbsent(item.getId(), item);
            }
        }
        return List.copyOf(itemsById.values());
    }

    private List<OotdRelatedCardResponse> findSimilarMood(Long currentUserId, Ootd sourceOotd) {
        return ootdRepository.findRelatedBySameOwner(
                        currentUserId,
                        sourceOotd.getOwner().getId(),
                        sourceOotd.getId(),
                        OotdPublicationStatus.ACTIVE,
                        PageRequest.of(0, SECTION_LIMIT)
                )
                .stream()
                .map(OotdRelatedCardResponse::of)
                .toList();
    }

    private List<OotdRelatedItemSectionResponse> findSameItems(
            Long currentUserId,
            Long ootdId,
            List<Item> sourceItems
    ) {
        List<OotdRelatedItemSectionResponse> sections = new ArrayList<>();
        for (Item item : sourceItems) {
            List<OotdRelatedCardResponse> ootds = ootdTagRepository.findRelatedOotdsByItemId(
                            item.getId(),
                            currentUserId,
                            ootdId,
                            OotdPublicationStatus.ACTIVE,
                            TagStatus.CONFIRMED,
                            PageRequest.of(0, SECTION_LIMIT)
                    )
                    .stream()
                    .map(OotdRelatedCardResponse::of)
                    .toList();

            if (!ootds.isEmpty()) {
                sections.add(OotdRelatedItemSectionResponse.of(item, ootds));
            }
        }
        return sections;
    }

    private List<OotdRelatedCardResponse> findRecommended(Long currentUserId, Long ootdId, List<Item> sourceItems) {
        Set<Long> excludedOotdIds = new LinkedHashSet<>();
        excludedOotdIds.add(ootdId);

        List<Ootd> recommended = new ArrayList<>();
        Set<Long> categoryIds = findCategoryIds(sourceItems);
        if (!categoryIds.isEmpty()) {
            List<Ootd> categoryMatches = ootdTagRepository.findRelatedOotdsByCategoryIds(
                    categoryIds,
                    currentUserId,
                    excludedOotdIds,
                    OotdPublicationStatus.ACTIVE,
                    TagStatus.CONFIRMED,
                    PageRequest.of(0, SECTION_LIMIT)
            );
            addUnique(recommended, excludedOotdIds, categoryMatches);
        }

        if (recommended.size() < SECTION_LIMIT) {
            List<Ootd> latestFallback = ootdRepository.findLatestVisible(
                    currentUserId,
                    excludedOotdIds,
                    OotdPublicationStatus.ACTIVE,
                    PageRequest.of(0, SECTION_LIMIT - recommended.size())
            );
            addUnique(recommended, excludedOotdIds, latestFallback);
        }

        return recommended.stream()
                .map(OotdRelatedCardResponse::of)
                .toList();
    }

    private Set<Long> findCategoryIds(List<Item> sourceItems) {
        Set<Long> categoryIds = new LinkedHashSet<>();
        for (Item item : sourceItems) {
            Category category = item.getCategory();
            if (category != null && category.getId() != null) {
                categoryIds.add(category.getId());
            }
        }
        return categoryIds;
    }

    private void addUnique(List<Ootd> target, Set<Long> excludedOotdIds, List<Ootd> candidates) {
        for (Ootd candidate : candidates) {
            if (target.size() >= SECTION_LIMIT) {
                return;
            }
            Long candidateId = candidate.getId();
            if (candidateId != null && excludedOotdIds.add(candidateId)) {
                target.add(candidate);
            }
        }
    }
}
