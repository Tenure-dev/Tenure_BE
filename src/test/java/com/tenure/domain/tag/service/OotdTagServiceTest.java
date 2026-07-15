package com.tenure.domain.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.ai.AiTagResult;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.tag.dto.request.OotdTagCreateRequest;
import com.tenure.domain.tag.dto.request.OotdTagCreateRequest.BboxRequest;
import com.tenure.domain.tag.dto.response.OotdTagResponse;
import com.tenure.domain.tag.dto.request.OotdTagUpdateRequest;
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

    private OotdTagService ootdTagService;

    @BeforeEach
    void setUp() {
        AiTagProperties aiTagProperties = new AiTagProperties(BigDecimal.valueOf(0.6));
        ootdTagService = new OotdTagService(ootdRepository, itemRepository, ootdTagRepository, aiTagProperties);
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
    void updateTag_updatesContentWithoutChangingStatus() {
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
        assertThat(aiTag.getStatus()).isEqualTo(TagStatus.AUTO_UNCONFIRMED);
        assertThat(aiTag.getItem()).isEqualTo(item);
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
    void saveAiTags_filtersOutResultsBelowConfidenceThreshold() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        List<AiTagResult> results = List.of(
                aiTagResult("블루종 자켓", BigDecimal.valueOf(0.92)),
                aiTagResult("청바지", BigDecimal.valueOf(0.55)),
                aiTagResult("운동화", BigDecimal.valueOf(0.60))
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
        });
        assertThat(savedTags).extracting(OotdTag::getLabelText)
                .containsExactlyInAnyOrder("블루종 자켓", "운동화");
    }

    @Test
    void saveAiTags_filtersOutResultsWithOutOfRangeBbox() {
        User owner = user(OWNER_ID);
        Ootd ootd = ootd(OOTD_ID, owner);

        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.of(ootd));

        AiTagResult validResult = aiTagResult("블루종 자켓", BigDecimal.valueOf(0.92));
        AiTagResult outOfRangeResult = new AiTagResult(
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
    void saveAiTags_doesNothingWhenOotdNotFound() {
        when(ootdRepository.findById(OOTD_ID)).thenReturn(Optional.empty());

        ootdTagService.saveAiTags(OOTD_ID, List.of(aiTagResult("블루종 자켓", BigDecimal.valueOf(0.9))));

        verify(ootdTagRepository, never()).saveAll(anyList());
    }

    private OotdTagCreateRequest request(Long itemId, String status) {
        return new OotdTagCreateRequest(
                itemId,
                new BboxRequest(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.4)),
                "블루종 자켓",
                status
        );
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

    private AiTagResult aiTagResult(String labelText, BigDecimal confidence) {
        return new AiTagResult(
                labelText,
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
