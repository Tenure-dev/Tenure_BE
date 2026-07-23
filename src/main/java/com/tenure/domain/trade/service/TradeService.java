package com.tenure.domain.trade.service;

import com.tenure.domain.item.repository.ItemHistoryRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.dto.TradeListItemResponse;
import com.tenure.domain.trade.dto.TradeStatusChangeRequest;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeAction;
import com.tenure.domain.trade.enums.TradeActor;
import com.tenure.domain.trade.enums.TradeRole;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeTransition;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.domain.wish.repository.WishRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final ItemHistoryRepository itemHistoryRepository;
    private final WishRepository wishRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public PageResponse<TradeListItemResponse> getTradeList(
            Long currentUserId,
            TradeRole role,
            List<TradeStatus> status,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Trade> trades = findTrades(currentUserId, role, normalizeStatuses(status), pageable);

        return PageResponse.from(trades, TradeListItemResponse::of);
    }

    @Transactional(readOnly = true)
    public TradeDetailResponse getTradeDetail(Long tradeId, Long currentUserId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new CustomException(TradeErrorCode.TRADE_NOT_FOUND));

        TradeViewerMode viewerMode = resolveViewerMode(trade, currentUserId);
        List<TradeAction> availableActions = resolveAvailableActions(viewerMode, trade.getStatus());

        return TradeDetailResponse.of(trade, viewerMode, availableActions);
    }

    @Transactional
    public TradeDetailResponse changeTradeStatus(Long tradeId, Long currentUserId, TradeStatusChangeRequest request) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new CustomException(TradeErrorCode.TRADE_NOT_FOUND));

        TradeViewerMode viewerMode = resolveViewerMode(trade, currentUserId);
        TradeActor actor = TradeActor.from(viewerMode);

        Trade result = applyTransition(trade, actor, request.status(), request);

        List<TradeAction> availableActions = resolveAvailableActions(viewerMode, result.getStatus());
        return TradeDetailResponse.of(result, viewerMode, availableActions);
    }

    /**
     * 배송 완료 후 72시간이 지난 거래를 시스템이 자동 구매확정 처리한다. 스케줄러 전용 진입점이다.
     * 구매자가 먼저 수동으로 확정해 진입 전이(DELIVERED -> PURCHASE_CONFIRMED)의 영향 행이 0건이 되는 경우는
     * 정상적인 경합이므로 예외 없이 false를 반환한다. 자동 연쇄 구간의 실패는 IllegalStateException으로 올라와
     * 여기서 잡히지 않고 트랜잭션을 롤백시킨다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean confirmPurchaseBySystem(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        if (trade == null) {
            return false;
        }

        try {
            applyTransition(trade, TradeActor.SYSTEM, TradeStatus.PURCHASE_CONFIRMED, TradeStatusChangeRequest.empty(TradeStatus.PURCHASE_CONFIRMED));
            return true;
        } catch (CustomException e) {
            if (e.getErrorCode() == TradeErrorCode.TRADE_INVALID_TRANSITION) {
                return false;
            }
            throw e;
        }
    }

    private Trade applyTransition(Trade trade, TradeActor actor, TradeStatus targetStatus, TradeStatusChangeRequest request) {
        TradeTransition transition = TradeTransition.find(trade.getStatus(), targetStatus)
                .orElseThrow(() -> new CustomException(TradeErrorCode.TRADE_INVALID_TRANSITION));

        if (!transition.isAllowed(actor)) {
            throw new CustomException(TradeErrorCode.TRADE_FORBIDDEN_TRANSITION);
        }

        transition.validate(request);

        int updated = transition.applyUpdate(tradeRepository, trade, request);
        if (updated == 0) {
            throw new CustomException(TradeErrorCode.TRADE_INVALID_TRANSITION);
        }

        Trade afterPrimaryUpdate = tradeRepository.findById(trade.getId())
                .orElseThrow(() -> new CustomException(TradeErrorCode.TRADE_NOT_FOUND));

        transition.applySideEffects(new TradeTransition.Context(
                tradeRepository, productRepository, itemRepository, userRepository,
                purchaseOfferRepository, itemHistoryRepository, wishRepository, eventPublisher, afterPrimaryUpdate
        ));

        return tradeRepository.findById(trade.getId())
                .orElseThrow(() -> new CustomException(TradeErrorCode.TRADE_NOT_FOUND));
    }

    private Page<Trade> findTrades(Long currentUserId, TradeRole role, List<TradeStatus> statuses, Pageable pageable) {
        if (role == TradeRole.BUYER) {
            return tradeRepository.findAllByBuyer(currentUserId, statuses, pageable);
        }
        if (role == TradeRole.SELLER) {
            return tradeRepository.findAllBySeller(currentUserId, statuses, pageable);
        }
        return tradeRepository.findAllByParticipant(currentUserId, statuses, pageable);
    }

    private List<TradeStatus> normalizeStatuses(List<TradeStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Arrays.asList(TradeStatus.values());
        }
        if (statuses.contains(TradeStatus.TRANSFERRED)) {
            // TRANSFERRED는 API 계약상 COMPLETED로만 노출되는 내부 상태이므로 필터로 직접 조회할 수 없다.
            throw new CustomException(TradeErrorCode.TRADE_STATUS_FILTER_NOT_ALLOWED);
        }
        if (statuses.contains(TradeStatus.COMPLETED)) {
            // 커밋된 거래는 실제로 TRANSFERRED까지 자동 전이되므로, COMPLETED 필터는 TRANSFERRED 행도 함께 조회해야 한다.
            List<TradeStatus> expanded = new ArrayList<>(statuses);
            expanded.add(TradeStatus.TRANSFERRED);
            return expanded;
        }
        return statuses;
    }

    private TradeViewerMode resolveViewerMode(Trade trade, Long currentUserId) {
        if (trade.getBuyer().getId().equals(currentUserId)) {
            return TradeViewerMode.BUYER;
        }
        if (trade.getSeller().getId().equals(currentUserId)) {
            return TradeViewerMode.SELLER;
        }
        // 거래 ID는 순차 채번되어 403을 반환하면 거래 존재 여부가 노출되므로, 참여자가 아니면 404로 존재 자체를 숨긴다.
        throw new CustomException(TradeErrorCode.TRADE_NOT_FOUND);
    }

    private List<TradeAction> resolveAvailableActions(TradeViewerMode viewerMode, TradeStatus status) {
        return TradeTransition.resolveActions(status, TradeActor.from(viewerMode));
    }
}
