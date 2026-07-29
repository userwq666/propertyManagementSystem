package com.lsy.propertymanagementsystem.module.fee.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fee_notice_owner")
public class FeeNoticeOwnerDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long noticeId;
    private Long ownerId;
}
