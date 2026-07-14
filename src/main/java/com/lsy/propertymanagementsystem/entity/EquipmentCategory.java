package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("equipment_category")
public class EquipmentCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String categoryName;
    private String description;
    private Integer sortOrder;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}