package com.tenure.domain.chat.dto.request;

import com.tenure.domain.chat.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    @NotNull(message = "messageType은 필수입니다.")
    private MessageType messageType;
    private String content; // 텍스트인 경우
    private String imageUrl; // 이미지인 경우

}
