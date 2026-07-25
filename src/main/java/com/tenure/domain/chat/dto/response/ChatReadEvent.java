package com.tenure.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatReadEvent {
    private final String type = "READ";
    private Long readUserId; // 읽은 사람 id
}
