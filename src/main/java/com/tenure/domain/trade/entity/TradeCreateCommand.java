package com.tenure.domain.trade.entity;

import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.user.entity.User;

public record TradeCreateCommand(
        TradeSourceType sourceType,
        Long sourceId,
        Item item,
        Product product,
        User buyer,
        User seller,
        Integer itemPrice,
        Integer paymentAmount,
        Integer buyerShippingFee,
        Integer buyerServiceFee,
        Integer sellerServiceFee,
        Integer settlementAmount,
        String paymentMethodId,
        String paymentAuthorizationId,
        String deliveryReceiverName,
        String deliveryPhone,
        String deliveryAddressLine1,
        String deliveryAddressLine2,
        String deliveryPostalCode,
        String deliveryRequestNote
) {
}
