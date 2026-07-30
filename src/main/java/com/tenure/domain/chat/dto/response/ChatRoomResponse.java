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
    private String opponentUsername;        // 상대방 이름
    private String opponentProfileImage;    // 상대방 프로필 이미지
    private Long itemId;                    // 구매 의사/제안 API 호출용 아이템 ID
    private String itemImageUrl;
    private String brandName;
    private String itemName;
    private ProductStatus productStatus;
    private Integer price;
    private LocalDate lastWornAt;

    private boolean buyer;                   // 현재 유저가 구매자인지
    private Long tradeId;                    // 거래 있으면 tradeId, 없으면 null
    private Long productId;                  // 상품 상세/관리 링크용
    private boolean hasPurchaseIntent;       // SENT 상태 구매 의사 존재 여부 (buyer: 내가 보낸 요청, seller: 받은 요청)
    private boolean hasPurchaseOffer;        // SENT 상태 구매 제안 존재 여부 (buyer: 내가 보낸 제안, seller: 받은 제안)
    private boolean blocked;                 // 어느 한쪽이라도 차단했으면 true
    private boolean opponentExited;          // 상대방이 채팅방을 나갔으면 true


    public static ChatRoomResponse from(
            ChatRoom chatRoom, Item item, Product product,
            Long currentUserId, Long tradeId, boolean hasPurchaseIntent,
            boolean hasPurchaseOffer, boolean isBlocked, boolean isOpponentExited)
    {
        boolean isBuyer = currentUserId.equals(chatRoom.getBuyer().getId());

        String opponentUsername = isBuyer
                ? chatRoom.getSeller().getUsername()
                : chatRoom.getBuyer().getUsername();

        String opponentProfileImage = isBuyer
                ? chatRoom.getSeller().getProfileImageUrl()
                : chatRoom.getBuyer().getProfileImageUrl();

        return new ChatRoomResponse(chatRoom.getId(), opponentUsername, opponentProfileImage,item.getId(),
                item.getRepresentativeImageUrl(), item.getBrandName(), item.getItemName(), product.getProductStatus(), product.getPrice(),
                item.getLastWornAt(), isBuyer, tradeId, product.getId(), hasPurchaseIntent, hasPurchaseOffer, isBlocked, isOpponentExited
        );
    }


}
