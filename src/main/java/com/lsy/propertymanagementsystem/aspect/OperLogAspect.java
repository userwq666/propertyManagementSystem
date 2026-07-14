package com.lsy.propertymanagementsystem.aspect;

import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import com.lsy.propertymanagementsystem.module.system.entity.SysOperLog;
import com.lsy.propertymanagementsystem.module.system.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class OperLogAspect {

    @Autowired
    private SysOperLogService operLogService;

    @Pointcut("execution(* com.lsy.propertymanagementsystem.controller..*.*(..)) " +
            "&& !execution(* com.lsy.propertymanagementsystem.controller.SysUserController.login(..)) " +
            "&& !execution(* com.lsy.propertymanagementsystem.controller.SysUserController.logout(..)) " +
            "&& !execution(* com.lsy.propertymanagementsystem.controller.SysUserController.getUserInfo(..))")
    public void operLogPointcut() {
    }

    @Around("operLogPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();

        try {
            saveOperLog(point);
        } catch (Exception ignored) {
        }

        return result;
    }

    private void saveOperLog(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        String className = point.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        String operModule = className.replace("Controller", "");
        String operType = getOperType(methodName);
        String operDesc = operModule + " - " + operType;

        String username = null;
        String operIp = null;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operIp = request.getRemoteAddr();

            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    username = JwtUtils.getUsername(token);
                } catch (Exception ignored) {
                }
            }
        }

        SysOperLog operLog = new SysOperLog();
        operLog.setUserName(username);
        operLog.setOperModule(operModule);
        operLog.setOperType(operType);
        operLog.setOperIp(operIp);
        operLog.setOperDesc(operDesc);
        operLogService.addOperLog(operLog);
    }

    private String getOperType(String methodName) {
        if (methodName.startsWith("add") || methodName.startsWith("save") || methodName.startsWith("generate")) {
            return "新增";
        } else if (methodName.startsWith("update") || methodName.startsWith("edit") || methodName.startsWith("confirm") || methodName.startsWith("reset") || methodName.startsWith("assign") || methodName.startsWith("mark")) {
            return "编辑";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "删除";
        } else {
            return "查询";
        }
    }
}