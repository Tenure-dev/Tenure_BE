package com.tenure.domain.chat.dto.response;

import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

//채팅방 최초 생성 or 기존 채팅방 조회
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponse {

    private Long chatRoomId;
    private String opponentUsername; //상대방 이름
    private String opponentProfileImage; // 상대방 프로필 이미지
    private String itemImageUrl;
    private String brandName;
    private String itemName;
    private ProductStatus productStatus;
    private Integer price;
    private LocalDate lastWornAt;

    private boolean isBuyer;          // 현재 유저가 구매자인지
    private Long tradeId;             // 거래 있으면 tradeId, 없으면 null
    private Long productId;           // 상품 상세/관리 링크용
    private boolean isBlocked;        // 어느 한쪽이라도 차단했으면 true
    private boolean isOpponentExited; // 상대방이 채팅방을 나갔으면 true


    public static ChatRoomResponse from(ChatRoom chatRoom, Item item, Product product, Long currentUserId, Long tradeId, boolean isBlocked, boolean isOpponentExited) {
        boolean isBuyer = currentUserId.equals(chatRoom.getBuyer().getId());

        String opponentUsername = isBuyer
                ? chatRoom.getSeller().getUsername()
                : chatRoom.getBuyer().getUsername();

        String opponentProfileImage = isBuyer
                ? chatRoom.getSeller().getProfileImageUrl()
                : chatRoom.getBuyer().getProfileImageUrl();

        return new ChatRoomResponse(chatRoom.getId(), opponentUsername, opponentProfileImage, item.getRepresentativeImageUrl(),
                item.getBrandName(), item.getItemName(), product.getProductStatus(), product.getPrice(),
                item.getLastWornAt(), isBuyer, tradeId, product.getId(), isBlocked, isOpponentExited
        );
    }


}
