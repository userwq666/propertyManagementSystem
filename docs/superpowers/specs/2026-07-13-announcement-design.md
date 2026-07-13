# 公告管理模块设计

## 一、需求概述

公告管理模块用于管理员发布公告，业主查看公告列表和详情。

### 1.1 功能清单

| 功能 | 说明 |
|------|------|
| 公告发布 | 管理员填写标题、内容，选择类型、设置发布时间、过期时间、是否置顶 |
| 公告查看 | 业主查看公告列表和详情 |
| 公告管理 | 管理员管理公告状态（草稿/预发布/已发布/已过期） |

### 1.2 用户角色

| 角色 | 权限 |
|------|------|
| 管理员 | 创建、编辑、删除、发布、下架公告 |
| 业主 | 查看公告列表和详情 |

---

## 二、数据库设计

### 2.1 表结构

**表名**：`announcement`

```sql
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    type VARCHAR(20) NOT NULL COMMENT '类型：通知/活动/紧急',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0草稿 1预发布 2已发布 3已过期',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
    publish_time DATETIME NULL COMMENT '定时发布时间',
    expire_time DATETIME NULL COMMENT '过期时间',
    create_user VARCHAR(50) NOT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除'
) COMMENT '公告表';
```

### 2.2 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 主键，自增 |
| title | VARCHAR(200) | 是 | 标题 |
| content | TEXT | 是 | 内容 |
| type | VARCHAR(20) | 是 | 类型：通知/活动/紧急 |
| status | TINYINT | 是 | 状态：0草稿 1预发布 2已发布 3已过期 |
| is_top | TINYINT | 是 | 是否置顶：0否 1是 |
| publish_time | DATETIME | 否 | 定时发布时间 |
| expire_time | DATETIME | 否 | 过期时间 |
| create_user | VARCHAR(50) | 是 | 创建人 |
| create_time | DATETIME | 是 | 创建时间 |
| update_time | DATETIME | 是 | 更新时间 |
| deleted | TINYINT | 是 | 逻辑删除 |

---

## 三、后端设计

### 3.1 API接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/announcement | 新增公告 | 管理员 |
| PUT | /api/announcement | 更新公告 | 管理员 |
| DELETE | /api/announcement/{id} | 删除公告 | 管理员 |
| GET | /api/announcement/{id} | 获取详情 | 管理员/业主 |
| GET | /api/announcement/page | 分页查询 | 管理员/业主 |
| PUT | /api/announcement/status | 更新状态 | 管理员 |
| PUT | /api/announcement/top | 更新置顶状态 | 管理员 |

### 3.2 状态流转

```
草稿(0) → 预发布(1) → 已发布(2) → 已过期(3)
```

### 3.3 定时发布逻辑

- 创建公告时设置publish_time，状态为预发布(1)
- 系统定时检查，到时间自动发布为已发布(2)
- 到expire_time自动过期为已过期(3)

### 3.4 请求/响应DTO

**AnnouncementRequest**：

```java
@Data
public class AnnouncementRequest {
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "类型不能为空")
    private String type;

    private Integer status;
    private Integer isTop;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
}
```

---

## 四、前端设计

### 4.1 页面结构

| 页面 | 路径 | 说明 |
|------|------|------|
| 公告列表 | /announcement | 分页展示，支持按类型、状态筛选 |
| 新增公告 | /announcement/add | 表单：选择类型、填写标题、内容、设置发布时间、过期时间、置顶 |
| 公告详情 | /announcement/:id | 查看公告详细信息 |

### 4.2 列表页功能

- 搜索栏：类型筛选（下拉）、状态筛选（下拉）
- 表格：标题、类型、状态、置顶、创建时间、操作
- 分页：标准分页组件
- 操作：查看详情、编辑、删除

### 4.3 表单页功能

- 类型选择：下拉选择（通知/活动/紧急）
- 标题输入：文本输入框
- 内容输入：文本域
- 发布时间：日期时间选择器
- 过期时间：日期时间选择器
- 置顶开关：切换按钮
- 提交按钮

### 4.4 详情页功能

- 信息展示：标题、类型、内容、状态、置顶、发布时间、过期时间

---

## 五、实施计划

### 5.1 开发步骤

1. **数据库**：创建 announcement 表
2. **后端实体类**：创建 Announcement 实体类
3. **后端Mapper**：创建 AnnouncementMapper 接口
4. **后端DTO**：创建 AnnouncementRequest 请求类
5. **后端Service**：创建 AnnouncementService 接口和实现
6. **后端Controller**：创建 AnnouncementController
7. **前端API**：创建 announcement.js API文件
8. **前端页面**：创建公告页面（列表、新增、详情）
9. **路由配置**：更新前端路由

### 5.2 预计时间

- 开发时间：1天
- 测试时间：0.5天

### 5.3 技术要点

- 复用现有的MyBatis-Plus框架
- 复用现有的Result统一响应
- 复用现有的前端组件和样式
- 保持与现有模块一致的代码风格

---

## 六、扩展性考虑

### 6.1 未来扩展点

- **附件支持**：可扩展支持图片、文件上传
- **阅读统计**：可增加阅读记录表，统计阅读情况
- **分类管理**：可增加分类表，支持自定义分类
- **推送通知**：可扩展消息推送功能

### 6.2 当前限制

- 不支持附件上传
- 不支持阅读统计
- 分类为固定选项（通知/活动/紧急）

---

## 七、相关文档

- 项目README：`docs/README.md`
- API文档：`docs/api/README.md`
- 数据字典：`docs/database/README.md`
- 系统架构：`docs/architecture/README.md`
- 开发进度：`docs/progress/README.md`
