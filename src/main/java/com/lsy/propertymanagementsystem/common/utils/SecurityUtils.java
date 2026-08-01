package com.lsy.propertymanagementsystem.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全工具类 - 获取当前登录用户信息
 */
public class SecurityUtils {

    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        return attrs.getRequest();
    }

    public static Long getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return null;
        return (Long) request.getAttribute("userId");
    }

    public static String getRoleKey() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return null;
        Object attr = request.getAttribute("roleKey");
        return attr instanceof String ? (String) attr : null;
    }

    public static boolean isOwner() {
        return "owner".equals(getRoleKey());
    }

    public static boolean hasPermission(String permission) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> permission.equals(a.getAuthority()));
    }
}
