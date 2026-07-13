package com.tenure.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.product.dto.ProductCreateRequest;
import com.tenure.domain.product.dto.ProductCreateResponse;
import com.tenure.domain.product.dto.ProductDetailResponse;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.entity.ProductAttachedOotd;
import com.tenure.domain.product.enums.ProductAction;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.enums.ProductViewerMode;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductAttachedOotdRepository;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.global.exception.CustomException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
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
    private final FollowRelationshipRepository followRelationshipRepository;
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
                resolveProductFeeRate(item.getOwner()),
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

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long productId, Long currentUserId) {
        Product product = productRepository.findDetailById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        User seller = product.getSeller();
        ProductViewerMode viewerMode = resolveViewerMode(seller, currentUserId);
        validateHiddenProduct(product, viewerMode);
        validateProductVisibility(seller, currentUserId, viewerMode);

        List<ProductAttachedOotd> representativeOotds =
                productAttachedOotdRepository.findActiveByProductIdOrderByOotdCreatedAtDesc(
                        product.getId(),
                        OotdPublicationStatus.ACTIVE
                );

        return ProductDetailResponse.of(
                product,
                viewerMode,
                resolveAvailableActions(viewerMode, product.getProductStatus()),
                readMapOrEmpty(product.getMeasurements()),
                readListOrEmpty(product.getConditionFlags()),
                representativeOotds
        );
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

    private BigDecimal resolveProductFeeRate(User seller) {
        if (seller.getGrade() == UserGrade.RECORD) {
            return new BigDecimal("0.0300");
        }
        return new BigDecimal("0.0600");
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

    private ProductViewerMode resolveViewerMode(User seller, Long currentUserId) {
        if (seller.getId().equals(currentUserId)) {
            return ProductViewerMode.SELLER;
        }
        return ProductViewerMode.BUYER;
    }

    private void validateProductVisibility(User seller, Long currentUserId, ProductViewerMode viewerMode) {
        if (viewerMode == ProductViewerMode.SELLER || seller.getAccountVisibility() == AccountVisibility.PUBLIC) {
            return;
        }

        boolean acceptedFollower = followRelationshipRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                currentUserId,
                seller.getId(),
                FollowStatus.ACCEPTED
        );
        if (!acceptedFollower) {
            throw new CustomException(ProductErrorCode.PRIVATE_PRODUCT_ACCESS_DENIED);
        }
    }

    private void validateHiddenProduct(Product product, ProductViewerMode viewerMode) {
        if (product.getProductStatus() == ProductStatus.HIDDEN && viewerMode != ProductViewerMode.SELLER) {
            throw new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    private List<ProductAction> resolveAvailableActions(ProductViewerMode viewerMode, ProductStatus productStatus) {
        if (viewerMode == ProductViewerMode.SELLER) {
            if (productStatus == ProductStatus.ON_SALE) {
                return List.of(ProductAction.EDIT, ProductAction.DELETE, ProductAction.MARK_SOLD);
            }
            return List.of();
        }

        if (productStatus == ProductStatus.ON_SALE) {
            return List.of(ProductAction.CHAT, ProductAction.PURCHASE, ProductAction.SHARE, ProductAction.REPORT);
        }
        return List.of(ProductAction.SHARE, ProductAction.REPORT);
    }

    private Map<String, Object> readMapOrEmpty(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new CustomException(ProductErrorCode.PRODUCT_DETAIL_DATA_INVALID);
        }
    }

    private List<String> readListOrEmpty(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new CustomException(ProductErrorCode.PRODUCT_DETAIL_DATA_INVALID);
        }
    }
}
