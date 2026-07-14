package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String equipmentName;
    private String equipmentCode;
    private Long categoryId;
    private String location;
    private Integer status;
    private String maintenanceUser;
    private LocalDate installDate;
    private LocalDate warrantyDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}