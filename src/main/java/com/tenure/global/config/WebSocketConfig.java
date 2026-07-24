package com.tenure.global.config;

import com.tenure.global.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/sub"); // 클라이언트가 메시지를 받을 때 구독하는 경로 (Broker 역할)
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 서버로 메시지를 보낼 때 붙일 prefix (Controller 역할)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") //클라이언트가 웹소켓 연결을 맺기 위해 접속하는 엔드포인트
                .setAllowedOriginPatterns("*") // CORS 허용 TODO: 배포 전 프론트 도메인으로 교체 예정
                .withSockJS(); // SockJS 지원 (웹소켓 미지원 브라우저 대응)
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // 인터셉터 등록
    }
}
