-- ===============================================================
-- 物业管理系统 - 公告通知模块
-- ===============================================================

-- 8. 公告通知表
-- =====================================================================

-- 公告表
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1通知公告 2政策法规 3便民服务 4活动通知 5紧急通知',
    cover_image VARCHAR(255) COMMENT '封面图片',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
    top_expire_time DATETIME COMMENT '置顶过期时间',
    publish_status TINYINT NOT NULL DEFAULT 0 COMMENT '发布状态：0草稿 1发布 2下架',
    publish_time DATETIME COMMENT '发布时间',
    creator_id BIGINT COMMENT '创建人ID',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_announcement_creator FOREIGN KEY (creator_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '公告表';

-- 公告阅读记录表
CREATE TABLE announcement_read (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    announcement_id BIGINT NOT NULL COMMENT '公告ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    read_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    UNIQUE KEY uk_announcement_user (announcement_id, user_id),
    CONSTRAINT fk_ar_announcement FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE,
    CONSTRAINT fk_ar_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) COMMENT '公告阅读记录表';

-- 索引
CREATE INDEX idx_announcement_creator ON announcement(creator_id);
CREATE INDEX idx_announcement_status ON announcement(publish_status);
CREATE INDEX idx_announcement_type ON announcement(type);
CREATE INDEX idx_announcement_create_time ON announcement(create_time);
CREATE INDEX idx_ar_user ON announcement_read(user_id);

-- =====================================================================
