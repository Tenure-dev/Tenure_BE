package com.tenure.domain.mypage.service;

import com.tenure.domain.mypage.dto.MyPageSaleResponse;
import com.tenure.domain.mypage.enums.MyPageSalesTab;
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
public class MyPageSaleService {

    private final UserRepository userRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final TradeRepository tradeRepository;

    @Transactional(readOnly = true)
    public PageResponse<MyPageSaleResponse> getMySales(
            Long currentUserId,
            MyPageSalesTab tab,
            Pageable pageable
    ) {
        validateUserExists(currentUserId);

        MyPageSalesTab selectedTab = tab == null ? MyPageSalesTab.ALL : tab;

        return switch (selectedTab) {
            case ALL -> getAllSales(currentUserId, pageable);
            case OFFER_RECEIVED -> getOfferReceivedSales(currentUserId, pageable);
            case TRADING -> getTradingSales(currentUserId, pageable);
            case COMPLETED -> getCompletedSales(currentUserId, pageable);
        };
    }

    private PageResponse<MyPageSaleResponse> getOfferReceivedSales(
            Long currentUserId,
            Pageable pageable
    ) {
        Page<PurchaseIntent> intents = purchaseIntentRepository.findMySaleIntents(
                currentUserId,
                List.of(PurchaseIntentStatus.SENT),
                pageable
        );

        Page<PurchaseOffer> offers = purchaseOfferRepository.findMySaleOffers(
                currentUserId,
                List.of(PurchaseOfferStatus.SENT),
                pageable
        );

        List<MyPageSaleResponse> responses = new ArrayList<>();
        responses.addAll(intents.getContent().stream()
                .map(MyPageSaleResponse::fromPurchaseIntent)
                .toList());
        responses.addAll(offers.getContent().stream()
                .map(MyPageSaleResponse::fromPurchaseOffer)
                .toList());

        responses.sort(Comparator.comparing(MyPageSaleResponse::createdAt).reversed());

        Page<MyPageSaleResponse> page = toPage(
                responses,
                intents.getTotalElements() + offers.getTotalElements(),
                pageable
        );

        return PageResponse.from(page);
    }

    private PageResponse<MyPageSaleResponse> getTradingSales(
            Long currentUserId,
            Pageable pageable
    ) {
        return PageResponse.from(
                tradeRepository.findAllBySeller(
                        currentUserId,
                        tradingStatuses(),
                        pageable
                ),
                MyPageSaleResponse::fromTrade
        );
    }

    private PageResponse<MyPageSaleResponse> getCompletedSales(
            Long currentUserId,
            Pageable pageable
    ) {
        return PageResponse.from(
                tradeRepository.findAllBySeller(
                        currentUserId,
                        completedStatuses(),
                        pageable
                ),
                MyPageSaleResponse::fromTrade
        );
    }

    private PageResponse<MyPageSaleResponse> getAllSales(
            Long currentUserId,
            Pageable pageable
    ) {
        List<MyPageSaleResponse> responses = new ArrayList<>();

        responses.addAll(purchaseIntentRepository.findMySaleIntents(
                        currentUserId,
                        List.of(PurchaseIntentStatus.SENT),
                        Pageable.unpaged()
                ).getContent().stream()
                .map(MyPageSaleResponse::fromPurchaseIntent)
                .toList());

        responses.addAll(purchaseOfferRepository.findMySaleOffers(
                        currentUserId,
                        List.of(PurchaseOfferStatus.SENT),
                        Pageable.unpaged()
                ).getContent().stream()
                .map(MyPageSaleResponse::fromPurchaseOffer)
                .toList());

        responses.addAll(tradeRepository.findAllBySeller(
                        currentUserId,
                        allTradeStatuses(),
                        Pageable.unpaged()
                ).getContent().stream()
                .map(MyPageSaleResponse::fromTrade)
                .toList());

        responses.sort(Comparator.comparing(MyPageSaleResponse::createdAt).reversed());

        Page<MyPageSaleResponse> page = toPage(responses, responses.size(), pageable);
        return PageResponse.from(page);
    }

    private Page<MyPageSaleResponse> toPage(
            List<MyPageSaleResponse> responses,
            long totalElements,
            Pageable pageable
    ) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responses.size());

        List<MyPageSaleResponse> content = start >= responses.size()
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