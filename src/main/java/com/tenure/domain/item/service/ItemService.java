package com.tenure.domain.item.service;

import com.tenure.domain.item.dto.*;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.entity.ItemHistory;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.exception.ItemErrorCode;
import com.tenure.domain.item.repository.CategoryRepository;
import com.tenure.domain.item.repository.ItemHistoryRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import com.tenure.global.storage.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tenure.domain.item.dto.ItemOotdCandidateResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.tag.enums.TagStatus;
import com.tenure.global.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private static final int LARGE_CATEGORY_DEPTH = 1; //상위 카테고리 depth
    private static final int SMALL_CATEGORY_DEPTH = 2; //상세 카테고리 depth
    private final ProductRepository productRepository;

    private final ItemRepository itemRepository; //새 Item 저장
    private final CategoryRepository categoryRepository; //categoryLarge/categorySmall로 Category 찾기
    private final UserRepository userRepository; //currentUserID로 User 찾기
    private final ItemHistoryRepository itemHistoryRepository;

    private static final String AI_PENDING_CATEGORY_NAME = "AI 분류 대기";
    private static final int DETAIL_CATEGORY_DEPTH = 2;

    private final OotdTagRepository ootdTagRepository;
    private final ImageStorageService imageStorageService;

    @Transactional
    public ItemCreateResponse createItem(Long currentUserId, ItemCreateRequest request) {
        validateFirstOwnedAt(request.firstOwnedAt());

        User owner = findUser(currentUserId); //로그인한 사용자 찾기
        Category largeCategory = findLargeCategory(request.categoryLarge()); //요청으로 들어온 카테고리명을 실제 Category 엔티티로 변경
        Category smallCategory = findSmallCategory(request.categorySmall(), largeCategory);

        Item item = Item.create( //Item을 생성해서 저장
                owner,
                smallCategory,
                request.brandName(),
                request.itemName(),
                request.wearingTarget(),
                request.sizeSystem(),
                request.sizeValue(),
                request.firstOwnedAt(),
                request.representativeImageUrl()
        );

        Item savedItem = itemRepository.save(item);
        itemHistoryRepository.save(
                ItemHistory.ofFirstRegistration(savedItem, owner, resolveFirstRegisteredStartedAt(savedItem))
        );

        return ItemCreateResponse.of(savedItem); //저장 결과 응답DTO로 돌려줌
    }

    private void validateFirstOwnedAt(LocalDate firstOwnedAt) {
        if (firstOwnedAt != null && firstOwnedAt.isAfter(LocalDate.now())) {
            throw new CustomException(ItemErrorCode.FIRST_OWNED_AT_IN_FUTURE);
        }
    }

    // items.first_owned_at은 사용자가 입력한 최초 보유일(DATE, nullable)이라 이걸 우선 쓰고,
    // 없으면 아이템 등록 시각(created_at)을 소유 기간 시작 시각으로 대신 쓴다.
    private LocalDateTime resolveFirstRegisteredStartedAt(Item item) {
        if (item.getFirstOwnedAt() != null) {
            return item.getFirstOwnedAt().atStartOfDay();
        }
        return item.getCreatedAt();
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemListResponse> getMyItems(
            Long currentUserId,
            String query,
            ItemStatus itemStatus,
            Pageable pageable
    ) {
        return PageResponse.from(
                itemRepository.findMyItems(currentUserId, normalizeQuery(query), itemStatus, pageable),
                ItemListResponse::from
        );
    }

    private String normalizeQuery(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }
        return query.trim();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ItemErrorCode.USER_NOT_FOUND));
    }

    private Category findLargeCategory(String categoryLarge) {
        return categoryRepository
                .findByNameAndDepthAndIsActiveTrue(categoryLarge, LARGE_CATEGORY_DEPTH)
                .orElseThrow(() -> new CustomException(ItemErrorCode.CATEGORY_NOT_FOUND));
    }

    private Category findSmallCategory(String categorySmall, Category parentCategory) {
        return categoryRepository
                .findByNameAndParentAndDepthAndIsActiveTrue(
                        categorySmall,
                        parentCategory,
                        SMALL_CATEGORY_DEPTH
                )
                .orElseThrow(() -> new CustomException(ItemErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ItemDetailResponse getItemDetail(Long currentUserId, Long itemId) {
        Item item = findItem(itemId);
        validateItemAccess(item, currentUserId);

        Product product = productRepository.findFirstByItemIdAndProductStatusInOrderByCreatedAtDesc(
                itemId,
                List.of(ProductStatus.ON_SALE, ProductStatus.TRADING, ProductStatus.SOLD)
        ).orElse(null);

        return ItemDetailResponse.from(item, product);
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ItemErrorCode.ITEM_NOT_FOUND));
    }

    private void validateItemAccess(Item item, Long currentUserId) {
        if (item.getOwner().getId().equals(currentUserId)) {
            return;
        }

        if (isTaggedInVisibleOotd(item.getId())) {
            return;
        }

        if (isOnSaleProduct(item.getId())) {
            return;
        }

        throw new CustomException(ItemErrorCode.ITEM_ACCESS_DENIED);
    }

    private void validateItemOwner(Item item, Long currentUserId) {
        if (!item.getOwner().getId().equals(currentUserId)) {
            throw new CustomException(ItemErrorCode.ITEM_ACCESS_DENIED);
        }
    }

    private boolean isTaggedInVisibleOotd(Long itemId) {
        return ootdTagRepository.existsVisibleTagByItemId(
                itemId,
                OotdPublicationStatus.ACTIVE,
                TagStatus.CONFIRMED
        );
    }

    private boolean isOnSaleProduct(Long itemId) {
        return productRepository.existsByItemIdAndProductStatus(
                itemId,
                ProductStatus.ON_SALE
        );
    }

    @Transactional
    public ItemOfferSettingResponse updatePurchaseOfferSetting(
            Long currentUserId,
            Long itemId,
            ItemOfferSettingRequest request
    ) {
        Item item = findItem(itemId);
        validateItemOwner(item, currentUserId);

        item.changePurchaseOfferEnabled(request.purchaseOfferEnabled());

        return ItemOfferSettingResponse.from(item);
    }

    @Transactional
    public ItemUpdateResponse updateItem(
            Long currentUserId,
            Long itemId,
            ItemUpdateRequest request
    ) {
        Item item = findItem(itemId);
        validateItemOwner(item, currentUserId);

        Category largeCategory = findLargeCategory(request.categoryLarge());
        Category smallCategory = findSmallCategory(request.categorySmall(), largeCategory);

        item.updateInfo(
                smallCategory,
                request.brandName(),
                request.itemName(),
                request.wearingTarget(),
                request.sizeSystem(),
                request.sizeValue(),
                request.firstOwnedAt(),
                request.representativeImageUrl()
        );

        return ItemUpdateResponse.from(item);
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemHistoryResponse> getItemHistories(
            Long currentUserId,
            Long itemId,
            Pageable pageable
    ) {
        Item item = findItem(itemId);
        validateItemAccess(item, currentUserId);

        return PageResponse.from(
                itemHistoryRepository.findByItemIdOrderByStartedAtDesc(itemId, pageable),
                ItemHistoryResponse::from
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemHistoryOotdResponse> getItemHistoryOotds(
            Long currentUserId,
            Long itemId,
            Long historyId,
            Pageable pageable
    ) {
        Item item = findItem(itemId);
        validateItemAccess(item, currentUserId);

        ItemHistory history = itemHistoryRepository.findByIdAndItemId(historyId, itemId)
                .orElseThrow(() -> new CustomException(ItemErrorCode.ITEM_HISTORY_NOT_FOUND));

        Page<Ootd> ootds;

        if (history.getEndedAt() == null) {
            ootds = ootdTagRepository.findCurrentItemHistoryOotds(
                    itemId,
                    history.getOwner().getId(),
                    history.getStartedAt(),
                    OotdPublicationStatus.ACTIVE,
                    TagStatus.CONFIRMED,
                    pageable
            );
        } else {
            ootds = ootdTagRepository.findClosedItemHistoryOotds(
                    itemId,
                    history.getOwner().getId(),
                    history.getStartedAt(),
                    history.getEndedAt(),
                    OotdPublicationStatus.ACTIVE,
                    TagStatus.CONFIRMED,
                    pageable
            );
        }

        return PageResponse.from(ootds, ItemHistoryOotdResponse::from);
    }

    @Transactional
    public ItemTagDraftCreateResponse createTagDraftItem(
            Long currentUserId,
            ItemTagDraftCreateRequest request
    ) {
        validateFirstOwnedAt(request.firstOwnedAt());

        User owner = findUser(currentUserId);
        Category category = findAiPendingCategory();

        Item item = Item.create(
                owner,
                category,
                request.brandName(),
                request.itemName(),
                request.wearingTarget(),
                null,
                null,
                request.firstOwnedAt(),
                null
        );

        Item savedItem = itemRepository.save(item);
        itemHistoryRepository.save(
                ItemHistory.ofFirstRegistration(savedItem, owner, resolveFirstRegisteredStartedAt(savedItem))
        );

        return ItemTagDraftCreateResponse.of(savedItem);
    }

    private Category findAiPendingCategory() {
        return categoryRepository.findByNameAndDepthAndIsActiveTrue(AI_PENDING_CATEGORY_NAME, DETAIL_CATEGORY_DEPTH)
                .orElseThrow(() -> new CustomException(ItemErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ItemFrequentlyWornTogetherResponse> getFrequentlyWornTogetherItems(
            Long currentUserId,
            Long itemId
    ) {
        Item item = findItem(itemId); //기준 아이템이 존재하는지 확인
        validateItemAccess(item, currentUserId); //상세 조회와 같은 권한 기준 적용

        return ootdTagRepository.findFrequentlyWornTogetherItems( //같은 OOTD에 함께 태그된 아이템을 count 순으로 조회
                        itemId,
                        OotdPublicationStatus.ACTIVE,
                        TagStatus.CONFIRMED,
                        PageRequest.of(0, 3) //최대 3개만
                )
                .stream()
                .map(result -> new ItemFrequentlyWornTogetherResponse(
                        result.getItemId(),
                        result.getBrandName(),
                        result.getItemName(),
                        result.getRepresentativeImageUrl(),
                        result.getTogetherCount()
                ))
                .toList();
    }

    @Transactional
    public String uploadItemImage(MultipartFile image) {
        return imageStorageService.store(image, "items");
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemOotdCandidateResponse> getItemOotdCandidates(
            Long currentUserId,
            Long itemId,
            Pageable pageable
    ) {
        Item item = findItem(itemId);
        validateItemOwner(item, currentUserId);

        Page<Ootd> ootds = ootdTagRepository.findItemOotdCandidates(
                itemId,
                currentUserId,
                TagStatus.CONFIRMED,
                OotdPublicationStatus.ACTIVE,
                pageable
        );

        return PageResponse.from(ootds, ItemOotdCandidateResponse::from);
    }
}
