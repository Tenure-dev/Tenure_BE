package com.tenure.domain.wish.service;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.domain.wish.dto.WishCreateResponse;
import com.tenure.domain.wish.dto.WishDeleteResponse;
import com.tenure.domain.wish.entity.Wish;
import com.tenure.domain.wish.exception.WishErrorCode;
import com.tenure.domain.wish.repository.WishRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public WishCreateResponse createWish(Long currentUserId, Long itemId) {
        User user = findUser(currentUserId);
        Item item = findItem(itemId);

        validateNotAlreadyWished(currentUserId, itemId);

        Wish wish = Wish.create(user, item);
        Wish savedWish = wishRepository.save(wish);

        item.increaseWishCount();

        return WishCreateResponse.from(savedWish);
    }

    @Transactional
    public WishDeleteResponse deleteWish(Long currentUserId, Long itemId) {
        User user = findUser(currentUserId);
        Item item = findItem(itemId);
        Wish wish = findWish(currentUserId, itemId);

        wishRepository.delete(wish);
        item.decreaseWishCount();

        return new WishDeleteResponse(
                item.getId(),
                user.getId(),
                item.getWishCount()
        );
    }

    private Wish findWish(Long userId, Long itemId) {
        return wishRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new CustomException(WishErrorCode.WISH_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(WishErrorCode.USER_NOT_FOUND));
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(WishErrorCode.ITEM_NOT_FOUND));
    }

    private void validateNotAlreadyWished(Long userId, Long itemId) {
        if (wishRepository.existsByUserIdAndItemId(userId, itemId)) {
            throw new CustomException(WishErrorCode.WISH_ALREADY_EXISTS);
        }
    }
}
