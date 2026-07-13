# 报修维修模块设计文档

## 一、模块概述

报修维修模块是物业管理系统的核心业务模块，提供报修记录管理、处理流程、完成评价功能。该模块基于小区基础信息模块的数据（业主、房屋）进行报修管理。

### 1.1 技术栈
- 后端：SpringBoot、MyBatis-Plus、MySQL 8.0、JWT、Spring Validation
- 前端：Vue3、Vite、Element Plus、Vue Router、Pinia
- 架构：前后端分离架构

### 1.2 模块范围
- 报修提交：业主和物业人员都可以提交报修申请
- 处理流程：物业人员接单、处理、完成
- 完成评价：业主对维修服务进行评分（1-5星）

### 1.3 设计决策
- 报修类型为固定类型（水电、门窗、公共设备等），支持描述备注
- 评价功能仅支持评分（1-5星），不支持文字评价
- 支持图片上传（故障图片）

## 二、数据库设计

### 2.1 repair_record 报修记录表
```sql
CREATE TABLE repair_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL COMMENT '报修业主id',
    house_id BIGINT NOT NULL COMMENT '房屋id',
    repair_type VARCHAR(50) NOT NULL COMMENT '报修类型：水电/门窗/公共设备',
    content TEXT NOT NULL COMMENT '故障描述',
    img_url VARCHAR(500) COMMENT '故障图片',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待处理 1处理中 2已完成 3驳回',
    handle_user VARCHAR(50) COMMENT '处理物业人员',
    handle_result TEXT COMMENT '处理结果',
    finish_time DATETIME COMMENT '完成时间',
    rating TINYINT COMMENT '评分：1-5',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '报修记录表';
```

## 三、后端API设计

### 3.1 报修管理API
```
POST   /api/repair/record          # 新增报修记录
PUT    /api/repair/record          # 编辑报修记录
DELETE /api/repair/record/{id}     # 删除报修记录
GET    /api/repair/record/{id}     # 获取报修记录详情
GET    /api/repair/record/page     # 分页查询报修记录
PUT    /api/repair/record/status   # 更新报修状态
PUT    /api/repair/record/rating   # 更新报修评分
```

## 四、前端页面设计

### 4.1 报修管理页面
- 报修记录列表表格
- 新增/编辑报修记录对话框
- 处理状态更新操作
- 评分操作
- 分页查询
- 按状态、业主、房屋筛选

## 五、路由配置

在 `frontend/src/router/index.js` 的 layout children 中添加：
```javascript
{
  path: 'repair',
  name: 'Repair',
  redirect: '/repair/record',
  meta: { title: '报修管理', icon: 'Tools' },
  children: [
    {
      path: 'record',
      name: 'RepairRecord',
      component: () => import('@/views/repair/record/index.vue'),
      meta: { title: '报修记录' }
    }
  ]
}
```
