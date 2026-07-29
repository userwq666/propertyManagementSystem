package com.lsy.propertymanagementsystem.common.utils;

import com.lsy.propertymanagementsystem.module.system.enums.UserType;
import jakarta.servlet.http.HttpServletRequest;
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

    public static Integer getUserTypeValue() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return null;
        Object attr = request.getAttribute("userType");
        return attr instanceof Integer ? (Integer) attr : null;
    }

    public static boolean isOwner() {
        Integer type = getUserTypeValue();
        return type != null && type == UserType.OWNER.getValue();
    }

    public static boolean isSuperAdmin() {
        Integer type = getUserTypeValue();
        return type != null && type == UserType.SUPER_ADMIN.getValue();
    }

    public static boolean isPropertyAdmin() {
        Integer type = getUserTypeValue();
        return type != null && type == UserType.PROPERTY_ADMIN.getValue();
    }
}
