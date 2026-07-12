package com.lsy.propertymanagementsystem.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private Integer userType;
    private String avatar;
}
