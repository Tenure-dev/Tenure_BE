package com.tenure.domain.item.service;

import com.tenure.domain.item.dto.ItemCreateRequest;
import com.tenure.domain.item.dto.ItemCreateResponse;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.exception.ItemErrorCode;
import com.tenure.domain.item.repository.CategoryRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.domain.item.dto.ItemListResponse;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.global.response.PageResponse;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private static final int LARGE_CATEGORY_DEPTH = 1; //상위 카테고리 depth
    private static final int SMALL_CATEGORY_DEPTH = 2; //상세 카테고리 depth

    private final ItemRepository itemRepository; //새 Item 저장
    private final CategoryRepository categoryRepository; //categoryLarge/categorySmall로 Category 찾기
    private final UserRepository userRepository; //currentUserID로 User 찾기

    @Transactional
    public ItemCreateResponse createItem(Long currentUserId, ItemCreateRequest request) {
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
        return ItemCreateResponse.of(savedItem); //저장 결과 응답DTO로 돌려줌
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
}
