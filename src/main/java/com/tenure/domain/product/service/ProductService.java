package com.tenure.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.product.dto.ProductCreateRequest;
import com.tenure.domain.product.dto.ProductCreateResponse;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.entity.ProductAttachedOotd;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductAttachedOotdRepository;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.global.exception.CustomException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final ProductAttachedOotdRepository productAttachedOotdRepository;
    private final OotdRepository ootdRepository;
    private final OotdTagRepository ootdTagRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProductCreateResponse createProduct(Long itemId, Long currentUserId, ProductCreateRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.ITEM_NOT_FOUND));

        validateOwner(item, currentUserId);
        validateItemStatus(item);
        validateBasicUserPolicy(item.getOwner(), request);
        validateAttachedOotdIds(item, currentUserId, request.attachedOotdIds());

        Product product = Product.create(
                item,
                item.getOwner(),
                request.price(),
                request.shippingFee(),
                request.feePolicy(),
                request.mainImageUrl(),
                writeJsonOrNull(request.measurements()),
                writeJsonOrNull(request.conditionFlags()),
                request.sellerDescription()
        );
        productRepository.save(product);

        List<Ootd> ootds = ootdRepository.findAllById(request.attachedOotdIds());
        Map<Long, Ootd> ootdById = ootds.stream()
                .collect(Collectors.toMap(Ootd::getId, Function.identity()));
        List<ProductAttachedOotd> attachedOotds = request.attachedOotdIds().stream()
                .map(ootdById::get)
                .map(ootd -> ProductAttachedOotd.create(product, ootd))
                .toList();
        productAttachedOotdRepository.saveAll(attachedOotds);

        item.markOnSale();

        return ProductCreateResponse.of(product, item, request.attachedOotdIds());
    }

    private void validateOwner(Item item, Long currentUserId) {
        if (!item.getOwner().getId().equals(currentUserId)) {
            throw new CustomException(ProductErrorCode.PRODUCT_OWNER_ONLY);
        }
    }

    private void validateItemStatus(Item item) {
        if (item.getItemStatus() != ItemStatus.OWNED) {
            throw new CustomException(ProductErrorCode.PRODUCT_ITEM_STATUS_INVALID);
        }
    }

    private void validateBasicUserPolicy(User seller, ProductCreateRequest request) {
        if (seller.getGrade() != UserGrade.BASIC) {
            return;
        }
        if (request.feePolicy() != FeePolicy.SELLER_PAYS) {
            throw new CustomException(ProductErrorCode.BASIC_USER_FEE_POLICY_INVALID);
        }
        if (request.shippingFee() != 0) {
            throw new CustomException(ProductErrorCode.BASIC_USER_SHIPPING_FEE_INVALID);
        }
    }

    private void validateAttachedOotdIds(Item item, Long currentUserId, List<Long> attachedOotdIds) {
        if (new LinkedHashSet<>(attachedOotdIds).size() != attachedOotdIds.size()) {
            throw new CustomException(ProductErrorCode.ATTACHED_OOTD_DUPLICATED);
        }

        long validCount = ootdTagRepository.countValidProductAttachedOotds(
                item.getId(),
                currentUserId,
                attachedOotdIds,
                OotdPublicationStatus.ACTIVE,
                TagStatus.CONFIRMED
        );
        if (validCount != attachedOotdIds.size()) {
            throw new CustomException(ProductErrorCode.ATTACHED_OOTD_INVALID);
        }
    }

    private String writeJsonOrNull(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new CustomException(ProductErrorCode.PRODUCT_JSON_INVALID);
        }
    }
}
