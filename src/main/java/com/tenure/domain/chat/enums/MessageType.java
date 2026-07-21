package com.tenure.domain.chat.enums;

public enum MessageType {
    TEXT, IMAGE;

    public String toLastMessagePreview() {
        return this == IMAGE ? "사진을 보냈습니다." : null;
    }
}
