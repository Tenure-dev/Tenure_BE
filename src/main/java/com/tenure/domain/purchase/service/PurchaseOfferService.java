package com.tenure.domain.purchase.service;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.dto.PurchaseOfferCancelResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse.ViewerRole;
import com.tenure.domain.purchase.dto.PurchaseOfferReceivedListResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferRejectResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferSentListResponse;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOfferService {

    private static final int RESPONSE_HOURS = 24;
    private static final int MINIMUM_OFFER_PRICE = 1000;

    private final ItemRepository itemRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final PurchaseOfferExpirationService purchaseOfferExpirationService;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;

    @Transactional
    public PurchaseOfferCreateResponse createPurchaseOffer(
            Long itemId,
            Long currentUserId,
            PurchaseOfferCreateRequest request
    ) {
        validateAgreement(request.agreement());
        validateOfferPrice(request.offerPrice());

        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.ITEM_NOT_FOUND));
        User proposer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.PROPOSER_NOT_FOUND));

        validateOfferableItem(item, currentUserId);
        validateOfferNotUsed(itemId, currentUserId);

        DeliveryAddress deliveryAddress = deliveryAddressRepository
                .findByIdAndUser_Id(request.deliveryAddressId(), currentUserId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        BigDecimal feeRate = resolveOfferFeeRate(item.getOwner());
        int shippingFee = resolveOwnerShippingFee(item.getOwner());
        int serviceFee = calculateServiceFee(request.offerPrice(), feeRate);
        int totalPaymentAmount = request.offerPrice() + shippingFee + serviceFee;
        int ownerSettlementAmount = request.offerPrice() + shippingFee;

        PurchaseOffer offer = PurchaseOffer.create(
                item,
                proposer,
                item.getOwner(),
                deliveryAddress,
                request.offerPrice(),
                shippingFee,
                serviceFee,
                feeRate,
                totalPaymentAmount,
                ownerSettlementAmount,
                createMockPaymentAuthorizationId(),
                request.paymentMethodId(),
                now.plusHours(RESPONSE_HOURS)
        );
        purchaseOfferRepository.save(offer);
        return PurchaseOfferCreateResponse.from(offer, now);
    }

    @Transactional
    public PurchaseOfferDetailResponse getPurchaseOfferDetail(Long offerId, Long currentUserId) {
        PurchaseOffer offer = findOfferWithItemLock(offerId);
        ViewerRole viewerRole = resolveViewerRole(offer, currentUserId);
        LocalDateTime now = LocalDateTime.now();
        purchaseOfferExpirationService.expireIfSentAndExpired(offer, now);
        return PurchaseOfferDetailResponse.from(offer, viewerRole, now);
    }

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

        List<PurchaseOffer> fetched = purchaseOfferRepository.findSentListByProposerWithCursor(
                currentUserId,
                normalizeStatuses(statuses),
                cursorCreatedAt == null ? null : cursorCreatedAt.toLocalDateTime(),
                cursorOfferId,
                PageRequest.of(0, pageSize + 1)
        );
        boolean hasNext = fetched.size() > pageSize;
        List<PurchaseOffer> pageItems = hasNext ? fetched.subList(0, pageSize) : fetched;
        return PurchaseOfferSentListResponse.of(pageItems, findTradeIds(pageItems), now, hasNext);
    }

    @Transactional
    public PurchaseOfferReceivedListResponse getReceivedPurchaseOffers(
            Long currentUserId,
            List<PurchaseOfferStatus> statuses,
            OffsetDateTime cursorCreatedAt,
            Long cursorOfferId,
            Integer size
    ) {
        validateCursor(cursorCreatedAt, cursorOfferId);
        int pageSize = normalizeSize(size);
        LocalDateTime now = LocalDateTime.now();
        expireSentOffersForOwner(currentUserId, now);

        List<PurchaseOffer> fetched = purchaseOfferRepository.findReceivedListByOwnerWithCursor(
                currentUserId,
                normalizeStatuses(statuses),
                cursorCreatedAt == null ? null : cursorCreatedAt.toLocalDateTime(),
                cursorOfferId,
                PageRequest.of(0, pageSize + 1)
        );
        boolean hasNext = fetched.size() > pageSize;
        List<PurchaseOffer> pageItems = hasNext ? fetched.subList(0, pageSize) : fetched;
        return PurchaseOfferReceivedListResponse.of(pageItems, findTradeIds(pageItems), now, hasNext);
    }

    @Transactional(noRollbackFor = CustomException.class)
    public PurchaseOfferRejectResponse rejectPurchaseOffer(Long offerId, Long currentUserId) {
        PurchaseOffer offer = findOfferWithItemLock(offerId);
        validateOwner(offer, currentUserId);
        validateSentOrExpire(offer, LocalDateTime.now());
        offer.rejectAndReleaseAuthorization();
        return PurchaseOfferRejectResponse.from(offer, LocalDateTime.now());
    }

    @Transactional(noRollbackFor = CustomException.class)
    public PurchaseOfferCancelResponse cancelPurchaseOffer(Long offerId, Long currentUserId) {
        PurchaseOffer offer = findOfferWithItemLock(offerId);
        validateProposer(offer, currentUserId);
        validateSentOrExpire(offer, LocalDateTime.now());
        offer.cancelAndReleaseAuthorization();
        return PurchaseOfferCancelResponse.from(offer, LocalDateTime.now());
    }

    private PurchaseOffer findOfferWithItemLock(Long offerId) {
        Long itemId = purchaseOfferRepository.findItemIdById(offerId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_FOUND));
        itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.ITEM_NOT_FOUND));
        return purchaseOfferRepository.findByIdForUpdate(offerId)
                .orElseThrow(() -> new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_FOUND));
    }

    private void validateSentOrExpire(PurchaseOffer offer, LocalDateTime now) {
        if (purchaseOfferExpirationService.expireIfSentAndExpired(offer, now)) {
            throw new CustomException(PurchaseOfferErrorCode.PURCHASE_REQUEST_EXPIRED);
        }
        if (!offer.isSent()) {
            throw new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_SENT);
        }
    }

    private void validateAgreement(Boolean agreement) {
        if (!Boolean.TRUE.equals(agreement)) {
            throw new CustomException(PurchaseOfferErrorCode.AGREEMENT_REQUIRED);
        }
    }

    private void validateOfferPrice(Integer offerPrice) {
        if (offerPrice == null || offerPrice < MINIMUM_OFFER_PRICE) {
            throw new CustomException(PurchaseOfferErrorCode.OFFER_PRICE_TOO_LOW);
        }
    }

    private void validateOfferableItem(Item item, Long currentUserId) {
        if (item.getOwner().getId().equals(currentUserId)) {
            throw new CustomException(PurchaseOfferErrorCode.SELF_OFFER_NOT_ALLOWED);
        }
        if (item.getItemStatus() != ItemStatus.OWNED) {
            throw new CustomException(PurchaseOfferErrorCode.ITEM_NOT_OWNED);
        }
        if (!Boolean.TRUE.equals(item.getPurchaseOfferEnabled())) {
            throw new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_DISABLED);
        }
    }

    private void validateOfferNotUsed(Long itemId, Long currentUserId) {
        purchaseOfferRepository.findByItemIdAndProposerIdForUpdate(itemId, currentUserId)
                .ifPresent(existingOffer -> {
                    throw new CustomException(
                            PurchaseOfferErrorCode.PURCHASE_OFFER_ALREADY_USED,
                            Map.of(
                                    "existingOfferId", existingOffer.getId(),
                                    "existingOfferStatus", existingOffer.getStatus()
                            )
                    );
                });
    }

    private BigDecimal resolveOfferFeeRate(User owner) {
        if (owner.getGrade() == UserGrade.RECORD) {
            return new BigDecimal("0.0300");
        }
        return new BigDecimal("0.0600");
    }

    private int resolveOwnerShippingFee(User owner) {
        return owner.getDefaultShippingFee() == null ? 0 : owner.getDefaultShippingFee();
    }

    private int calculateServiceFee(Integer offerPrice, BigDecimal feeRate) {
        return BigDecimal.valueOf(offerPrice)
                .multiply(feeRate)
                .setScale(0, RoundingMode.DOWN)
                .intValue();
    }

    private String createMockPaymentAuthorizationId() {
        return "mock_offer_auth_" + UUID.randomUUID();
    }

    private ViewerRole resolveViewerRole(PurchaseOffer offer, Long currentUserId) {
        if (offer.getProposer().getId().equals(currentUserId)) {
            return ViewerRole.PROPOSER;
        }
        if (offer.getOwner().getId().equals(currentUserId)) {
            return ViewerRole.OWNER;
        }
        throw new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
    }

    private void validateOwner(PurchaseOffer offer, Long currentUserId) {
        if (!offer.getOwner().getId().equals(currentUserId)) {
            throw new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
        }
    }

    private void validateProposer(PurchaseOffer offer, Long currentUserId) {
        if (!offer.getProposer().getId().equals(currentUserId)) {
            throw new CustomException(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
        }
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

    private void expireSentOffersForOwner(Long currentUserId, LocalDateTime now) {
        List<PurchaseOffer> expiredSentOffers = purchaseOfferRepository.findExpiredSentByOwnerIdForUpdate(
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
