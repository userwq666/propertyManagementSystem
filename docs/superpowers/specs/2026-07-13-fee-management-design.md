# 收费管理模块设计文档

## 一、模块概述

收费管理模块是物业管理系统的核心业务模块，提供收费项目配置、账单管理、缴费记录、欠费统计功能。该模块基于小区基础信息模块的数据（业主、房屋）进行收费管理。

### 1.1 技术栈
- 后端：SpringBoot、MyBatis-Plus、MySQL 8.0、JWT、Spring Validation
- 前端：Vue3、Vite、Element Plus、Vue Router、Pinia
- 架构：前后端分离架构

### 1.2 模块范围
- 收费项目管理：收费项目增删改查、启用禁用
- 账单管理：手动批量生成账单、自动定时生成账单、账单查询
- 缴费记录：记录线下缴费（现金、转账等）、缴费状态更新
- 欠费统计：欠费列表、欠费金额汇总、按业主/房屋筛选

### 1.3 设计决策
- 仅记录线下缴费，不对接在线支付
- 支持手动和自动两种账单生成方式
- 欠费统计为基础统计（列表+汇总），暂不实现图表和导出

## 二、数据库设计

### 2.1 fee_item 收费项目表
```sql
CREATE TABLE fee_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(100) NOT NULL COMMENT '收费项目名称',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    cycle_type TINYINT NOT NULL COMMENT '收费周期：1月 2季 3年',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0禁用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '收费项目表';
```

### 2.2 fee_record 缴费账单记录表
```sql
CREATE TABLE fee_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL COMMENT '业主id',
    house_id BIGINT NOT NULL COMMENT '房屋id',
    item_id BIGINT NOT NULL COMMENT '收费项目id',
    total_money DECIMAL(10,2) NOT NULL COMMENT '应付总金额',
    bill_cycle VARCHAR(20) NOT NULL COMMENT '账单所属周期（如：2024-01）',
    pay_status TINYINT NOT NULL DEFAULT 0 COMMENT '缴费状态：0未缴费 1已缴费 2欠费',
    pay_time DATETIME COMMENT '实际缴费时间',
    pay_way VARCHAR(50) COMMENT '支付方式',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '缴费账单记录表';
```

## 三、后端API设计

### 3.1 收费项目管理API
```
POST   /api/fee/item              # 新增收费项目
PUT    /api/fee/item              # 编辑收费项目
DELETE /api/fee/item/{id}         # 删除收费项目
GET    /api/fee/item/list         # 获取收费项目列表
GET    /api/fee/item/{id}         # 获取收费项目详情
GET    /api/fee/item/page         # 分页查询收费项目
PUT    /api/fee/item/status       # 更新收费项目状态
```

### 3.2 账单管理API
```
POST   /api/fee/record/generate   # 批量生成账单
GET    /api/fee/record/list       # 获取账单列表
GET    /api/fee/record/{id}       # 获取账单详情
GET    /api/fee/record/page       # 分页查询账单
PUT    /api/fee/record/pay        # 确认缴费
GET    /api/fee/record/statistics # 欠费统计
```

## 四、前端页面设计

### 4.1 收费项目管理页面
- 收费项目列表表格
- 新增/编辑收费项目对话框
- 启用/禁用状态切换
- 分页查询

### 4.2 账单管理页面
- 账单列表表格
- 批量生成账单对话框
- 确认缴费操作
- 分页查询

### 4.3 缴费记录页面
- 缴费记录列表表格
- 分页查询
- 按业主/房屋筛选

### 4.4 欠费统计页面
- 欠费列表表格
- 欠费金额汇总
- 按业主/房屋筛选

## 五、路由配置

在 `frontend/src/router/index.js` 的 layout children 中添加：
```javascript
{
  path: 'fee',
  name: 'Fee',
  redirect: '/fee/item',
  meta: { title: '收费管理', icon: 'Money' },
  children: [
    {
      path: 'item',
      name: 'FeeItem',
      component: () => import('@/views/fee/item/index.vue'),
      meta: { title: '收费项目' }
    },
    {
      path: 'record',
      name: 'FeeRecord',
      component: () => import('@/views/fee/record/index.vue'),
      meta: { title: '账单管理' }
    },
    {
      path: 'payment',
      name: 'FeePayment',
      component: () => import('@/views/fee/payment/index.vue'),
      meta: { title: '缴费记录' }
    },
    {
      path: 'arrears',
      name: 'FeeArrears',
      component: () => import('@/views/fee/arrears/index.vue'),
      meta: { title: '欠费统计' }
    }
  ]
}
```
