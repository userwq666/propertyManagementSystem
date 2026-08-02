package com.lsy.propertymanagementsystem.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class WebSocketSessionManager {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSessionManager.class);
    private final Map<Long, CopyOnWriteArraySet<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
    }

    public void removeSession(Long userId, WebSocketSession session) {
        CopyOnWriteArraySet<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }

    public boolean sendToUser(Long userId, String message) {
        CopyOnWriteArraySet<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return false;
        }
        boolean sent = false;
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                    sent = true;
                } catch (IOException e) {
                    log.warn("发送消息失败 userId={}", userId, e);
                }
            }
        }
        return sent;
    }

    public int onlineCount() {
        return userSessions.size();
    }

    public void broadcast(String message) {
        TextMessage textMessage = new TextMessage(message);
        userSessions.values().forEach(sessions -> sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    log.warn("广播消息失败", e);
                }
            }
        }));
    }
}
