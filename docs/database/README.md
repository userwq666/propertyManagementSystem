# 数据字典

## 一、概述

本文档描述物业管理系统的所有数据库表结构。数据库使用MySQL 8.0，字符集为utf8mb4。

### 1.1 数据库命名规范

- 表名使用小写字母和下划线，如：sys_user、community_house
- 字段名使用小写字母和下划线，如：user_name、create_time
- 主键字段统一使用id
- 逻辑删除字段统一使用deleted
- 时间字段统一使用create_time和update_time

### 1.2 通用字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除：0未删除 1已删除 |

---

## 二、系统管理模块

### 2.1 sys_user 用户表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| username | VARCHAR(50) | 是 | - | 用户名 |
| password | VARCHAR(100) | 是 | - | 密码（BCrypt加密） |
| real_name | VARCHAR(50) | 否 | NULL | 真实姓名 |
| phone | VARCHAR(20) | 否 | NULL | 手机号 |
| email | VARCHAR(100) | 否 | NULL | 邮箱 |
| avatar | VARCHAR(255) | 否 | NULL | 头像 |
| status | TINYINT | 是 | 1 | 状态：0禁用 1启用 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)
- UNIQUE INDEX uk_username (username)

### 2.2 sys_role 角色表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| role_name | VARCHAR(50) | 是 | - | 角色名称 |
| role_code | VARCHAR(50) | 是 | - | 角色编码 |
| status | TINYINT | 是 | 1 | 状态：0禁用 1启用 |
| remark | VARCHAR(500) | 否 | NULL | 备注 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)
- UNIQUE INDEX uk_role_code (role_code)

### 2.3 sys_menu 菜单表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| menu_name | VARCHAR(50) | 是 | - | 菜单名称 |
| parent_id | BIGINT | 是 | 0 | 父菜单ID |
| order_num | INT | 是 | 0 | 显示顺序 |
| path | VARCHAR(200) | 否 | NULL | 路由地址 |
| component | VARCHAR(255) | 否 | NULL | 组件路径 |
| menu_type | CHAR(1) | 是 | - | 菜单类型：M目录 C菜单 F按钮 |
| visible | TINYINT | 是 | 1 | 是否可见：0隐藏 1显示 |
| status | TINYINT | 是 | 1 | 状态：0禁用 1启用 |
| perms | VARCHAR(100) | 否 | NULL | 权限标识 |
| icon | VARCHAR(100) | 否 | NULL | 图标 |
| remark | VARCHAR(500) | 否 | NULL | 备注 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)
- INDEX idx_parent_id (parent_id)

### 2.4 sys_user_role 用户角色关联表

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| user_id | BIGINT | 是 | 用户ID |
| role_id | BIGINT | 是 | 角色ID |

**索引：**
- PRIMARY KEY (user_id, role_id)

### 2.5 sys_role_menu 角色菜单关联表

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| role_id | BIGINT | 是 | 角色ID |
| menu_id | BIGINT | 是 | 菜单ID |

**索引：**
- PRIMARY KEY (role_id, menu_id)

### 2.6 sys_oper_log 操作日志表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| title | VARCHAR(50) | 否 | NULL | 操作模块 |
| business_type | INT | 否 | 0 | 业务类型：0其他 1新增 2修改 3删除 |
| method | VARCHAR(200) | 否 | NULL | 方法名称 |
| request_method | VARCHAR(10) | 否 | NULL | 请求方式 |
| oper_name | VARCHAR(50) | 否 | NULL | 操作人 |
| oper_url | VARCHAR(255) | 否 | NULL | 请求URL |
| oper_ip | VARCHAR(128) | 否 | NULL | 操作IP |
| oper_param | TEXT | 否 | NULL | 请求参数 |
| json_result | TEXT | 否 | NULL | 返回参数 |
| status | INT | 否 | 0 | 操作状态：0正常 1异常 |
| error_msg | TEXT | 否 | NULL | 错误消息 |
| oper_time | DATETIME | 否 | NULL | 操作时间 |
| cost_time | BIGINT | 否 | 0 | 耗时(ms) |

