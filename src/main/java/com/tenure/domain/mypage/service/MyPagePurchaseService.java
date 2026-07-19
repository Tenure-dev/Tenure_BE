package com.tenure.domain.mypage.service;

import com.tenure.domain.mypage.dto.MyPagePurchaseResponse;
import com.tenure.domain.mypage.enums.MyPagePurchaseTab;
import com.tenure.domain.purchase.entity.PurchaseIntent;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPagePurchaseService {

    private final UserRepository userRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final TradeRepository tradeRepository;

    @Transactional(readOnly = true)
    public PageResponse<MyPagePurchaseResponse> getMyPurchases(
            Long currentUserId,
            MyPagePurchaseTab tab,
            Pageable pageable
    ) {
        validateUserExists(currentUserId);

        MyPagePurchaseTab selectedTab = tab == null ? MyPagePurchaseTab.ALL : tab;

        return switch (selectedTab) {
            case ALL -> getAllPurchases(currentUserId, pageable);
            case OFFERING -> getOfferingPurchases(currentUserId, pageable);
            case TRADING -> getTradingPurchases(currentUserId, pageable);
            case COMPLETED -> getCompletedPurchases(currentUserId, pageable);
        };
    }

    private PageResponse<MyPagePurchaseResponse> getOfferingPurchases(
            Long currentUserId,
            Pageable pageable
    ) {
        Page<PurchaseIntent> intents = purchaseIntentRepository.findMyPurchaseIntents(
                currentUserId,
                List.of(PurchaseIntentStatus.SENT),
                pageable
        );

        Page<PurchaseOffer> offers = purchaseOfferRepository.findMyPurchaseOffers(
                currentUserId,
                List.of(PurchaseOfferStatus.SENT),
                pageable
        );

        List<MyPagePurchaseResponse> responses = new ArrayList<>();
        responses.addAll(intents.getContent().stream()
                .map(MyPagePurchaseResponse::fromPurchaseIntent)
                .toList());
        responses.addAll(offers.getContent().stream()
                .map(MyPagePurchaseResponse::fromPurchaseOffer)
                .toList());

        responses.sort(Comparator.comparing(MyPagePurchaseResponse::createdAt).reversed());

        Page<MyPagePurchaseResponse> page = toPage(
                responses,
                intents.getTotalElements() + offers.getTotalElements(),
                pageable
        );

        return PageResponse.from(page);
    }

    private PageResponse<MyPagePurchaseResponse> getTradingPurchases(
            Long currentUserId,
            Pageable pageable
    ) {
        return PageResponse.from(
                tradeRepository.findAllByBuyer(
                        currentUserId,
                        tradingStatuses(),
                        pageable
                ),
                MyPagePurchaseResponse::fromTrade
        );
    }

    private PageResponse<MyPagePurchaseResponse> getCompletedPurchases(
            Long currentUserId,
            Pageable pageable
    ) {
        return PageResponse.from(
                tradeRepository.findAllByBuyer(
                        currentUserId,
                        completedStatuses(),
                        pageable
                ),
                MyPagePurchaseResponse::fromTrade
        );
    }

    private PageResponse<MyPagePurchaseResponse> getAllPurchases(
            Long currentUserId,
            Pageable pageable
    ) {
        List<MyPagePurchaseResponse> responses = new ArrayList<>();

        responses.addAll(purchaseIntentRepository.findMyPurchaseIntents(
                        currentUserId,
                        List.of(PurchaseIntentStatus.SENT),
                        Pageable.unpaged()
                ).getContent().stream()
                .map(MyPagePurchaseResponse::fromPurchaseIntent)
                .toList());

        responses.addAll(purchaseOfferRepository.findMyPurchaseOffers(
                        currentUserId,
                        List.of(PurchaseOfferStatus.SENT),
                        Pageable.unpaged()
                ).getContent().stream()
                .map(MyPagePurchaseResponse::fromPurchaseOffer)
                .toList());

        responses.addAll(tradeRepository.findAllByBuyer(
                        currentUserId,
                        allTradeStatuses(),
                        Pageable.unpaged()
                ).getContent().stream()
                .map(MyPagePurchaseResponse::fromTrade)
                .toList());

        responses.sort(Comparator.comparing(MyPagePurchaseResponse::createdAt).reversed());

        Page<MyPagePurchaseResponse> page = toPage(responses, responses.size(), pageable);
        return PageResponse.from(page);
    }

    private Page<MyPagePurchaseResponse> toPage(
            List<MyPagePurchaseResponse> responses,
            long totalElements,
            Pageable pageable
    ) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responses.size());

        List<MyPagePurchaseResponse> content = start >= responses.size()
                ? List.of()
                : responses.subList(start, end);

        return new PageImpl<>(content, pageable, totalElements);
    }

    private void validateUserExists(Long currentUserId) {
        if (!userRepository.existsById(currentUserId)) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    private List<TradeStatus> tradingStatuses() {
        return List.of(
                TradeStatus.PAID,
                TradeStatus.SHIPPED,
                TradeStatus.DELIVERED,
                TradeStatus.PURCHASE_CONFIRMED,
                TradeStatus.SETTLED
        );
    }

    private List<TradeStatus> completedStatuses() {
        return List.of(
                TradeStatus.COMPLETED,
                TradeStatus.TRANSFERRED
        );
    }

    private List<TradeStatus> allTradeStatuses() {
        List<TradeStatus> statuses = new ArrayList<>();
        statuses.addAll(tradingStatuses());
        statuses.addAll(completedStatuses());
        return statuses;
    }
}