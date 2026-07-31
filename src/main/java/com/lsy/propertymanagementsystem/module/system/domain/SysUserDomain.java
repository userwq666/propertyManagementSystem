package com.lsy.propertymanagementsystem.module.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")                  // 用户表sys_user
public class SysUserDomain {
    @TableId(type = IdType.AUTO)        // 主键自增
    // 用户ID
    private Long id;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 真实姓名
    private String realName;
    // 手机号
    private String phone;
    // 头像
    private String avatar;
    // 用户状态
    private UserStatus status;
    // 创建时间
    @TableField(fill = FieldFill.INSERT)             // 插入时填充当前时间
    private LocalDateTime createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)        // 插入或更新时填充当前时间
    // 删除状态
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    // 改变用户状态
    public void changeStatus(UserStatus status) { this.status = status; }
    // 重置密码
    public void resetPassword(String newPassword) { this.password = newPassword; }
}