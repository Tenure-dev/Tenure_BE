package com.tenure.domain.chat.controller;

import com.tenure.domain.chat.dto.request.ChatRoomRequest;
import com.tenure.domain.chat.dto.response.ChatRoomResponse;
import com.tenure.domain.chat.service.ChatRoomService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final CurrentUserProvider currentUserProvider;
    private final ChatRoomService chatRoomService;

    @Operation(
            summary = "채팅방 생성 또는 조회",
            description = "아이템 상세 페이지에서 채팅하기 버튼 클릭 시 호출합니다. 이미 채팅방이 존재하면 기존 채팅방을 반환합니다."
    )
    @Parameter(
            name = "X-USER-ID",
            description = "개발용 사용자 ID 헤더",
            in = ParameterIn.HEADER,
            example = "1"
    )
    @PostMapping
    public BaseResponse<ChatRoomResponse> findOrCreateChatRoom(@RequestBody @Valid ChatRoomRequest chatRoomRequest) {

        ChatRoomResponse chatRoomResponse = chatRoomService
                .findOrCreateChatRoom(currentUserProvider.getCurrentUserId(), chatRoomRequest.getItemId());

        return BaseResponse.success(chatRoomResponse);

    }
}
