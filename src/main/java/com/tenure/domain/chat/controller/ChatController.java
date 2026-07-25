package com.tenure.domain.chat.controller;

import com.tenure.domain.chat.dto.request.ChatRoomRequest;
import com.tenure.domain.chat.dto.response.ChatImageUploadResponse;
import com.tenure.domain.chat.dto.response.ChatMessageCursorResponse;
import com.tenure.domain.chat.dto.response.ChatRoomListCursorResponse;
import com.tenure.domain.chat.dto.response.ChatRoomResponse;
import com.tenure.domain.chat.enums.ChatRoomFilterType;
import com.tenure.domain.chat.service.ChatRoomService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @GetMapping
    public BaseResponse<ChatRoomListCursorResponse> chatList(
            @RequestParam(defaultValue = "ALL") ChatRoomFilterType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtCursor,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        ChatRoomListCursorResponse chatRoomListCursorResponse = chatRoomService
                .chatRoomList(currentUserProvider.getCurrentUserId(), type, cursor, createdAtCursor, cursorId, size);

        return BaseResponse.success(chatRoomListCursorResponse);
    }

    @Operation(
            summary = "채팅방 조회",
            description = "채팅방 목록에서 채팅방 클릭 시 호출합니다. 구매자/판매자 여부와 거래 상태에 따른 버튼 정보를 함께 반환합니다."
    )
    @GetMapping("/{chatRoomId}")
    public BaseResponse<ChatRoomResponse> getChatRoom(@PathVariable Long chatRoomId) {

        ChatRoomResponse chatRoomResponse = chatRoomService
                .enterChatroom(currentUserProvider.getCurrentUserId(), chatRoomId);

        return BaseResponse.success(chatRoomResponse);
    }

    @Operation(
            summary = "채팅방 읽음 처리",
            description = "채팅방 접속 시 호출합니다. 읽지 않은 메시지 수를 0으로 초기화합니다."
    )
    @PostMapping("/{chatRoomId}/read")
    public BaseResponse<Void> updateUnreadCount(@PathVariable Long chatRoomId) {
        chatRoomService.updateRead(currentUserProvider.getCurrentUserId(), chatRoomId);
        return BaseResponse.success(null);
    }

    @Operation(
            summary = "채팅 내역 조회",
            description = "채팅방의 메시지 목록을 커서 기반 페이지네이션으로 반환합니다. 최신 메시지부터 내려옵니다. TEXT 타입은 content만, IMAGE 타입은 contentImageUrl만 값이 있고 나머지는 null입니다."
    )
    @GetMapping("/{chatRoomId}/messages")
    public BaseResponse<ChatMessageCursorResponse> getMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        ChatMessageCursorResponse messages = chatRoomService
                .getMessages(currentUserProvider.getCurrentUserId(), chatRoomId, cursor, cursorId, size);

        return BaseResponse.success(messages);
    }

    @Operation(
            summary = "채팅 이미지 업로드",
            description = "채팅방에서 이미지 전송 시 호출합니다. 이미지를 업로드하고 URL을 반환합니다. 반환된 URL을 WebSocket 메시지의 imageUrl 필드에 담아 전송하세요."
    )
    @PostMapping(value = "/{chatRoomId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<ChatImageUploadResponse> uploadChatImage(
            @PathVariable Long chatRoomId,
            @RequestParam("image") MultipartFile image
    ) {
        String url = chatRoomService.uploadImage(currentUserProvider.getCurrentUserId(), chatRoomId, image);
        ChatImageUploadResponse imageUrl = ChatImageUploadResponse.from(url);
        return BaseResponse.success(imageUrl);
    }
}
