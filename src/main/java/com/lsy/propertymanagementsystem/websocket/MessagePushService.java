package com.lsy.propertymanagementsystem.websocket;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessagePushService {

    private static final Logger log = LoggerFactory.getLogger(MessagePushService.class);
    private final WebSocketSessionManager sessionManager;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

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

    public void pushToRole(String type, String title, String content, Long refId, String... roleKeys) {
        if (roleKeys == null || roleKeys.length == 0) {
            return;
        }
        Set<Long> userIds = new HashSet<>();
        for (String roleKey : roleKeys) {
            if (roleKey == null) {
                continue;
            }
            SysRoleDomain role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRoleDomain>()
                    .eq(SysRoleDomain::getRoleKey, roleKey));
            if (role == null) {
                continue;
            }
            userIds.addAll(sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRoleDomain>()
                            .eq(SysUserRoleDomain::getRoleId, role.getId()))
                    .stream()
                    .map(SysUserRoleDomain::getUserId)
                    .collect(Collectors.toList()));
        }
        for (Long userId : userIds) {
            pushToUser(userId, type, title, content, refId);
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
