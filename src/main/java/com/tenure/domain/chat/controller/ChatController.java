package com.tenure.domain.chat.controller;

import com.tenure.domain.chat.dto.request.ChatRoomRequest;
import com.tenure.domain.chat.dto.response.ChatRoomListCursorResponse;
import com.tenure.domain.chat.dto.response.ChatRoomResponse;
import com.tenure.domain.chat.enums.ChatRoomFilterType;
import com.tenure.domain.chat.service.ChatRoomService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @Operation(
            summary = "채팅방 목록 조회",
            description = "현재 사용자의 채팅방 목록을 커서 기반 페이지네이션으로 반환합니다. type: ALL(전체), BUYING(구매 채팅), SELLING(판매 채팅), UNREAD(읽지 않음)"
    )
    @Parameter(name = "X-USER-ID", description = "개발용 사용자 ID 헤더", in = ParameterIn.HEADER, example = "1")
    @GetMapping
    public BaseResponse<ChatRoomListCursorResponse> chatList(
            @RequestParam(defaultValue = "ALL") ChatRoomFilterType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        ChatRoomListCursorResponse chatRoomListCursorResponse = chatRoomService
                .chatRoomList(currentUserProvider.getCurrentUserId(), type, cursor, cursorId, size);

        return BaseResponse.success(chatRoomListCursorResponse);
    }
}
