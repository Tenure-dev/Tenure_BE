package com.tenure.domain.tag.service;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.ai.AiTagResult;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.tag.dto.OotdTagCreateRequest;
import com.tenure.domain.tag.dto.OotdTagResponse;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.exception.TagErrorCode;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.global.config.AiTagProperties;
import com.tenure.global.exception.CustomException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OotdTagService {

    private final OotdRepository ootdRepository;
    private final ItemRepository itemRepository;
    private final OotdTagRepository ootdTagRepository;
    private final AiTagProperties aiTagProperties;

    @Transactional
    public void saveAiTags(Long ootdId, List<AiTagResult> results) {
        Ootd ootd = ootdRepository.findById(ootdId).orElse(null);
        if (ootd == null) {
            log.warn("AI 태그 저장 실패 - OOTD를 찾을 수 없습니다. ootdId={}", ootdId);
            return;
        }

        List<OotdTag> tags = results.stream()
                .filter(this::meetsConfidenceThreshold)
                .filter(this::hasValidBbox)
                .map(result -> OotdTag.createAiTag(
                        ootd,
                        result.labelText(),
                        result.bboxX(),
                        result.bboxY(),
                        result.bboxWidth(),
                        result.bboxHeight(),
                        result.confidence()
                ))
                .toList();

        ootdTagRepository.saveAll(tags);
        log.info("AI 태그 저장 완료 - ootdId={}, 저장된 태그 수={}/{}", ootdId, tags.size(), results.size());
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

        return OotdTagResponse.of(tag);
    }

    private boolean meetsConfidenceThreshold(AiTagResult result) {
        return result.confidence() != null
                && result.confidence().compareTo(aiTagProperties.confidenceThreshold()) >= 0;
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
