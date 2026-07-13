# 投诉建议模块设计

## 一、需求概述

投诉建议模块用于业主向物业提交投诉或建议，物业人员进行受理、处理，最终业主对处理结果进行评价。

### 1.1 功能清单

| 功能 | 说明 |
|------|------|
| 投诉建议提交 | 业主选择类型（投诉/建议/其他），填写标题和内容 |
| 处理流程 | 物业人员受理、处理、完成或驳回 |
| 完成评价 | 业主对处理结果进行评分（1-5星） |

### 1.2 用户角色

| 角色 | 权限 |
|------|------|
| 业主 | 提交投诉建议、查看自己的记录、评价处理结果 |
| 物业人员 | 查看所有记录、受理、处理、驳回 |

---

## 二、数据库设计

### 2.1 表结构

**表名**：`complaint_suggest`

```sql
CREATE TABLE complaint_suggest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL COMMENT '业主ID',
    house_id BIGINT NOT NULL COMMENT '房屋ID',
    type VARCHAR(20) NOT NULL COMMENT '类型：投诉/建议/其他',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容描述',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待受理 1已受理 2处理中 3已完成 4已评价 5已驳回',
    handle_user VARCHAR(50) NULL COMMENT '处理人',
    handle_result VARCHAR(500) NULL COMMENT '处理结果',
    finish_time DATETIME NULL COMMENT '完成时间',
    rating TINYINT NULL COMMENT '评分：1-5',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除'
) COMMENT '投诉建议表';
```

### 2.2 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 主键，自增 |
| owner_id | BIGINT | 是 | 业主ID，关联community_owner表 |
| house_id | BIGINT | 是 | 房屋ID，关联community_house表 |
| type | VARCHAR(20) | 是 | 类型：投诉/建议/其他 |
| title | VARCHAR(100) | 是 | 标题 |
| content | TEXT | 是 | 内容描述 |
| status | TINYINT | 是 | 状态：0待受理 1已受理 2处理中 3已完成 4已评价 5已驳回 |
| handle_user | VARCHAR(50) | 否 | 处理人姓名 |
| handle_result | VARCHAR(500) | 否 | 处理结果描述 |
| finish_time | DATETIME | 否 | 完成时间 |
| rating | TINYINT | 否 | 评分：1-5 |
| create_time | DATETIME | 是 | 创建时间 |
| update_time | DATETIME | 是 | 更新时间 |
| deleted | TINYINT | 是 | 逻辑删除 |

---

## 三、后端设计

### 3.1 API接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/complaint/suggest | 新增投诉建议 | 业主 |
| PUT | /api/complaint/suggest | 更新投诉建议 | 业主（仅自己的） |
| DELETE | /api/complaint/suggest/{id} | 删除投诉建议 | 业主（仅自己的） |
| GET | /api/complaint/suggest/{id} | 获取详情 | 业主（仅自己的）/物业 |
| GET | /api/complaint/suggest/page | 分页查询 | 业主（仅自己的）/物业 |
| PUT | /api/complaint/suggest/status | 更新状态 | 物业 |
| PUT | /api/complaint/suggest/rating | 更新评分 | 业主（仅自己的） |

### 3.2 状态流转

```
待受理(0) → 已受理(1) → 处理中(2) → 已完成(3) → 已评价(4)
                    ↓
                已驳回(5)
```

### 3.3 请求/响应DTO

**ComplaintSuggestRequest**：

```java
@Data
public class ComplaintSuggestRequest {
    private Long id;

    @NotNull(message = "业主ID不能为空")
    private Long ownerId;

    @NotNull(message = "房屋ID不能为空")
    private Long houseId;

    @NotBlank(message = "类型不能为空")
    private String type;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
}
```

---

## 四、前端设计

### 4.1 页面结构

| 页面 | 路径 | 说明 |
|------|------|------|
| 投诉建议列表 | /complaint/suggest | 分页展示，支持按状态、类型筛选 |
| 新增投诉建议 | /complaint/suggest/add | 表单：选择类型、填写标题、内容 |
| 投诉建议详情 | /complaint/suggest/:id | 查看详细信息、处理进度、评分 |

### 4.2 列表页功能

- 搜索栏：类型筛选（下拉）、状态筛选（下拉）
- 表格：标题、类型、状态、创建时间、操作
- 分页：标准分页组件
- 操作：查看详情、删除

### 4.3 表单页功能

- 类型选择：下拉选择（投诉/建议/其他）
- 标题输入：文本输入框
- 内容输入：文本域
- 提交按钮

### 4.4 详情页功能

- 信息展示：标题、类型、内容、状态
- 处理信息：处理人、处理结果、完成时间
- 评分区域：星级评分（仅状态为已完成时显示）
- 状态流程：可视化展示当前进度

---

## 五、实施计划

### 5.1 开发步骤

1. **数据库**：创建 complaint_suggest 表
2. **后端实体类**：创建 ComplaintSuggest 实体类
3. **后端Mapper**：创建 ComplaintSuggestMapper 接口
4. **后端DTO**：创建 ComplaintSuggestRequest 请求类
5. **后端Service**：创建 ComplaintSuggestService 接口和实现
6. **后端Controller**：创建 ComplaintSuggestController
7. **前端API**：创建 complaintSuggest.js API文件
8. **前端页面**：创建投诉建议页面（列表、新增、详情）
9. **路由配置**：更新前端路由

### 5.2 预计时间

- 开发时间：1天
- 测试时间：0.5天

### 5.3 技术要点

- 复用现有的MyBatis-Plus框架
- 复用现有的Result统一响应
- 复用现有的前端组件和样式
- 保持与报修模块一致的代码风格

---

## 六、扩展性考虑

### 6.1 未来扩展点

- **附件支持**：可扩展支持图片、文件上传
- **多次回复**：可增加回复表，支持物业多次回复
- **满意度评价**：可扩展评价维度（满意度、响应速度等）
- **数据统计**：可增加投诉建议统计图表

### 6.2 当前限制

- 不支持附件上传
- 不支持多次回复
- 评价仅支持评分，无文字评价

---

## 七、相关文档

- 项目README：`docs/README.md`
- API文档：`docs/api/README.md`
- 数据字典：`docs/database/README.md`
- 系统架构：`docs/architecture/README.md`
- 开发进度：`docs/progress/README.md`
