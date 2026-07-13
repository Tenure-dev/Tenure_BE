package com.tenure.domain.purchase.service;

import com.tenure.domain.purchase.dto.PurchaseOfferSentListResponse;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOfferService {

    private final PurchaseOfferRepository purchaseOfferRepository;
    private final TradeRepository tradeRepository;
    private final PurchaseOfferExpirationService purchaseOfferExpirationService;

    @Transactional
    public PurchaseOfferSentListResponse getSentPurchaseOffers(
            Long currentUserId,
            List<PurchaseOfferStatus> statuses,
            OffsetDateTime cursorCreatedAt,
            Long cursorOfferId,
            Integer size
    ) {
        validateCursor(cursorCreatedAt, cursorOfferId);
        int pageSize = normalizeSize(size);
        LocalDateTime now = LocalDateTime.now();
        expireSentOffersForProposer(currentUserId, now);

        List<PurchaseOfferStatus> normalizedStatuses = normalizeStatuses(statuses);
        List<PurchaseOffer> fetched = purchaseOfferRepository.findSentListByProposerWithCursor(
                currentUserId,
                normalizedStatuses,
                cursorCreatedAt == null ? null : cursorCreatedAt.toLocalDateTime(),
                cursorOfferId,
                PageRequest.of(0, pageSize + 1)
        );
        boolean hasNext = fetched.size() > pageSize;
        List<PurchaseOffer> pageItems = hasNext ? fetched.subList(0, pageSize) : fetched;
        Map<Long, Long> tradeIdByOfferId = findTradeIds(pageItems);
        return PurchaseOfferSentListResponse.of(pageItems, tradeIdByOfferId, now, hasNext);
    }

    private void validateCursor(OffsetDateTime cursorCreatedAt, Long cursorOfferId) {
        if ((cursorCreatedAt == null) != (cursorOfferId == null)) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return 20;
        }
        if (size < 1 || size > 50) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
        return size;
    }

    private List<PurchaseOfferStatus> normalizeStatuses(List<PurchaseOfferStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Arrays.asList(PurchaseOfferStatus.values());
        }
        return statuses;
    }

    private void expireSentOffersForProposer(Long currentUserId, LocalDateTime now) {
        List<PurchaseOffer> expiredSentOffers = purchaseOfferRepository.findExpiredSentByProposerIdForUpdate(
                currentUserId,
                PurchaseOfferStatus.SENT,
                now
        );
        for (PurchaseOffer offer : expiredSentOffers) {
            purchaseOfferExpirationService.expireIfSentAndExpired(offer, now);
        }
    }

    private Map<Long, Long> findTradeIds(List<PurchaseOffer> offers) {
        List<Long> acceptedOfferIds = offers.stream()
                .filter(offer -> offer.getStatus() == PurchaseOfferStatus.ACCEPTED)
                .map(PurchaseOffer::getId)
                .toList();
        if (acceptedOfferIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return tradeRepository.findAllBySourceTypeAndSourceIdIn(
                        TradeSourceType.PURCHASE_OFFER,
                        acceptedOfferIds
                ).stream()
                .filter(trade -> trade.getSourceId() != null)
                .collect(Collectors.toMap(
                        Trade::getSourceId,
                        Trade::getId,
                        (left, right) -> left
                ));
    }
}
