package com.tenure.domain.chat.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatImageUploadResponse {

    private List<String> imageUrls;

    public static ChatImageUploadResponse from(List<String> imageUrls) {
        return new ChatImageUploadResponse(imageUrls);
    }
}
