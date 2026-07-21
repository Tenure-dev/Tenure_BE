package com.tenure.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket @Valid 검증 실패 시 해당 클라이언트에게 에러 전송
    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(MethodArgumentNotValidException e,
                                          SimpMessageHeaderAccessor headerAccessor) {
        log.warn("[웹소켓] 메시지 요청 검증 실패: {}", e.getMessage());
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        sendError(headerAccessor.getSessionId(), errorMessage);
    }

    // 서비스 비즈니스 예외 시 해당 클라이언트에게 에러 전송
    @MessageExceptionHandler(CustomException.class)
    public void handleCustomException(CustomException e,
                                      SimpMessageHeaderAccessor headerAccessor) {
        log.warn("[웹소켓] 비즈니스 예외 발생: {}", e.getMessage());
        sendError(headerAccessor.getSessionId(), e.getMessage());
    }

    private void sendError(String sessionId, String errorMessage) {
        // 해당 세션(클라이언트)에게만 에러 전송
        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                errorMessage,
                Map.of(SimpMessageHeaderAccessor.SESSION_ID_HEADER, sessionId)
        );
    }
}
