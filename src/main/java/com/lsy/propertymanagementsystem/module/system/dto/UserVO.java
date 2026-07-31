package com.lsy.propertymanagementsystem.module.system.dto;

import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    // 用户ID
    private Long id;
    // 用户名
    private String username;
    // 真实姓名
    private String realName;
    // 手机号
    private String phone;
    // 头像
    private String avatar;
    // 状态
    private UserStatus status;
    // 创建时间
    private LocalDateTime createTime;
    // 角色ID
    private Long roleId;
    // 角色名称
    private String roleName;
    // 角色列表
    private List<String> roles;
    // 权限列表
    private List<String> permissions;       //permission是权限的字符串表示
}
