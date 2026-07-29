package com.tenure.global.handler;

import com.tenure.domain.chat.exception.ChatErrorCode;
import com.tenure.domain.chat.repository.ChatRoomMemberRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.security.JwtProvider;
import com.tenure.global.security.SecurityErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final JwtProvider jwtProvider;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static final String chatSubPattern = "/sub/chats/{chatRoomId}";
    private static final String userSubPattern = "/sub/users/{userId}";

    /**
     * Message<?> message: 사용자가 치는 채팅이 아님, 프레임 데이터 자체
     * 클라이언트가 보낸 명령어가 무엇인지 (SUBSCRIBE, CONNECT, SEND 등)
     * 어떤 주소로 향하는지 (destination)
     * 헤더에 토큰이 들어 있는지 (Authorization)
     * (만약 SEND라면) 본문 내용이 무엇인지 (payload)
     */
    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 요청인 경우(웹소켓 연결하기 직전 실행)
        if(StompCommand.CONNECT.equals(accessor.getCommand())) {

            // 유효 토큰 검사
            String authorization = accessor.getFirstNativeHeader("Authorization");
            String jwtToken = (authorization != null && authorization.startsWith("Bearer "))
                    ? authorization.substring(7) : authorization;

            if(jwtToken == null || !jwtProvider.validateToken(jwtToken)) {
                log.warn("[웹소켓] 유효하지 않은 토큰입니다.");
                throw new CustomException(SecurityErrorCode.INVALID_TOKEN);
            }

            Long currentUserId = jwtProvider.getUserId(jwtToken);
            accessor.setUser(new UsernamePasswordAuthenticationToken(currentUserId.toString(),
                    null, Collections.emptyList()));
            accessor.getSessionAttributes().put("userId", currentUserId);
        }
        
        //구독요청일 경우(채팅방 접속)
        if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            log.info("[웹소켓 구독 시도] Destination: {}", accessor.getDestination());

            // 세션에서 userId 조회
            Long currentUserId = (Long) accessor.getSessionAttributes().get("userId");
            if (currentUserId == null) {
                log.warn("[웹소켓 오류] SUBSCRIBE 시점에 사용자 인증 정보가 없습니다.");
                throw new CustomException(CommonErrorCode.FORBIDDEN);
            }

            // 요청 경로
            String destination = accessor.getDestination();

            if(destination == null) {
                return message;
            }

            // /sub/chats/{chatRoomId} 경로가 아닌 경우 권한 검사 생략
            if (antPathMatcher.match(chatSubPattern, destination)) {
                Long chatRoomId = getIdFromDestination(chatSubPattern, destination, "chatRoomId");

                // 해당 채팅방에 접근 권한 검사
                if(!chatRoomMemberRepository.existsByUserIdAndChatRoomIdAndIsExitedFalse(currentUserId, chatRoomId)) {
                    log.warn("[웹소켓] 채팅방 접근 권한이 없습니다. chatRoomId = {}", chatRoomId);
                    throw new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
                }
            } // /sub/users/{userId} 경로 검사
            else if (antPathMatcher.match(userSubPattern, destination)) {
                Long userId = getIdFromDestination(userSubPattern, destination, "userId");

                if(!currentUserId.equals(userId)) {
                    log.warn("[웹소켓] 다른 유저의 알림 채널에 접근 할 수 없습니다. currentUserId = {}, userId = {}", currentUserId, userId);
                    throw new CustomException(CommonErrorCode.FORBIDDEN);
                }
            }
        }
        return message;
    }

    // 구독 경로로부터 pathVariable 추출 매서드
    private Long getIdFromDestination(String pattern, String destination, String variableName) {

        if (destination == null || !antPathMatcher.match(pattern, destination)) {
            log.warn("[웹소켓] 잘못된 구독 경로입니다. destination = {}", destination);
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }

        Map<String, String> variables = antPathMatcher.extractUriTemplateVariables(pattern, destination);
        String id = variables.get(variableName);

        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            log.warn("[웹소켓] 채팅방 ID가 올바른 숫자 형식이 아닙니다. value = {}", id);
            throw new CustomException(CommonErrorCode.INVALID_REQUEST);
        }
    }
}
