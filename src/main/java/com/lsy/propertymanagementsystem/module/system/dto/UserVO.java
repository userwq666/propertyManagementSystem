package com.lsy.propertymanagementsystem.module.system.dto;

import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import com.lsy.propertymanagementsystem.module.system.enums.UserType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String avatar;
    private UserType userType;
    private UserStatus status;
    private LocalDateTime createTime;
    private List<Long> roleIds;
    private List<String> roles;
}