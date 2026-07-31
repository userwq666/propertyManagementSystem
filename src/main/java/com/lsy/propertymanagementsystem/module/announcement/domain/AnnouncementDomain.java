package com.lsy.propertymanagementsystem.module.announcement.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.announcement.enums.AnnouncementType;
import com.lsy.propertymanagementsystem.module.announcement.enums.PublishStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class AnnouncementDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private AnnouncementType type;
    private String coverImage;
    private Integer isTop;
    private LocalDateTime topExpireTime;
    private PublishStatus publishStatus;
    private LocalDateTime publishTime;
    private LocalDateTime scheduledPublishTime;
    private Long creatorId;
    private Integer viewCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void prepareAdd() { this.publishStatus = PublishStatus.DRAFT; this.isTop = 0; this.viewCount = 0; }
    public void publish() { this.publishStatus = PublishStatus.PUBLISHED; this.publishTime = LocalDateTime.now(); }
    public void topUntil(LocalDateTime expireTime) { this.isTop = 1; this.topExpireTime = expireTime; }
    public void cancelTop() { this.isTop = 0; this.topExpireTime = null; }
    public void offline() { this.publishStatus = PublishStatus.OFFLINE; }
    public void incViewCount() { this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1; }
}