**索引：**
- PRIMARY KEY (id)
- INDEX idx_oper_time (oper_time)

---

## 三、小区基础信息模块

### 3.1 community_building 楼栋表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| building_no | VARCHAR(50) | 是 | - | 楼栋编号 |
| floor_count | INT | 否 | NULL | 总楼层 |
| total_house | INT | 否 | NULL | 总户数 |
| build_year | INT | 否 | NULL | 建成年份 |
| remark | VARCHAR(500) | 否 | NULL | 备注 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)
- UNIQUE INDEX uk_building_no (building_no)

### 3.2 community_house 房屋表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| building_id | BIGINT | 是 | - | 楼栋ID |
| room_no | VARCHAR(50) | 是 | - | 房间号 |
| area | DECIMAL(10,2) | 否 | NULL | 房屋面积 |
| house_type | VARCHAR(50) | 否 | NULL | 户型 |
| house_status | TINYINT | 是 | 0 | 房屋状态：0空置 1已入住 2出租 |
| owner_id | BIGINT | 否 | NULL | 业主ID |
| remark | VARCHAR(500) | 否 | NULL | 备注 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)
- INDEX idx_building_id (building_id)
- INDEX idx_owner_id (owner_id)

### 3.3 community_owner 业主信息表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| user_id | BIGINT | 否 | NULL | 关联系统登录用户ID |
| name | VARCHAR(50) | 是 | - | 业主姓名 |
| id_card | VARCHAR(20) | 否 | NULL | 身份证号 |
| phone | VARCHAR(20) | 否 | NULL | 联系电话 |
| emergency_contact | VARCHAR(50) | 否 | NULL | 紧急联系人 |
| emergency_phone | VARCHAR(20) | 否 | NULL | 紧急联系电话 |
| check_in_time | DATETIME | 否 | NULL | 入住时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)
- INDEX idx_user_id (user_id)
- INDEX idx_phone (phone)

### 3.4 community_parking 车位表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| parking_no | VARCHAR(50) | 是 | - | 车位编号 |
| parking_type | TINYINT | 是 | 0 | 车位类型：0固定车位 1临时车位 |
| status | TINYINT | 是 | 0 | 状态：0空闲 1已租赁 |
| owner_id | BIGINT | 否 | NULL | 所属业主ID |
| expire_time | DATETIME | 否 | NULL | 租赁到期时间 |
| remark | VARCHAR(500) | 否 | NULL | 备注 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)
- UNIQUE INDEX uk_parking_no (parking_no)
- INDEX idx_owner_id (owner_id)

---

## 四、物业收费模块

### 4.1 fee_item 收费项目表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| item_name | VARCHAR(100) | 是 | - | 收费项目名称 |
| price | DECIMAL(10,2) | 是 | - | 单价 |
| cycle_type | TINYINT | 是 | - | 收费周期：1月 2季 3年 |
| status | TINYINT | 是 | 1 | 是否启用：0禁用 1启用 |
| remark | VARCHAR(500) | 否 | NULL | 备注 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)

### 4.2 fee_record 缴费账单记录表

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| owner_id | BIGINT | 是 | - | 业主ID |
| house_id | BIGINT | 是 | - | 房屋ID |
| item_id | BIGINT | 是 | - | 收费项目ID |
| total_money | DECIMAL(10,2) | 是 | - | 应付总金额 |
| bill_cycle | VARCHAR(20) | 是 | - | 账单所属周期（如：2026-07） |
| pay_status | TINYINT | 是 | 0 | 缴费状态：0未缴费 1已缴费 2欠费 |
| pay_time | DATETIME | 否 | NULL | 实际缴费时间 |
| pay_way | VARCHAR(50) | 否 | NULL | 支付方式：现金/转账/微信/支付宝 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

