package com.lsy.propertymanagementsystem.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager sessionManager;

    public MessageWebSocketHandler(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.addSession((Long) userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 客户端消息目前仅用于心跳，无需处理
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object userId = session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.removeSession((Long) userId, session);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        if (session.isOpen()) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (Exception ignored) {
            }
        }
    }
}
