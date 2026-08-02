package com.lsy.propertymanagementsystem.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessagePushService {

    private static final Logger log = LoggerFactory.getLogger(MessagePushService.class);
    private final WebSocketSessionManager sessionManager;

    public MessagePushService(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void pushToUser(Long userId, String type, String title, String content, Long refId) {
        if (userId == null) {
            return;
        }
        try {
            String json = buildMessage(type, title, content, refId);
            sessionManager.sendToUser(userId, json);
        } catch (Exception e) {
            log.warn("消息推送失败 type={} userId={}", type, userId, e);
        }
    }

    public void broadcast(String type, String title, String content, Long refId) {
        try {
            String json = buildMessage(type, title, content, refId);
            sessionManager.broadcast(json);
        } catch (Exception e) {
            log.warn("广播失败 type={}", type, e);
        }
    }

    private String buildMessage(String type, String title, String content, Long refId) {
        return "{\"type\":\"" + escape(type) + "\",\"title\":\"" + escape(title)
                + "\",\"content\":\"" + escape(content) + "\",\"refId\":" + refId
                + ",\"time\":" + System.currentTimeMillis() + "}";
    }

    private String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
