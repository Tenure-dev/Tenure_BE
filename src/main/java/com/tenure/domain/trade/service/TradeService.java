package com.tenure.domain.trade.service;

import com.tenure.domain.trade.dto.TradeListItemResponse;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeRole;
import com.tenure.domain.trade.enums.TradeStatus;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.global.response.PageResponse;
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

    private Page<Trade> findTrades(Long currentUserId, TradeRole role, TradeStatus status, Pageable pageable) {
        if (role == TradeRole.BUYER) {
            return tradeRepository.findAllByBuyer(currentUserId, status, pageable);
        }
        if (role == TradeRole.SELLER) {
            return tradeRepository.findAllBySeller(currentUserId, status, pageable);
        }
        return tradeRepository.findAllByParticipant(currentUserId, status, pageable);
    }
}
