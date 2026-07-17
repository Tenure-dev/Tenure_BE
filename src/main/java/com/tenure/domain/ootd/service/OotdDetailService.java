package com.tenure.domain.ootd.service;

import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.ootd.dto.OotdDetailResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdReactionType;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdReactionRepository;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.AccountVisibility;
import com.tenure.global.exception.CustomException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OotdDetailService {

    private final OotdRepository ootdRepository;
    private final OotdTagRepository ootdTagRepository;
    private final OotdReactionRepository ootdReactionRepository;
    private final ProductRepository productRepository;
    private final FollowRelationshipRepository followRelationshipRepository;

    @Transactional(readOnly = true)
    public OotdDetailResponse getOotdDetail(Long currentUserId, Long ootdId) {
        Ootd ootd = ootdRepository.findVisibleActiveById(ootdId, currentUserId, OotdPublicationStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(OotdErrorCode.OOTD_NOT_FOUND));

        validateOotdVisibility(ootd.getOwner(), currentUserId);

        List<OotdTag> tags = ootdTagRepository.findConfirmedItemTagsByOotdId(ootdId, TagStatus.CONFIRMED);

        boolean hearted = ootdReactionRepository
                .findReactedOotdIds(currentUserId, List.of(ootdId), OotdReactionType.HEART)
                .contains(ootdId);
        boolean saved = ootdReactionRepository
                .findReactedOotdIds(currentUserId, List.of(ootdId), OotdReactionType.SAVE)
                .contains(ootdId);

        Map<Long, Product> onSaleProductsByItemId = findOnSaleProductsByItemId(tags);

        return OotdDetailResponse.of(ootd, hearted, saved, tags, onSaleProductsByItemId);
    }

    private void validateOotdVisibility(User owner, Long currentUserId) {
        if (owner.getId().equals(currentUserId) || owner.getAccountVisibility() == AccountVisibility.PUBLIC) {
            return;
        }

        boolean acceptedFollower = followRelationshipRepository.existsByFollower_IdAndFollowing_IdAndStatus(
                currentUserId,
                owner.getId(),
                FollowStatus.ACCEPTED
        );
        if (!acceptedFollower) {
            throw new CustomException(OotdErrorCode.PRIVATE_OOTD_ACCESS_DENIED);
        }
    }

    private Map<Long, Product> findOnSaleProductsByItemId(List<OotdTag> tags) {
        Set<Long> itemIds = tags.stream()
                .map(tag -> tag.getItem().getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (itemIds.isEmpty()) {
            return Map.of();
        }

        List<Product> onSaleProducts = productRepository.findByItemIdInAndProductStatus(
                itemIds,
                ProductStatus.ON_SALE
        );
        return onSaleProducts.stream()
                .collect(Collectors.toMap(
                        product -> product.getItem().getId(),
                        Function.identity(),
                        (first, second) -> first
                ));
    }
}
