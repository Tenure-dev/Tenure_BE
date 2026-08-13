package com.tenure.domain.chat.dto.response;

import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;

import static com.tenure.domain.product.enums.ProductStatus.ON_SALE;
import static com.tenure.domain.product.enums.ProductStatus.TRADING;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

//채팅방 최초 생성 or 기존 채팅방 조회
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponse {

    private Long chatRoomId;
    private Long opponentUserId;            // 상대방 userId (차단 API 호출용)
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
    private Long purchaseIntentId;           // SENT 상태 구매 의사 ID, 없으면 null (buyer: 내가 보낸 요청, seller: 받은 요청)
    private Long purchaseOfferId;            // SENT 상태 구매 제안 ID, 없으면 null (buyer: 내가 보낸 제안, seller: 받은 제안)
    private boolean blocked;                 // 어느 한쪽이라도 차단했으면 true
    private boolean opponentExited;          // 상대방이 채팅방을 나갔으면 true


    public static ChatRoomResponse from(
            ChatRoom chatRoom, Item item, Product product,
            Long currentUserId, Long tradeId, Long purchaseIntentId,
            Long purchaseOfferId, boolean isBlocked, boolean isOpponentExited,
            Integer offerPrice)
    {
        boolean isBuyer = currentUserId.equals(chatRoom.getBuyer().getId());

        Long opponentUserId = isBuyer
                ? chatRoom.getSeller().getId()
                : chatRoom.getBuyer().getId();

        String opponentUsername = isBuyer
                ? chatRoom.getSeller().getUsername()
                : chatRoom.getBuyer().getUsername();

        String opponentProfileImage = isBuyer
                ? chatRoom.getSeller().getProfileImageUrl()
                : chatRoom.getBuyer().getProfileImageUrl();

        ProductStatus productStatus = product != null ? product.getProductStatus() : null;


        // 판매중/거래중이면 상품 가격, 그 외(미판매 + product없음)는 제안 금액(없으면 null)
        Integer price = (product != null && (product.getProductStatus() == ON_SALE || product.getProductStatus() == TRADING))
                ? product.getPrice()
                : offerPrice;
        Long productId = product != null ? product.getId() : null;

        return new ChatRoomResponse(chatRoom.getId(), opponentUserId, opponentUsername, opponentProfileImage, item.getId(),
                item.getRepresentativeImageUrl(), item.getBrandName(), item.getItemName(), productStatus, price,
                item.getLastWornAt(), isBuyer, tradeId, productId, purchaseIntentId, purchaseOfferId, isBlocked, isOpponentExited
        );
    }


}