**索引：**
- PRIMARY KEY (id)
- INDEX idx_owner_id (owner_id)
- INDEX idx_house_id (house_id)
- INDEX idx_item_id (item_id)
- INDEX idx_pay_status (pay_status)
- INDEX idx_bill_cycle (bill_cycle)

---

## 五、待开发模块

### 5.1 repair_record 报修记录表（待创建）

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| owner_id | BIGINT | 是 | - | 报修业主ID |
| house_id | BIGINT | 是 | - | 房屋ID |
| repair_type | VARCHAR(50) | 是 | - | 报修类型：水电/门窗/公共设备 |
| content | TEXT | 是 | - | 故障描述 |
| img_url | VARCHAR(500) | 否 | NULL | 故障图片 |
| status | TINYINT | 是 | 0 | 状态：0待处理 1处理中 2已完成 3驳回 |
| handle_user | VARCHAR(50) | 否 | NULL | 处理物业人员 |
| handle_result | TEXT | 否 | NULL | 处理结果 |
| finish_time | DATETIME | 否 | NULL | 完成时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

### 5.2 complaint_suggest 投诉建议表（待创建）

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| owner_id | BIGINT | 是 | - | 业主ID |
| type | TINYINT | 是 | - | 类型：1投诉 2建议 |
| title | VARCHAR(100) | 是 | - | 标题 |
| content | TEXT | 是 | - | 内容 |
| status | TINYINT | 是 | 0 | 状态：0待处理 1已处理 |
| handle_user | VARCHAR(50) | 否 | NULL | 处理人 |
| handle_result | TEXT | 否 | NULL | 处理结果 |
| handle_time | DATETIME | 否 | NULL | 处理时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

### 5.3 announcement 公告表（待创建）

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| title | VARCHAR(100) | 是 | - | 公告标题 |
| content | TEXT | 是 | - | 公告内容 |
| type | TINYINT | 是 | - | 公告类型：1通知 2公告 3活动 |
| status | TINYINT | 是 | 0 | 状态：0草稿 1已发布 |
| publish_time | DATETIME | 否 | NULL | 发布时间 |
| create_by | VARCHAR(50) | 否 | NULL | 创建人 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

### 5.4 equipment 设备表（待创建）

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| equipment_name | VARCHAR(100) | 是 | - | 设备名称 |
| equipment_type | VARCHAR(50) | 是 | - | 设备类型 |
| location | VARCHAR(200) | 否 | NULL | 安装位置 |
| status | TINYINT | 是 | 1 | 状态：0停用 1正常 2维修中 |
| install_time | DATETIME | 否 | NULL | 安装时间 |
| remark | VARCHAR(500) | 否 | NULL | 备注 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

### 5.5 inspection_record 巡检记录表（待创建）

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | 自增 | 主键 |
| equipment_id | BIGINT | 是 | - | 设备ID |
| inspection_date | DATE | 是 | - | 巡检日期 |
| inspector | VARCHAR(50) | 是 | - | 巡检人 |
| result | TINYINT | 是 | - | 巡检结果：0异常 1正常 |
| content | TEXT | 否 | NULL | 巡检内容 |
| remark | VARCHAR(500) | 否 | NULL | 备注 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 是 | 0 | 逻辑删除：0未删除 1已删除 |

---

## 六、数据字典维护说明

### 6.1 新增模块时

1. 在对应模块章节下添加表结构说明
2. 包含完整的字段说明、类型、默认值
3. 添加必要的索引说明
4. 更新"待开发模块"章节

### 6.2 修改表结构时

1. 更新对应字段说明
2. 添加变更说明
3. 记录变更时间和版本

### 6.3 注意事项

- 所有时间字段使用DATETIME类型
- 金额字段使用DECIMAL类型，精度根据业务需求设置
- 状态字段使用TINYINT类型
- 逻辑删除字段统一使用deleted，0未删除，1已删除
- 主键统一使用BIGINT自增
