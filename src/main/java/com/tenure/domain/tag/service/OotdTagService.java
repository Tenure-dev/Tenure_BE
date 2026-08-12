package com.tenure.domain.tag.service;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.ai.AiTagResult;
import com.tenure.domain.ootd.ai.AiTagService;
import com.tenure.domain.ootd.ai.RegionAnalysisResult;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.tag.dto.request.OotdTagAnalyzeRequest;
import com.tenure.domain.tag.dto.request.OotdTagBatchRequest;
import com.tenure.domain.tag.dto.request.OotdTagCreateRequest;
import com.tenure.domain.tag.dto.response.OotdTagAnalyzeResponse;
import com.tenure.domain.tag.dto.response.OotdTagBatchResponse;
import com.tenure.domain.tag.dto.response.OotdTagResponse;
import com.tenure.domain.tag.dto.request.OotdTagUpdateRequest;
import com.tenure.domain.tag.dto.response.OotdTagConfirmResponse;
import com.tenure.domain.tag.dto.response.SimilarItemResponse;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.exception.TagErrorCode;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.global.config.AiTagProperties;
import com.tenure.global.exception.CustomException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OotdTagService {

    private static final List<String> MOCK_LABELS = List.of(
            "가디건", "청바지", "운동화", "니트 스웨터", "코트", "원피스", "스카프"
    );
    private static final Random MOCK_RANDOM = new Random();
    private static final int DEFAULT_SIMILAR_ITEM_LIMIT = 10;
    private static final int MAX_MATCHED_ITEM_CANDIDATES = 5;

    private final OotdRepository ootdRepository;
    private final ItemRepository itemRepository;
    private final OotdTagRepository ootdTagRepository;
    private final AiTagProperties aiTagProperties;
    private final AiTagService aiTagService;

    @Transactional
    public void saveAiTags(Long ootdId, List<AiTagResult> results) {
        Ootd ootd = ootdRepository.findById(ootdId).orElse(null);
        if (ootd == null) {
            log.warn("AI 태그 저장 실패 - OOTD를 찾을 수 없습니다. ootdId={}", ootdId);
            return;
        }
        if (ootd.getPublicationStatus() == OotdPublicationStatus.DELETED) {
            log.info("AI 태그 저장 스킵 - OOTD가 삭제되었습니다. ootdId={}", ootdId);
            return;
        }

        List<Item> ownedItems = itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(
                ootd.getOwner().getId(), ItemStatus.OWNED
        );

        List<OotdTag> tags = results.stream()
                .filter(result -> meetsConfidenceThreshold(result.confidence()))
                .filter(this::hasValidBbox)
                .map(result -> findMatchingItem(ownedItems, result.labelText(), result.categorySmall())
                        .map(item -> OotdTag.createAiTag(
                                ootd,
                                item,
                                result.labelText(),
                                result.bboxX(),
                                result.bboxY(),
                                result.bboxWidth(),
                                result.bboxHeight(),
                                result.confidence()
                        ))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        ootdTagRepository.saveAll(tags);
        ootd.markAutoTagsReady();
        log.info("AI 태그 저장 완료 - ootdId={}, 저장된 태그 수={}/{} (보유 아이템 매칭 실패분 제외)", ootdId, tags.size(), results.size());
    }

    @Transactional(readOnly = true)
    public OotdTagAnalyzeResponse analyzeTagArea(Long ootdId, Long currentUserId, OotdTagAnalyzeRequest request) {
        Ootd ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new CustomException(TagErrorCode.OOTD_NOT_FOUND));
        validateOwner(ootd, currentUserId);

        RegionAnalysisResult result = aiTagService.analyzeRegion(
                ootd.getImageUrl(),
                request.bbox().x(),
                request.bbox().y(),
                request.bbox().width(),
                request.bbox().height()
        );

        if (result.labelText() == null || result.labelText().isBlank()) {
            return OotdTagAnalyzeResponse.of(null, null, null, result.confidence(), List.of());
        }

        List<Long> matchedItemIds = List.of();
        if (meetsConfidenceThreshold(result.confidence())) {
            List<Item> ownedItems = itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(
                    currentUserId, ItemStatus.OWNED
            );
            matchedItemIds = findMatchingItems(
                    ownedItems, result.labelText(), result.categorySmall(), MAX_MATCHED_ITEM_CANDIDATES
            ).stream().map(Item::getId).toList();
        }

        return OotdTagAnalyzeResponse.of(
                result.labelText(), result.categoryLarge(), result.categorySmall(), result.confidence(), matchedItemIds
        );
    }

    // 보유 아이템 중 카테고리가 일치하고, 라벨/브랜드명이 겹치는 아이템을 유사도 점수(레벤슈타인 거리 기반)가
    // 높은 순으로 정렬해 최대 limit개까지 반환한다. 카테고리만 같고 텍스트가 전혀 안 겹치면 잘못된 아이템에
    // 연결될 위험이 있어 후보에서 제외한다.
    private List<Item> findMatchingItems(List<Item> ownedItems, String labelText, String categorySmall, int limit) {
        List<Item> categoryMatches = ownedItems.stream()
                .filter(item -> matchesCategory(item, categorySmall))
                .toList();
        if (categoryMatches.isEmpty()) {
            return List.of();
        }

        String normalizedLabel = normalize(labelText);
        return categoryMatches.stream()
                .filter(item -> matchesLabel(item, labelText))
                .sorted(Comparator.comparingDouble((Item item) -> similarityScore(normalizedLabel, item)).reversed())
                .limit(limit)
                .toList();
    }

    private Optional<Item> findMatchingItem(List<Item> ownedItems, String labelText, String categorySmall) {
        return findMatchingItems(ownedItems, labelText, categorySmall, 1).stream().findFirst();
    }

    // 정규화된 라벨과 아이템명/브랜드명 사이의 문자열 유사도(레벤슈타인 거리 기반, 0~1)를 계산한다.
    // 값이 클수록 더 유사한 아이템이며, 후보가 여러 개일 때 순위를 매기는 데만 사용된다.
    private double similarityScore(String normalizedLabel, Item item) {
        double itemNameScore = levenshteinSimilarity(normalizedLabel, normalize(item.getItemName()));
        double brandNameScore = levenshteinSimilarity(normalizedLabel, normalize(item.getBrandName()));
        return Math.max(itemNameScore, brandNameScore);
    }

    private double levenshteinSimilarity(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        int maxLength = Math.max(a.length(), b.length());
        return 1.0 - ((double) levenshteinDistance(a, b) / maxLength);
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }

    private boolean matchesCategory(Item item, String categorySmall) {
        return categorySmall != null
                && item.getCategory() != null
                && categorySmall.equalsIgnoreCase(item.getCategory().getName());
    }

    private boolean matchesLabel(Item item, String labelText) {
        String normalizedLabel = normalize(labelText);
        if (normalizedLabel.isEmpty()) {
            return false;
        }
        String itemName = normalize(item.getItemName());
        String brandName = normalize(item.getBrandName());
        return (!itemName.isEmpty() && (normalizedLabel.contains(itemName) || itemName.contains(normalizedLabel)))
                || (!brandName.isEmpty() && normalizedLabel.contains(brandName));
    }

    private String normalize(String value) {
        return value == null ? "" : value.replace(" ", "").toLowerCase();
    }

    @Transactional
    public OotdTagResponse createManualTag(Long ootdId, Long currentUserId, OotdTagCreateRequest request) {
        validateStatus(request.status());

        Ootd ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new CustomException(TagErrorCode.OOTD_NOT_FOUND));
        validateOwner(ootd, currentUserId);

        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new CustomException(TagErrorCode.ITEM_NOT_FOUND));

        OotdTag tag = OotdTag.createManualTag(
                ootd,
                item,
                request.labelText(),
                request.bbox().x(),
                request.bbox().y(),
                request.bbox().width(),
                request.bbox().height()
        );
        ootdTagRepository.save(tag);
        recalculateWearStats(item);

        return OotdTagResponse.of(tag);
    }

    @Transactional
    public OotdTagResponse updateTag(Long tagId, Long currentUserId, OotdTagUpdateRequest request) {
        OotdTag tag = ootdTagRepository.findById(tagId)
                .orElseThrow(() -> new CustomException(TagErrorCode.TAG_NOT_FOUND));
        validateOwner(tag.getOotd(), currentUserId);

        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new CustomException(TagErrorCode.ITEM_NOT_FOUND));
        Item previousItem = tag.getItem();

        tag.updateContent(
                item,
                request.labelText(),
                request.bbox().x(),
                request.bbox().y(),
                request.bbox().width(),
                request.bbox().height()
        );

        // item이 교체된 경우, 새로 태그된 아이템뿐 아니라 태그가 빠져나간 이전 아이템의 착용 정보도
        // 다시 계산해야 카운트가 남아있지 않는다.
        recalculateWearStats(item);
        if (previousItem != null && !previousItem.getId().equals(item.getId())) {
            recalculateWearStats(previousItem);
        }

        return OotdTagResponse.of(tag);
    }

    @Transactional
    public OotdTagBatchResponse createTagsBatch(
            Long ootdId,
            Long currentUserId,
            OotdTagBatchRequest request
    ) {
        Ootd ootd = ootdRepository.findById(ootdId)
                .filter(found -> found.getPublicationStatus() != OotdPublicationStatus.DELETED)
                .orElseThrow(() -> new CustomException(TagErrorCode.OOTD_NOT_FOUND));
        validateOwner(ootd, currentUserId);

        List<Long> itemIds = request.tags().stream()
                .map(OotdTagBatchRequest.TagItem::itemId)
                .distinct()
                .toList();
        Map<Long, Item> itemsById = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        if (itemsById.size() != itemIds.size()) {
            throw new CustomException(TagErrorCode.BATCH_ITEM_NOT_FOUND);
        }

        // 기존 태그가 삭제되면서 빠지는 아이템도 착용 정보 재계산 대상이므로, 삭제 전에 대상 아이템을 모아둔다.
        Map<Long, Item> affectedItemsById = new LinkedHashMap<>();
        ootdTagRepository.findAllByOotdId(ootdId).forEach(previousTag -> {
            Item previousItem = previousTag.getItem();
            if (previousItem != null) {
                affectedItemsById.put(previousItem.getId(), previousItem);
            }
        });

        ootdTagRepository.deleteAllByOotdId(ootdId);

        List<OotdTag> tags = request.tags().stream()
                .map(tagItem -> OotdTag.createManualTag(
                        ootd,
                        itemsById.get(tagItem.itemId()),
                        tagItem.labelText(),
                        tagItem.bbox().x(),
                        tagItem.bbox().y(),
                        tagItem.bbox().width(),
                        tagItem.bbox().height()
                ))
                .toList();
        ootdTagRepository.saveAll(tags);

        tags.forEach(tag -> affectedItemsById.put(tag.getItem().getId(), tag.getItem()));
        affectedItemsById.values().forEach(this::recalculateWearStats);

        return OotdTagBatchResponse.of(ootd.getId(), tags);
    }

    @Transactional
    public OotdTagConfirmResponse confirmTags(Long ootdId, Long currentUserId) {
        Ootd ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new CustomException(TagErrorCode.OOTD_NOT_FOUND));
        validateOwner(ootd, currentUserId);

        List<OotdTag> tags = ootdTagRepository.findAllByOotdId(ootdId);
        if (tags.isEmpty()) {
            throw new CustomException(TagErrorCode.TAG_NOT_FOUND);
        }

        tags.forEach(OotdTag::confirm);
        ootd.confirmTags();

        // AUTO_UNCONFIRMED -> CONFIRMED로 상태가 바뀐 태그들의 아이템 착용 정보를 재계산한다.
        // 이미 CONFIRMED였던 태그(수동 등록분)는 재계산해도 값이 그대로라 안전하다.
        Map<Long, Item> itemsById = new LinkedHashMap<>();
        tags.forEach(tag -> {
            Item item = tag.getItem();
            if (item != null) {
                itemsById.put(item.getId(), item);
            }
        });
        itemsById.values().forEach(this::recalculateWearStats);

        return OotdTagConfirmResponse.of(ootd);
    }

    // 해당 아이템이 현재 CONFIRMED로 태그된, 게시(ACTIVE) 상태인 서로 다른 OOTD 개수와
    // 그중 가장 최근 OOTD 날짜로 착용 정보를 매번 재계산해서 덮어쓴다. 누적(+1) 방식이 아니므로
    // 같은 게시물 내 태그 추가/삭제를 반복해도 카운트가 부풀지 않고, 태그가 빠지면 자동으로 줄어든다.
    private void recalculateWearStats(Item item) {
        OotdTagRepository.ItemWearStatsProjection stats = ootdTagRepository.findWearStatsByItemId(
                item.getId(), TagStatus.CONFIRMED, OotdPublicationStatus.ACTIVE
        );
        int wornOotdCount = stats.getWornOotdCount() == null ? 0 : stats.getWornOotdCount().intValue();
        LocalDate lastWornAt = stats.getLastWornAt() == null ? null : stats.getLastWornAt().toLocalDate();
        item.updateWearStats(wornOotdCount, lastWornAt);
    }

    @Transactional(readOnly = true)
    public List<SimilarItemResponse> getSimilarItemsForTagging(Long currentUserId, Long ootdId, Integer limit) {
        int effectiveLimit = (limit == null || limit <= 0) ? DEFAULT_SIMILAR_ITEM_LIMIT : limit;

        List<Item> ownedItems = itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(
                currentUserId, ItemStatus.OWNED);
        if (ownedItems.isEmpty()) {
            return List.of();
        }

        OotdTagContext context = resolveOotdTagContext(ootdId, currentUserId);

        return ownedItems.stream()
                .filter(item -> !context.taggedItemIds().contains(item.getId()))
                .sorted(Comparator.comparingInt(item -> matchesContext(item, context) ? 0 : 1))
                .limit(effectiveLimit)
                .map(SimilarItemResponse::of)
                .toList();
    }

    private OotdTagContext resolveOotdTagContext(Long ootdId, Long currentUserId) {
        if (ootdId == null) {
            return OotdTagContext.empty();
        }

        Ootd ootd = ootdRepository.findById(ootdId).orElse(null);
        if (ootd == null || !ootd.getOwner().getId().equals(currentUserId)) {
            return OotdTagContext.empty();
        }

        List<OotdTag> confirmedItemTags = ootdTagRepository.findConfirmedItemTagsByOotdId(ootdId, TagStatus.CONFIRMED);
        Set<Long> categoryIds = confirmedItemTags.stream()
                .map(tag -> tag.getItem().getCategory().getId())
                .collect(Collectors.toSet());
        Set<String> brandNames = confirmedItemTags.stream()
                .map(tag -> tag.getItem().getBrandName())
                .collect(Collectors.toSet());
        Set<Long> taggedItemIds = confirmedItemTags.stream()
                .map(tag -> tag.getItem().getId())
                .collect(Collectors.toSet());

        return new OotdTagContext(categoryIds, brandNames, taggedItemIds);
    }

    private boolean matchesContext(Item item, OotdTagContext context) {
        return context.categoryIds().contains(item.getCategory().getId())
                || context.brandNames().contains(item.getBrandName());
    }

    private record OotdTagContext(Set<Long> categoryIds, Set<String> brandNames, Set<Long> taggedItemIds) {
        static OotdTagContext empty() {
            return new OotdTagContext(Set.of(), Set.of(), Set.of());
        }
    }

    @Transactional
    public List<OotdTagResponse> generateMockAiTags(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new CustomException(TagErrorCode.OOTD_NOT_FOUND));

        List<OotdTag> tags = createMockTags(ootd);
        ootdTagRepository.saveAll(tags);
        log.info("Mock AI 태그 생성 완료 - ootdId={}, 생성된 태그 수={}", ootdId, tags.size());

        return tags.stream().map(OotdTagResponse::of).toList();
    }

    private List<OotdTag> createMockTags(Ootd ootd) {
        int count = MOCK_RANDOM.nextInt(2, 4);
        return IntStream.range(0, count)
                .mapToObj(i -> OotdTag.createAiTag(
                        ootd,
                        MOCK_LABELS.get(MOCK_RANDOM.nextInt(MOCK_LABELS.size())),
                        randomBboxValue(0.0, 0.5),
                        randomBboxValue(0.0, 0.5),
                        randomBboxValue(0.2, 0.5),
                        randomBboxValue(0.2, 0.5),
                        randomConfidence()
                ))
                .toList();
    }

    private BigDecimal randomBboxValue(double origin, double bound) {
        return BigDecimal.valueOf(MOCK_RANDOM.nextDouble(origin, bound)).setScale(5, RoundingMode.HALF_UP);
    }

    private BigDecimal randomConfidence() {
        return BigDecimal.valueOf(MOCK_RANDOM.nextDouble(0.85, 0.99)).setScale(4, RoundingMode.HALF_UP);
    }

    private boolean meetsConfidenceThreshold(BigDecimal confidence) {
        return confidence != null
                && confidence.compareTo(aiTagProperties.confidenceThreshold()) >= 0;
    }

    private boolean hasValidBbox(AiTagResult result) {
        boolean valid = isNormalized(result.bboxX())
                && isNormalized(result.bboxY())
                && isNormalized(result.bboxWidth())
                && isNormalized(result.bboxHeight());
        if (!valid) {
            log.warn("AI 태그 bbox 값이 0~1 범위를 벗어나 제외됨 - labelText={}, bbox=({}, {}, {}, {})",
                    result.labelText(), result.bboxX(), result.bboxY(), result.bboxWidth(), result.bboxHeight());
        }
        return valid;
    }

    private boolean isNormalized(BigDecimal value) {
        return value != null
                && value.compareTo(BigDecimal.ZERO) >= 0
                && value.compareTo(BigDecimal.ONE) <= 0;
    }

    private void validateOwner(Ootd ootd, Long currentUserId) {
        if (!ootd.getOwner().getId().equals(currentUserId)) {
            throw new CustomException(TagErrorCode.TAG_OWNER_ONLY);
        }
    }

    private void validateStatus(String status) {
        if (!TagStatus.CONFIRMED.name().equalsIgnoreCase(status)) {
            throw new CustomException(TagErrorCode.TAG_STATUS_INVALID);
        }
    }
}
