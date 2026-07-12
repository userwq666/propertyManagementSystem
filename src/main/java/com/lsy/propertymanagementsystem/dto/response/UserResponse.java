package com.lsy.propertymanagementsystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String avatar;
    private Integer userType;
    private Integer status;
    private LocalDateTime createTime;
    private List<Long> roleIds;
}
