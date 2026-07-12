package com.tenure.domain.trade.service;

import com.tenure.domain.trade.dto.TradeDetailResponse;
import com.tenure.domain.trade.dto.TradeListItemResponse;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeAction;
import com.tenure.domain.trade.enums.TradeRole;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.enums.TradeViewerMode;
import com.tenure.domain.trade.exception.TradeErrorCode;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    @Transactional(readOnly = true)
    public PageResponse<TradeListItemResponse> getTradeList(
            Long currentUserId,
            TradeRole role,
            TradeStatus status,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Trade> trades = findTrades(currentUserId, role, status, pageable);

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

    private Page<Trade> findTrades(Long currentUserId, TradeRole role, TradeStatus status, Pageable pageable) {
        if (role == TradeRole.BUYER) {
            return tradeRepository.findAllByBuyer(currentUserId, status, pageable);
        }
        if (role == TradeRole.SELLER) {
            return tradeRepository.findAllBySeller(currentUserId, status, pageable);
        }
        return tradeRepository.findAllByParticipant(currentUserId, status, pageable);
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
        if (status == TradeStatus.PAID && viewerMode == TradeViewerMode.SELLER) {
            return List.of(TradeAction.REGISTER_SHIPMENT);
        }
        if (status == TradeStatus.DELIVERED && viewerMode == TradeViewerMode.BUYER) {
            return List.of(TradeAction.CONFIRM_PURCHASE);
        }
        return List.of();
    }
}
