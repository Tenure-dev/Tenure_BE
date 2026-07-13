package com.tenure.domain.purchase.service;

import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.exception.PurchaseIntentErrorCode;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.global.exception.CustomException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseIntentExpirationProcessor {

    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final PurchaseIntentExpirationService purchaseIntentExpirationService;

    @Transactional
    public boolean expire(Long intentId, LocalDateTime now) {
        Long productId = purchaseIntentRepository.findProductIdById(intentId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_FOUND));
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PRODUCT_NOT_FOUND));
        itemRepository.findByIdForUpdate(product.getItem().getId())
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.ITEM_NOT_FOUND));
        PurchaseIntent intent = purchaseIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new CustomException(PurchaseIntentErrorCode.PURCHASE_INTENT_NOT_FOUND));

        return purchaseIntentExpirationService.expireIfSentAndExpired(intent, now);
    }
}
