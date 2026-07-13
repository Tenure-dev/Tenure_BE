package com.tenure.domain.purchase.service;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.dto.PurchaseIntentCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseIntentCreateResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentDetailResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentDetailResponse.ViewerRole;
import com.tenure.domain.purchase.dto.PurchaseIntentReceivedListResponse;
import com.tenure.domain.purchase.dto.PurchaseIntentSentListResponse;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.exception.PurchaseIntentErrorCode;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
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
public class PurchaseIntentService {

    private static final int RESPONSE_HOURS = 24;

    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;
    private final FollowRelationshipRepository followRelationshipRepository;
    private final TradeRepository tradeRepository;
    private final PurchaseIntentExpirationService purchaseIntentExpirationService;

    @Transactional
    public PurchaseIntentCreateResponse createPurchaseIntent(
            Long productId,
            Long currentUserId,
            PurchaseIntentCreateRequest request
    ) {
        validateAgreement(request.agreement());

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PRODUCT_NOT_FOUND));
        Item item = itemRepository.findByIdForUpdate(product.getItem().getId())
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.ITEM_NOT_FOUND));

        User buyer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.BUYER_NOT_FOUND));
        validatePurchasableProduct(product, currentUserId);
        validateProductVisibility(product.getSeller(), currentUserId);
        expireSentIntentsIfNeeded(product.getId(), currentUserId);

        DeliveryAddress deliveryAddress = deliveryAddressRepository
                .findByIdAndUserId(request.deliveryAddressId(), currentUserId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        FeeAmounts amounts = calculateFeeAmounts(product);
        PurchaseIntent intent = PurchaseIntent.create(
                product,
                buyer,
                product.getSeller(),
                deliveryAddress,
                product.getPrice(),
                product.getFeePolicy(),
                product.getFeeRate(),
                product.getShippingFee(),
                amounts.buyerServiceFee(),
                amounts.sellerServiceFee(),
                amounts.totalPaymentAmount(),
                amounts.sellerSettlementAmount(),
                createMockPaymentAuthorizationId(),
                request.paymentMethodId(),
                LocalDateTime.now().plusHours(RESPONSE_HOURS)
        );

        purchaseIntentRepository.save(intent);
        return PurchaseIntentCreateResponse.from(intent);
    }

    @Transactional
    public PurchaseIntentSentListResponse getSentPurchaseIntents(
            Long currentUserId,
            List<PurchaseIntentStatus> statuses,
            OffsetDateTime cursorCreatedAt,
            Long cursorIntentId,
            Integer size
    ) {
        validateCursor(cursorCreatedAt, cursorIntentId);
        int pageSize = normalizeSize(size);
        LocalDateTime now = LocalDateTime.now();
        expireSentIntentsForBuyer(currentUserId, now);

        List<PurchaseIntentStatus> normalizedStatuses = normalizeStatuses(statuses);
        List<PurchaseIntent> fetched = purchaseIntentRepository.findSentListByBuyerWithCursor(
                currentUserId,
                normalizedStatuses,
                cursorCreatedAt == null ? null : cursorCreatedAt.toLocalDateTime(),
                cursorIntentId,
                PageRequest.of(0, pageSize + 1)
        );
        boolean hasNext = fetched.size() > pageSize;
        List<PurchaseIntent> pageItems = hasNext ? fetched.subList(0, pageSize) : fetched;
        Map<Long, Long> tradeIdByIntentId = findTradeIds(pageItems);
        return PurchaseIntentSentListResponse.of(pageItems, tradeIdByIntentId, now, hasNext);
    }

    @Transactional
    public PurchaseIntentReceivedListResponse getReceivedPurchaseIntents(
            Long currentUserId,
            List<PurchaseIntentStatus> statuses,
            OffsetDateTime cursorCreatedAt,
            Long cursorIntentId,
            Integer size
    ) {
        validateCursor(cursorCreatedAt, cursorIntentId);
        int pageSize = normalizeSize(size);
        LocalDateTime now = LocalDateTime.now();
        expireSentIntentsForSeller(currentUserId, now);

        List<PurchaseIntentStatus> normalizedStatuses = normalizeStatuses(statuses);
        List<PurchaseIntent> fetched = purchaseIntentRepository.findReceivedListBySellerWithCursor(
                currentUserId,
                normalizedStatuses,
                cursorCreatedAt == null ? null : cursorCreatedAt.toLocalDateTime(),
                cursorIntentId,
                PageRequest.of(0, pageSize + 1)
        );
        boolean hasNext = fetched.size() > pageSize;
        List<PurchaseIntent> pageItems = hasNext ? fetched.subList(0, pageSize) : fetched;
        Map<Long, Long> tradeIdByIntentId = findTradeIds(pageItems);
        return PurchaseIntentReceivedListResponse.of(pageItems, tradeIdByIntentId, now, hasNext);
    }

    @Transactional
    public PurchaseIntentDetailResponse getPurchaseIntentDetail(Long intentId, Long currentUserId) {
        Long productId = purchaseIntentRepository.findProductIdById(intentId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_FOUND));
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PRODUCT_NOT_FOUND));
        itemRepository.findByIdForUpdate(product.getItem().getId())
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.ITEM_NOT_FOUND));
        PurchaseIntent intent = purchaseIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_FOUND));

        ViewerRole viewerRole = resolveViewerRole(intent, currentUserId);
        LocalDateTime now = LocalDateTime.now();
        purchaseIntentExpirationService.expireIfSentAndExpired(intent, now);
        return PurchaseIntentDetailResponse.from(intent, viewerRole, now);
    }

    private void validateAgreement(Boolean agreement) {
        if (!Boolean.TRUE.equals(agreement)) {
            throw new CustomException(PurchaseIntentErrorCode.AGREEMENT_REQUIRED);
        }
    }

    private void validatePurchasableProduct(Product product, Long currentUserId) {
        if (product.getSeller().getId().equals(currentUserId)) {
            throw new CustomException(PurchaseIntentErrorCode.SELF_PURCHASE_NOT_ALLOWED);
        }
        if (product.getProductStatus() != ProductStatus.ON_SALE) {
            throw new CustomException(PurchaseIntentErrorCode.PRODUCT_NOT_ON_SALE);
        }
    }

    private void validateProductVisibility(User seller, Long currentUserId) {
        if (seller.getAccountVisibility() == AccountVisibility.PUBLIC) {
            return;
        }

        boolean acceptedFollower = followRelationshipRepository.existsByFollowerIdAndFollowingIdAndStatus(
                currentUserId,
                seller.getId(),
                FollowStatus.ACCEPTED
        );
        if (!acceptedFollower) {
            throw new CustomException(PurchaseIntentErrorCode.PRIVATE_PRODUCT_ACCESS_DENIED);
        }
    }

    private void expireSentIntentsIfNeeded(Long productId, Long currentUserId) {
        LocalDateTime now = LocalDateTime.now();
        List<PurchaseIntent> sentIntents = purchaseIntentRepository.findSentByProductIdAndBuyerIdForUpdate(
                productId,
                currentUserId,
                PurchaseIntentStatus.SENT
        );

        for (PurchaseIntent intent : sentIntents) {
            if (purchaseIntentExpirationService.expireIfSentAndExpired(intent, now)) {
                continue;
            }
            throw new CustomException(
                    PurchaseIntentErrorCode.ACTIVE_INTENT_EXISTS,
                    Map.of("existingIntentId", intent.getId())
            );
        }
    }

    private FeeAmounts calculateFeeAmounts(Product product) {
        int productPrice = product.getPrice();
        int shippingFee = product.getShippingFee();
        int totalFee = BigDecimal.valueOf(productPrice)
                .multiply(product.getFeeRate())
                .setScale(0, RoundingMode.DOWN)
                .intValue();

        int buyerServiceFee = 0;
        int sellerServiceFee = 0;
        if (product.getFeePolicy() == FeePolicy.BUYER_PAYS) {
            buyerServiceFee = totalFee;
        } else if (product.getFeePolicy() == FeePolicy.SPLIT) {
            buyerServiceFee = Math.floorDiv(totalFee, 2);
            sellerServiceFee = totalFee - buyerServiceFee;
        } else {
            sellerServiceFee = totalFee;
        }

        int totalPaymentAmount = productPrice + shippingFee + buyerServiceFee;
        int sellerSettlementAmount = productPrice + shippingFee - sellerServiceFee;
        return new FeeAmounts(
                buyerServiceFee,
                sellerServiceFee,
                totalPaymentAmount,
                sellerSettlementAmount
        );
    }

    private String createMockPaymentAuthorizationId() {
        return "mock_auth_" + UUID.randomUUID();
    }

    private void validateCursor(OffsetDateTime cursorCreatedAt, Long cursorIntentId) {
        if ((cursorCreatedAt == null) != (cursorIntentId == null)) {
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

    private List<PurchaseIntentStatus> normalizeStatuses(List<PurchaseIntentStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Arrays.asList(PurchaseIntentStatus.values());
        }
        return statuses;
    }

    private void expireSentIntentsForBuyer(Long currentUserId, LocalDateTime now) {
        List<PurchaseIntent> expiredSentIntents = purchaseIntentRepository.findExpiredSentByBuyerIdForUpdate(
                currentUserId,
                PurchaseIntentStatus.SENT,
                now
        );
        for (PurchaseIntent intent : expiredSentIntents) {
            purchaseIntentExpirationService.expireIfSentAndExpired(intent, now);
        }
    }

    private void expireSentIntentsForSeller(Long currentUserId, LocalDateTime now) {
        List<PurchaseIntent> expiredSentIntents = purchaseIntentRepository.findExpiredSentBySellerIdForUpdate(
                currentUserId,
                PurchaseIntentStatus.SENT,
                now
        );
        for (PurchaseIntent intent : expiredSentIntents) {
            purchaseIntentExpirationService.expireIfSentAndExpired(intent, now);
        }
    }

    private Map<Long, Long> findTradeIds(List<PurchaseIntent> intents) {
        List<Long> acceptedIntentIds = intents.stream()
                .filter(intent -> intent.getStatus() == PurchaseIntentStatus.ACCEPTED)
                .map(PurchaseIntent::getId)
                .toList();
        if (acceptedIntentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return tradeRepository.findAllBySourceTypeAndSourceIdIn(
                        TradeSourceType.PURCHASE_INTENT,
                        acceptedIntentIds
                ).stream()
                .filter(trade -> trade.getSourceId() != null)
                .collect(Collectors.toMap(
                        Trade::getSourceId,
                        Trade::getId,
                        (left, right) -> left
                ));
    }

    private ViewerRole resolveViewerRole(PurchaseIntent intent, Long currentUserId) {
        if (intent.getBuyer().getId().equals(currentUserId)) {
            return ViewerRole.BUYER;
        }
        if (intent.getSeller().getId().equals(currentUserId)) {
            return ViewerRole.SELLER;
        }
        throw new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_ACCESS_DENIED);
    }

    private record FeeAmounts(
            int buyerServiceFee,
            int sellerServiceFee,
            int totalPaymentAmount,
            int sellerSettlementAmount
    ) {
    }
}
