package com.tenure.domain.purchase.service;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateResponse;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOfferService {

    private static final int RESPONSE_HOURS = 24;
    private static final int MINIMUM_OFFER_PRICE = 1000;

    private final ItemRepository itemRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;

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
}
