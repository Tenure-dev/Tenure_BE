package com.tenure.domain.chat.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatImageUploadResponse {

    private String imageUrl;

    public static ChatImageUploadResponse from(String imageUrl) {
        return new ChatImageUploadResponse(imageUrl);
    }
}
