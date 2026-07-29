package com.lsy.propertymanagementsystem.module.announcement.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("announcement_read")
public class AnnouncementReadDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long announcementId;
    private Long userId;
    private LocalDateTime readTime;
}
