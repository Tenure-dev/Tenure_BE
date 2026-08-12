package com.tenure.domain.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.ai.AiTagResult;
import com.tenure.domain.ootd.ai.AiTagService;
import com.tenure.domain.ootd.ai.RegionAnalysisResult;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdTagStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.tag.dto.request.OotdTagAnalyzeRequest;
import com.tenure.domain.tag.dto.request.OotdTagBatchRequest;
import com.tenure.domain.tag.dto.request.OotdTagCreateRequest;
import com.tenure.domain.tag.dto.request.OotdTagCreateRequest.BboxRequest;
import com.tenure.domain.tag.dto.response.OotdTagAnalyzeResponse;
import com.tenure.domain.tag.dto.response.OotdTagBatchResponse;
import com.tenure.domain.tag.dto.response.OotdTagResponse;
import com.tenure.domain.tag.dto.request.OotdTagUpdateRequest;
import com.tenure.domain.tag.dto.response.OotdTagConfirmResponse;
import com.tenure.domain.tag.dto.response.SimilarItemResponse;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagSource;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.exception.TagErrorCode;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.global.config.AiTagProperties;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OotdTagServiceTest {

    private static final Long OOTD_ID = 1L;
    private static final Long OWNER_ID = 1L;
    private static final Long ITEM_ID = 10L;
    private static final Long TAG_ID = 100L;

    @Mock
    private OotdRepository ootdRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OotdTagRepository ootdTagRepository;

    @Mock
    private AiTagService aiTagService;

    private OotdTagService ootdTagService;

    @BeforeEach
    void setUp() {
        AiTagProperties aiTagProperties = new AiTagProperties(BigDecimal.valueOf(0.6));
        ootdTagService = new OotdTagService(ootdRepository, itemRepository, ootdTagRepository, aiTagProperties, aiTagService);
    }

    @Test
    void createManualTag_savesConfirmedTag() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item item = item(ITEM_ID);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        OotdTagCreateRequest request = request(ITEM_ID, "CONFIRMED");

        OotdTagResponse response = ootdTagService.createManualTag(OOTD_ID, OWNER_ID, request);

        assertThat(response.itemId()).isEqualTo(ITEM_ID);
        assertThat(response.status()).isEqualTo(TagStatus.CONFIRMED);
        assertThat(response.source()).isEqualTo(TagSource.MANUAL);

        verify(ootdTagRepository).save(any(OotdTag.class));
    }

    @Test
    void createManualTag_rejectsMissingOotd() {
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdTagService.createManualTag(OOTD_ID, OWNER_ID, request(ITEM_ID, "CONFIRMED")))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.OOTD_NOT_FOUND);
    }

    @Test
    void createManualTag_rejectsNonOwner() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        Long strangerId = 999L;
        assertThatThrownBy(() -> ootdTagService.createManualTag(OOTD_ID, strangerId, request(ITEM_ID, "CONFIRMED")))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.TAG_OWNER_ONLY);
    }

    @Test
    void createManualTag_rejectsMissingItem() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdTagService.createManualTag(OOTD_ID, OWNER_ID, request(ITEM_ID, "CONFIRMED")))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.ITEM_NOT_FOUND);
    }

    @Test
    void createManualTag_rejectsNonConfirmedStatus() {
        assertThatThrownBy(() -> ootdTagService.createManualTag(OOTD_ID, OWNER_ID, request(ITEM_ID, "AUTO_UNCONFIRMED")))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.TAG_STATUS_INVALID);
    }

    @Test
    void createTagsBatch_savesConfirmedTagsAndOverwritesExisting() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item firstItem = item(ITEM_ID);
        Item secondItem = item(ITEM_ID + 1);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(itemRepository.findAllById(List.of(ITEM_ID, ITEM_ID + 1)))
                .thenReturn(List.of(firstItem, secondItem));

        OotdTagBatchResponse response = ootdTagService.createTagsBatch(
                OOTD_ID, OWNER_ID, batchRequest(List.of(ITEM_ID, ITEM_ID + 1)));

        assertThat(response.ootdId()).isEqualTo(OOTD_ID);
        assertThat(response.savedCount()).isEqualTo(2);
        assertThat(response.tags()).allSatisfy(tag -> {
            assertThat(tag.status()).isEqualTo(TagStatus.CONFIRMED);
            assertThat(tag.source()).isEqualTo(TagSource.MANUAL);
        });

        verify(ootdTagRepository).deleteAllByOotdId(OOTD_ID);
        verify(ootdTagRepository).saveAll(anyList());
    }

    @Test
    void createTagsBatch_rejectsMissingOotd() {
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdTagService.createTagsBatch(
                OOTD_ID, OWNER_ID, batchRequest(List.of(ITEM_ID))))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.OOTD_NOT_FOUND);
    }

    @Test
    void createTagsBatch_rejectsDeletedOotd() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        ReflectionTestUtils.setField(ootd, "publicationStatus", OotdPublicationStatus.DELETED);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        assertThatThrownBy(() -> ootdTagService.createTagsBatch(
                OOTD_ID, OWNER_ID, batchRequest(List.of(ITEM_ID))))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.OOTD_NOT_FOUND);
    }

    @Test
    void createTagsBatch_rejectsNonOwner() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        Long strangerId = 999L;
        assertThatThrownBy(() -> ootdTagService.createTagsBatch(
                OOTD_ID, strangerId, batchRequest(List.of(ITEM_ID))))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.TAG_OWNER_ONLY);
    }

    @Test
    void createTagsBatch_rejectsWhenItemDoesNotExist() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item existingItem = item(ITEM_ID);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(itemRepository.findAllById(List.of(ITEM_ID, ITEM_ID + 1)))
                .thenReturn(List.of(existingItem));

        assertThatThrownBy(() -> ootdTagService.createTagsBatch(
                OOTD_ID, OWNER_ID, batchRequest(List.of(ITEM_ID, ITEM_ID + 1))))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.BATCH_ITEM_NOT_FOUND);

        verify(ootdTagRepository, never()).saveAll(anyList());
    }

    @Test
    void getSimilarItemsForTagging_prioritizesItemsMatchingOotdContext() {
        Item matchingItem = item(20L, 101L, "Nike");
        Item nonMatchingItem = item(21L, 202L, "Adidas");

        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(nonMatchingItem, matchingItem));

        Item contextItem = item(30L, 101L, "Nike");
        Ootd ootd = ootd(OOTD_ID, user(OWNER_ID));
        OotdTag contextTag = OotdTag.createManualTag(
                ootd, contextItem, "라벨",
                BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)
        );
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(contextTag));

        List<SimilarItemResponse> response = ootdTagService.getSimilarItemsForTagging(OWNER_ID, OOTD_ID, null);

        assertThat(response).extracting(SimilarItemResponse::itemId)
                .containsExactly(20L, 21L);
    }

    @Test
    void getSimilarItemsForTagging_prioritizesItemsMatchingOnlyCategory() {
        Item categoryOnlyMatch = item(20L, 101L, "Uniqlo");
        Item nonMatchingItem = item(21L, 202L, "Adidas");

        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(nonMatchingItem, categoryOnlyMatch));

        Item contextItem = item(30L, 101L, "Nike");
        Ootd ootd = ootd(OOTD_ID, user(OWNER_ID));
        OotdTag contextTag = OotdTag.createManualTag(
                ootd, contextItem, "라벨",
                BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)
        );
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(contextTag));

        List<SimilarItemResponse> response = ootdTagService.getSimilarItemsForTagging(OWNER_ID, OOTD_ID, null);

        assertThat(response).extracting(SimilarItemResponse::itemId)
                .containsExactly(20L, 21L);
    }

    @Test
    void getSimilarItemsForTagging_prioritizesItemsMatchingOnlyBrand() {
        Item brandOnlyMatch = item(20L, 303L, "Nike");
        Item nonMatchingItem = item(21L, 202L, "Adidas");

        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(nonMatchingItem, brandOnlyMatch));

        Item contextItem = item(30L, 101L, "Nike");
        Ootd ootd = ootd(OOTD_ID, user(OWNER_ID));
        OotdTag contextTag = OotdTag.createManualTag(
                ootd, contextItem, "라벨",
                BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)
        );
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(contextTag));

        List<SimilarItemResponse> response = ootdTagService.getSimilarItemsForTagging(OWNER_ID, OOTD_ID, null);

        assertThat(response).extracting(SimilarItemResponse::itemId)
                .containsExactly(20L, 21L);
    }

    @Test
    void getSimilarItemsForTagging_ignoresContextWhenOotdNotOwnedByCurrentUser() {
        Item matchingItem = item(20L, 101L, "Nike");
        Item nonMatchingItem = item(21L, 202L, "Adidas");

        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(nonMatchingItem, matchingItem));

        Long strangerId = 999L;
        Ootd strangersOotd = ootd(OOTD_ID, user(strangerId));
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(strangersOotd));

        List<SimilarItemResponse> response = ootdTagService.getSimilarItemsForTagging(OWNER_ID, OOTD_ID, null);

        assertThat(response).extracting(SimilarItemResponse::itemId)
                .containsExactly(21L, 20L);
        verify(ootdTagRepository, never()).findConfirmedItemTagsByOotdId(any(), any());
    }

    @Test
    void getSimilarItemsForTagging_excludesItemsAlreadyTaggedInOotd() {
        Item taggedItem = item(20L, 101L, "Nike");
        Item untaggedItem = item(21L, 202L, "Adidas");

        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(taggedItem, untaggedItem));

        Ootd ootd = ootd(OOTD_ID, user(OWNER_ID));
        OotdTag contextTag = OotdTag.createManualTag(
                ootd, taggedItem, "라벨",
                BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)
        );
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findConfirmedItemTagsByOotdId(OOTD_ID, TagStatus.CONFIRMED))
                .thenReturn(List.of(contextTag));

        List<SimilarItemResponse> response = ootdTagService.getSimilarItemsForTagging(OWNER_ID, OOTD_ID, null);

        assertThat(response).extracting(SimilarItemResponse::itemId)
                .containsExactly(21L);
    }

    @Test
    void getSimilarItemsForTagging_appliesGivenLimit() {
        List<Item> ownedItems = List.of(
                item(20L, 101L, "Nike"),
                item(21L, 202L, "Adidas"),
                item(22L, 303L, "Puma")
        );
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(ownedItems);

        List<SimilarItemResponse> response = ootdTagService.getSimilarItemsForTagging(OWNER_ID, null, 2);

        assertThat(response).extracting(SimilarItemResponse::itemId)
                .containsExactly(20L, 21L);
    }

    @Test
    void getSimilarItemsForTagging_returnsEmptyListWhenNoOwnedItems() {
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of());

        List<SimilarItemResponse> response = ootdTagService.getSimilarItemsForTagging(OWNER_ID, OOTD_ID, null);

        assertThat(response).isEmpty();
        verify(ootdTagRepository, never()).findConfirmedItemTagsByOotdId(any(), any());
    }

    @Test
    void updateTag_updatesContentAndConfirmsStatus() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item item = item(ITEM_ID);
        OotdTag aiTag = aiTag(TAG_ID, ootd);

        when(ootdTagRepository.findById(TAG_ID)).thenReturn(Optional.of(aiTag));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        OotdTagResponse response = ootdTagService.updateTag(TAG_ID, OWNER_ID, updateRequest(ITEM_ID));

        assertThat(response.itemId()).isEqualTo(ITEM_ID);
        assertThat(response.labelText()).isEqualTo("블루종 자켓");
        assertThat(response.source()).isEqualTo(TagSource.AI);
        assertThat(response.status()).isEqualTo(TagStatus.CONFIRMED);
        assertThat(aiTag.getStatus()).isEqualTo(TagStatus.CONFIRMED);
        assertThat(aiTag.getItem()).isEqualTo(item);
    }

    @Test
    void updateTag_keepsConfirmedStatusWhenAlreadyConfirmed() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item originalItem = item(ITEM_ID);
        Item newItem = item(ITEM_ID + 1);
        OotdTag manualTag = OotdTag.createManualTag(
                ootd, originalItem, "청바지",
                BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)
        );

        when(ootdTagRepository.findById(TAG_ID)).thenReturn(Optional.of(manualTag));
        when(itemRepository.findById(ITEM_ID + 1)).thenReturn(Optional.of(newItem));

        OotdTagResponse response = ootdTagService.updateTag(TAG_ID, OWNER_ID, updateRequest(ITEM_ID + 1));

        assertThat(response.status()).isEqualTo(TagStatus.CONFIRMED);
        assertThat(manualTag.getStatus()).isEqualTo(TagStatus.CONFIRMED);
        assertThat(manualTag.getItem()).isEqualTo(newItem);
    }

    @Test
    void updateTag_rejectsMissingTag() {
        when(ootdTagRepository.findById(TAG_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdTagService.updateTag(TAG_ID, OWNER_ID, updateRequest(ITEM_ID)))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.TAG_NOT_FOUND);
    }

    @Test
    void updateTag_rejectsNonOwner() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        OotdTag aiTag = aiTag(TAG_ID, ootd);

        when(ootdTagRepository.findById(TAG_ID)).thenReturn(Optional.of(aiTag));

        Long strangerId = 999L;
        assertThatThrownBy(() -> ootdTagService.updateTag(TAG_ID, strangerId, updateRequest(ITEM_ID)))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.TAG_OWNER_ONLY);
    }

    @Test
    void updateTag_rejectsMissingItem() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        OotdTag aiTag = aiTag(TAG_ID, ootd);

        when(ootdTagRepository.findById(TAG_ID)).thenReturn(Optional.of(aiTag));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdTagService.updateTag(TAG_ID, OWNER_ID, updateRequest(ITEM_ID)))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.ITEM_NOT_FOUND);
    }

    @Test
    void confirmTags_confirmsAllTagsAndRestoresArchivedOotd() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        ReflectionTestUtils.setField(ootd, "publicationStatus", OotdPublicationStatus.ARCHIVED);
        ReflectionTestUtils.setField(ootd, "reviewRequired", true);
        OotdTag aiTag = aiTag(TAG_ID, ootd);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findAllByOotdId(OOTD_ID)).thenReturn(List.of(aiTag));

        OotdTagConfirmResponse response = ootdTagService.confirmTags(OOTD_ID, OWNER_ID);

        assertThat(response.tagStatus()).isEqualTo(OotdTagStatus.CONFIRMED);
        assertThat(response.tagConfirmedAt()).isNotNull();
        assertThat(response.reviewRequired()).isFalse();
        assertThat(response.publicationStatus()).isEqualTo(OotdPublicationStatus.ACTIVE);
        assertThat(aiTag.getStatus()).isEqualTo(TagStatus.CONFIRMED);
    }

    @Test
    void confirmTags_rejectsMissingOotd() {
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdTagService.confirmTags(OOTD_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.OOTD_NOT_FOUND);
    }

    @Test
    void confirmTags_rejectsNonOwner() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        Long strangerId = 999L;
        assertThatThrownBy(() -> ootdTagService.confirmTags(OOTD_ID, strangerId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.TAG_OWNER_ONLY);
    }

    @Test
    void confirmTags_rejectsWhenNoTagsExist() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(ootdTagRepository.findAllByOotdId(OOTD_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> ootdTagService.confirmTags(OOTD_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.TAG_NOT_FOUND);
    }

    @Test
    void generateMockAiTags_savesAutoUnconfirmedTagsWithHighConfidence() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        List<OotdTagResponse> response = ootdTagService.generateMockAiTags(OOTD_ID);

        assertThat(response).isNotEmpty();
        assertThat(response).allSatisfy(tag -> {
            assertThat(tag.source()).isEqualTo(TagSource.AI);
            assertThat(tag.status()).isEqualTo(TagStatus.AUTO_UNCONFIRMED);
        });

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OotdTag>> captor = ArgumentCaptor.forClass(List.class);
        verify(ootdTagRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).allSatisfy(tag ->
                assertThat(tag.getConfidence()).isGreaterThanOrEqualTo(BigDecimal.valueOf(0.85)));
    }

    @Test
    void generateMockAiTags_rejectsMissingOotd() {
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdTagService.generateMockAiTags(OOTD_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.OOTD_NOT_FOUND);
    }

    @Test
    void saveAiTags_filtersOutResultsBelowConfidenceThreshold() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item jacket = itemWithCategory(30L, "아우터", "Uniqlo", "블루종 자켓");
        Item sneakers = itemWithCategory(31L, "신발", "Nike", "운동화");

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(jacket, sneakers));

        List<AiTagResult> results = List.of(
                aiTagResult("블루종 자켓", "아우터", BigDecimal.valueOf(0.92)),
                aiTagResult("청바지", "하의", BigDecimal.valueOf(0.55)),
                aiTagResult("운동화", "신발", BigDecimal.valueOf(0.60))
        );

        ootdTagService.saveAiTags(OOTD_ID, results);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OotdTag>> captor = ArgumentCaptor.forClass(List.class);
        verify(ootdTagRepository).saveAll(captor.capture());

        List<OotdTag> savedTags = captor.getValue();
        assertThat(savedTags).hasSize(2);
        assertThat(savedTags).allSatisfy(tag -> {
            assertThat(tag.getSource()).isEqualTo(TagSource.AI);
            assertThat(tag.getStatus()).isEqualTo(TagStatus.AUTO_UNCONFIRMED);
            assertThat(tag.getItem()).isNotNull();
        });
        assertThat(savedTags).extracting(OotdTag::getLabelText)
                .containsExactlyInAnyOrder("블루종 자켓", "운동화");
        assertThat(ootd.getTagStatus()).isEqualTo(OotdTagStatus.AUTO_UNCONFIRMED);
    }

    @Test
    void saveAiTags_filtersOutResultsWithOutOfRangeBbox() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item jacket = itemWithCategory(30L, "아우터", "Uniqlo", "블루종 자켓");

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(jacket));

        AiTagResult validResult = aiTagResult("블루종 자켓", "아우터", BigDecimal.valueOf(0.92));
        AiTagResult outOfRangeResult = new AiTagResult(
                "청바지",
                "하의",
                "청바지",
                BigDecimal.valueOf(120),
                BigDecimal.valueOf(0.2),
                BigDecimal.valueOf(0.3),
                BigDecimal.valueOf(0.4),
                BigDecimal.valueOf(0.9)
        );

        ootdTagService.saveAiTags(OOTD_ID, List.of(validResult, outOfRangeResult));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OotdTag>> captor = ArgumentCaptor.forClass(List.class);
        verify(ootdTagRepository).saveAll(captor.capture());

        List<OotdTag> savedTags = captor.getValue();
        assertThat(savedTags).hasSize(1);
        assertThat(savedTags.get(0).getLabelText()).isEqualTo("블루종 자켓");
    }

    @Test
    void saveAiTags_skipsResultWhenNoOwnedItemMatches() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item unrelatedItem = itemWithCategory(30L, "신발", "Nike", "운동화");

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(unrelatedItem));

        ootdTagService.saveAiTags(
                OOTD_ID,
                List.of(aiTagResult("블루종 자켓", "아우터", BigDecimal.valueOf(0.92)))
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OotdTag>> captor = ArgumentCaptor.forClass(List.class);
        verify(ootdTagRepository).saveAll(captor.capture());

        assertThat(captor.getValue()).isEmpty();
        assertThat(ootd.getTagStatus()).isEqualTo(OotdTagStatus.AUTO_UNCONFIRMED);
    }

    @Test
    void saveAiTags_skipsResultWhenCategoryMatchesButLabelDoesNot() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item unrelatedJacket = itemWithCategory(30L, "아우터", "Adidas", "레인 코트");

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(unrelatedJacket));

        ootdTagService.saveAiTags(
                OOTD_ID,
                List.of(aiTagResult("블루종 자켓", "아우터", BigDecimal.valueOf(0.92)))
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OotdTag>> captor = ArgumentCaptor.forClass(List.class);
        verify(ootdTagRepository).saveAll(captor.capture());

        assertThat(captor.getValue()).isEmpty();
    }

    @Test
    void saveAiTags_doesNothingWhenOotdNotFound() {
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.empty());

        ootdTagService.saveAiTags(OOTD_ID, List.of(aiTagResult("블루종 자켓", "아우터", BigDecimal.valueOf(0.9))));

        verify(ootdTagRepository, never()).saveAll(anyList());
    }

    @Test
    void saveAiTags_skipsWhenOotdIsDeleted() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        ReflectionTestUtils.setField(ootd, "publicationStatus", OotdPublicationStatus.DELETED);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        ootdTagService.saveAiTags(OOTD_ID, List.of(aiTagResult("블루종 자켓", "아우터", BigDecimal.valueOf(0.9))));

        verify(ootdTagRepository, never()).saveAll(anyList());
    }

    @Test
    void analyzeTagArea_returnsMatchedItemWhenConfidentAndMatching() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item jacket = itemWithCategory(30L, "아우터", "Uniqlo", "블루종 자켓");

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(aiTagService.analyzeRegion(
                eq(ootd.getImageUrl()),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        )).thenReturn(new RegionAnalysisResult("블루종 자켓", "아우터", "아우터", BigDecimal.valueOf(0.9)));
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(jacket));

        OotdTagAnalyzeResponse response = ootdTagService.analyzeTagArea(OOTD_ID, OWNER_ID, analyzeRequest());

        assertThat(response.labelText()).isEqualTo("블루종 자켓");
        assertThat(response.categoryLarge()).isEqualTo("아우터");
        assertThat(response.matchedItemIds()).containsExactly(30L);
    }

    @Test
    void analyzeTagArea_returnsMatchedItemsOrderedBySimilarityDescending() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        // 셋 다 라벨("블루종 자켓")과 부분적으로 겹쳐 매칭 후보가 되지만, 문자열 편집 거리가 가까운 순서는
        // 정확일치(exact) > 라벨에 몇 글자만 덧붙은 경우(close) > 라벨 일부만 남은 경우(loose) 순이다.
        Item exactMatch = itemWithCategory(30L, "아우터", "Uniqlo", "블루종 자켓");
        Item closeMatch = itemWithCategory(31L, "아우터", "Uniqlo", "블루종 자켓 새상품");
        Item looseMatch = itemWithCategory(32L, "아우터", "Uniqlo", "자켓");

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(aiTagService.analyzeRegion(
                eq(ootd.getImageUrl()),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        )).thenReturn(new RegionAnalysisResult("블루종 자켓", "아우터", "아우터", BigDecimal.valueOf(0.9)));
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of(looseMatch, closeMatch, exactMatch));

        OotdTagAnalyzeResponse response = ootdTagService.analyzeTagArea(OOTD_ID, OWNER_ID, analyzeRequest());

        assertThat(response.matchedItemIds()).containsExactly(30L, 31L, 32L);
    }

    @Test
    void analyzeTagArea_returnsNullMatchedItemWhenNoOwnedItemMatches() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(aiTagService.analyzeRegion(
                eq(ootd.getImageUrl()),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        )).thenReturn(new RegionAnalysisResult("블루종 자켓", "아우터", "아우터", BigDecimal.valueOf(0.9)));
        when(itemRepository.findByOwner_IdAndItemStatusOrderByCreatedAtDesc(OWNER_ID, ItemStatus.OWNED))
                .thenReturn(List.of());

        OotdTagAnalyzeResponse response = ootdTagService.analyzeTagArea(OOTD_ID, OWNER_ID, analyzeRequest());

        assertThat(response.labelText()).isEqualTo("블루종 자켓");
        assertThat(response.matchedItemIds()).isEmpty();
    }

    @Test
    void analyzeTagArea_returnsNullMatchedItemWhenBelowConfidenceThreshold() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);
        Item jacket = itemWithCategory(30L, "아우터", "Uniqlo", "블루종 자켓");

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(aiTagService.analyzeRegion(
                eq(ootd.getImageUrl()),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        )).thenReturn(new RegionAnalysisResult("블루종 자켓", "아우터", "아우터", BigDecimal.valueOf(0.2)));

        OotdTagAnalyzeResponse response = ootdTagService.analyzeTagArea(OOTD_ID, OWNER_ID, analyzeRequest());

        assertThat(response.labelText()).isEqualTo("블루종 자켓");
        assertThat(response.confidence()).isEqualByComparingTo(BigDecimal.valueOf(0.2));
        assertThat(response.matchedItemIds()).isEmpty();
        verify(itemRepository, never()).findByOwner_IdAndItemStatusOrderByCreatedAtDesc(any(), any());
    }

    @Test
    void analyzeTagArea_returnsAllNullWhenItemNotIdentified() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));
        when(aiTagService.analyzeRegion(
                eq(ootd.getImageUrl()),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        )).thenReturn(RegionAnalysisResult.empty());

        OotdTagAnalyzeResponse response = ootdTagService.analyzeTagArea(OOTD_ID, OWNER_ID, analyzeRequest());

        assertThat(response.labelText()).isNull();
        assertThat(response.categoryLarge()).isNull();
        assertThat(response.categorySmall()).isNull();
        assertThat(response.confidence()).isNull();
        assertThat(response.matchedItemIds()).isEmpty();
    }

    @Test
    void analyzeTagArea_rejectsMissingOotd() {
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ootdTagService.analyzeTagArea(OOTD_ID, OWNER_ID, analyzeRequest()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.OOTD_NOT_FOUND);
    }

    @Test
    void analyzeTagArea_rejectsNonOwner() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        Long strangerId = 999L;
        assertThatThrownBy(() -> ootdTagService.analyzeTagArea(OOTD_ID, strangerId, analyzeRequest()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TagErrorCode.TAG_OWNER_ONLY);
    }

    private OotdTagAnalyzeRequest analyzeRequest() {
        return new OotdTagAnalyzeRequest(
                new BboxRequest(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4))
        );
    }

    private OotdTagCreateRequest request(Long itemId, String status) {
        return new OotdTagCreateRequest(
                itemId,
                new BboxRequest(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)),
                "블루종 자켓",
                status
        );
    }

    private OotdTagBatchRequest batchRequest(List<Long> itemIds) {
        List<OotdTagBatchRequest.TagItem> tagItems = itemIds.stream()
                .map(itemId -> new OotdTagBatchRequest.TagItem(
                        itemId,
                        new BboxRequest(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)),
                        "블루종 자켓"
                ))
                .toList();
        return new OotdTagBatchRequest(tagItems);
    }

    private OotdTagUpdateRequest updateRequest(Long itemId) {
        return new OotdTagUpdateRequest(
                itemId,
                new BboxRequest(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)),
                "블루종 자켓"
        );
    }

    private OotdTag aiTag(Long id, Ootd ootd) {
        OotdTag tag = OotdTag.createAiTag(
                ootd,
                "블루종 자켓",
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(0.2),
                BigDecimal.valueOf(0.3),
                BigDecimal.valueOf(0.4),
                BigDecimal.valueOf(0.9)
        );
        ReflectionTestUtils.setField(tag, "id", id);
        return tag;
    }

    private AiTagResult aiTagResult(String labelText, String categorySmall, BigDecimal confidence) {
        return new AiTagResult(
                labelText,
                "대분류",
                categorySmall,
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(0.2),
                BigDecimal.valueOf(0.3),
                BigDecimal.valueOf(0.4),
                confidence
        );
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Ootd ootd(Long id, User owner) {
        Ootd ootd = instantiate(Ootd.class);
        ReflectionTestUtils.setField(ootd, "id", id);
        ReflectionTestUtils.setField(ootd, "owner", owner);
        return ootd;
    }

    private Item item(Long id) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }

    private Item item(Long id, Long categoryId, String brandName) {
        Item item = item(id);
        ReflectionTestUtils.setField(item, "category", category(categoryId));
        ReflectionTestUtils.setField(item, "brandName", brandName);
        return item;
    }

    private Item itemWithCategory(Long id, String categoryName, String brandName, String itemName) {
        Item item = item(id);
        Category namedCategory = instantiate(Category.class);
        ReflectionTestUtils.setField(namedCategory, "id", id + 1000);
        ReflectionTestUtils.setField(namedCategory, "name", categoryName);
        ReflectionTestUtils.setField(item, "category", namedCategory);
        ReflectionTestUtils.setField(item, "brandName", brandName);
        ReflectionTestUtils.setField(item, "itemName", itemName);
        return item;
    }

    private Category category(Long id) {
        Category category = instantiate(Category.class);
        ReflectionTestUtils.setField(category, "id", id);
        ReflectionTestUtils.setField(category, "name", "카테고리" + id);
        return category;
    }

    private <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
