package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RoleRequest {
    private Long id;
    
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    
    @NotBlank(message = "权限标识不能为空")
    private String roleKey;
    
    private String remark;
    
    private List<Long> menuIds;
}
