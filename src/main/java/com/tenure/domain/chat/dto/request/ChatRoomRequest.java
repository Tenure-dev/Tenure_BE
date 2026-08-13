package com.tenure.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequest {

    @NotNull(message = "아이템 id는 필수입니다.")
    private Long itemId;

    // 판매자가 채팅방을 생성할 때 사용. 구매자 입장에서는 null.
    private Long purchaseOfferId;
}
