package com.lsy.propertymanagementsystem.module.system.dto;

import com.lsy.propertymanagementsystem.module.system.enums.UserType;
import lombok.Data;

import java.util.List;

@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private UserType userType;
    private String avatar;
    private List<Long> roleIds;
    private List<String> permissions;
    private List<String> roles;
}