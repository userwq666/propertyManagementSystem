package com.lsy.propertymanagementsystem.module.announcement.dto;

import com.lsy.propertymanagementsystem.module.announcement.enums.AnnouncementType;
import com.lsy.propertymanagementsystem.module.announcement.enums.PublishStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementVO {
    private Long id;
    private String title;
    private String content;
    private AnnouncementType type;
    private String coverImage;
    private Integer isTop;
    private LocalDateTime topExpireTime;
    private PublishStatus publishStatus;
    private LocalDateTime publishTime;
    private Long creatorId;
    private String creatorName;
    private Integer viewCount;
    private LocalDateTime createTime;
}
