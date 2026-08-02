package com.lsy.propertymanagementsystem.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageWebSocketHandler messageWebSocketHandler;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    public WebSocketConfig(MessageWebSocketHandler messageWebSocketHandler,
                           AuthHandshakeInterceptor authHandshakeInterceptor) {
        this.messageWebSocketHandler = messageWebSocketHandler;
        this.authHandshakeInterceptor = authHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageWebSocketHandler, "/ws")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
