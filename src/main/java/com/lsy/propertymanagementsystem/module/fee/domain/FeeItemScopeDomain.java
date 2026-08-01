package com.lsy.propertymanagementsystem.module.fee.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fee_item_scope")
public class FeeItemScopeDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private Integer scopeType;
    private Long targetId;
}
