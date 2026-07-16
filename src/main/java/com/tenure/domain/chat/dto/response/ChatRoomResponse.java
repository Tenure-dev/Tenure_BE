package com.tenure.domain.chat.dto.response;

import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponse {

    private Long chatRoomId;
    private String opponentUsername; //상대방 이름
    private String itemImageUrl;
    private String brandName;
    private String itemName;
    private ProductStatus productStatus;
    private Integer price;
    private LocalDate lastWornAt;


    public static ChatRoomResponse from(ChatRoom chatRoom, User owner, Item item, Product product) {
        return new ChatRoomResponse(chatRoom.getId(), owner.getUsername(), item.getRepresentativeImageUrl(),
                item.getBrandName(), item.getItemName(), product.getProductStatus(),
                product.getPrice(), item.getLastWornAt());
    }


}
