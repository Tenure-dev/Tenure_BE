package com.tenure.domain.mypage.service;

import com.tenure.domain.follow.enums.FollowStatus;
import com.tenure.domain.follow.repository.FollowRelationshipRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.mypage.dto.MyPageResponse;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.domain.wish.repository.WishRepository;
import com.tenure.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final OotdRepository ootdRepository;
    private final ItemRepository itemRepository;
    private final WishRepository wishRepository;
    private final FollowRelationshipRepository followRelationshipRepository;

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        long feedCount = ootdRepository.countByOwner_IdAndPublicationStatus(
                currentUserId,
                OotdPublicationStatus.ACTIVE
        );
        long itemCount = itemRepository.countByOwner_Id(currentUserId);
        long wishCount = wishRepository.countByUser_Id(currentUserId);
        long followerCount = followRelationshipRepository.countByFollowing_IdAndStatus(
                currentUserId,
                FollowStatus.ACCEPTED
        );

        return MyPageResponse.of(
                user,
                feedCount,
                itemCount,
                wishCount,
                followerCount
        );
    }
}
