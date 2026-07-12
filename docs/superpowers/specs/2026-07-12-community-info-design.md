# 小区基础信息模块设计文档

## 一、模块概述

小区基础信息模块是物业管理系统的核心业务模块，提供楼栋、房屋、业主、车位的基础数据管理功能。该模块为收费管理、报修维修等业务模块提供数据支撑。

### 1.1 技术栈
- 后端：SpringBoot、MyBatis-Plus、MySQL 8.0、JWT、Spring Validation
- 前端：Vue3、Vite、Element Plus、Vue Router、Pinia
- 架构：前后端分离架构

### 1.2 模块范围
- 楼栋管理：楼栋增删改查
- 房屋管理：绑定楼栋、录入户型面积、关联业主
- 业主管理：业主信息录入、绑定房屋、开通登录账号
- 车位管理：车位新增、租赁分配、到期管理

### 1.3 设计决策
- 暂不实现多小区支持（单小区版本）
- 业主需要创建登录账号（关联sys_user表）

## 二、数据库设计

### 2.1 community_building 楼栋表
```sql
CREATE TABLE community_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_no VARCHAR(50) NOT NULL COMMENT '楼栋编号',
    floor_count INT COMMENT '总楼层',
    total_house INT COMMENT '总户数',
    build_year INT COMMENT '建成年份',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '楼栋表';
```

### 2.2 community_house 房屋表
```sql
CREATE TABLE community_house (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_id BIGINT NOT NULL COMMENT '楼栋id',
    room_no VARCHAR(50) NOT NULL COMMENT '房间号',
    area DECIMAL(10,2) COMMENT '房屋面积',
    house_type VARCHAR(50) COMMENT '户型',
    house_status TINYINT NOT NULL DEFAULT 0 COMMENT '房屋状态：0空置 1已入住 2出租',
    owner_id BIGINT COMMENT '业主id',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '房屋表';
```

### 2.3 community_owner 业主信息表
```sql
CREATE TABLE community_owner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '关联系统登录用户id',
    name VARCHAR(50) NOT NULL COMMENT '业主姓名',
    id_card VARCHAR(20) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '联系电话',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    check_in_time DATETIME COMMENT '入住时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '业主信息表';
```

### 2.4 community_parking 车位表
```sql
CREATE TABLE community_parking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parking_no VARCHAR(50) NOT NULL COMMENT '车位编号',
    parking_type TINYINT NOT NULL DEFAULT 0 COMMENT '车位类型：0固定车位 1临时车位',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0空闲 1已租赁',
    owner_id BIGINT COMMENT '所属业主id',
    expire_time DATETIME COMMENT '租赁到期时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '车位表';
```

## 三、后端API设计

### 3.1 楼栋管理API
```
POST   /api/building              # 新增楼栋
PUT    /api/building              # 编辑楼栋
DELETE /api/building/{id}         # 删除楼栋
GET    /api/building/list         # 获取楼栋列表
GET    /api/building/{id}         # 获取楼栋详情
GET    /api/building/page         # 分页查询楼栋
```

### 3.2 房屋管理API
```
POST   /api/house                 # 新增房屋
PUT    /api/house                 # 编辑房屋
DELETE /api/house/{id}            # 删除房屋
GET    /api/house/list            # 获取房屋列表
GET    /api/house/{id}            # 获取房屋详情
GET    /api/house/page            # 分页查询房屋
PUT    /api/house/status          # 修改房屋状态
```

### 3.3 业主管理API
```
POST   /api/owner                 # 新增业主
PUT    /api/owner                 # 编辑业主
DELETE /api/owner/{id}            # 删除业主
GET    /api/owner/list            # 获取业主列表
GET    /api/owner/{id}            # 获取业主详情
GET    /api/owner/page            # 分页查询业主
POST   /api/owner/bindUser        # 绑定用户账号
```

### 3.4 车位管理API
```
POST   /api/parking               # 新增车位
PUT    /api/parking               # 编辑车位
DELETE /api/parking/{id}          # 删除车位
GET    /api/parking/list          # 获取车位列表
GET    /api/parking/{id}          # 获取车位详情
GET    /api/parking/page          # 分页查询车位
PUT    /api/parking/status        # 修改车位状态（租赁/释放）
```

## 四、前端页面设计

### 4.1 楼栋管理页面
- 楼栋列表表格（分页）
- 新增/编辑楼栋弹窗
- 搜索栏（楼栋编号）
- 操作按钮：编辑、删除

### 4.2 房屋管理页面
- 房屋列表表格（分页）
- 新增/编辑房屋弹窗（选择楼栋、输入房间号、面积、户型、状态）
- 搜索栏（楼栋、房间号、状态）
- 操作按钮：编辑、删除、修改状态

### 4.3 业主管理页面
- 业主列表表格（分页）
- 新增/编辑业主弹窗
- 搜索栏（业主姓名、手机号）
- 操作按钮：编辑、删除、绑定用户

### 4.4 车位管理页面
- 车位列表表格（分页）
- 新增/编辑车位弹窗
- 搜索栏（车位编号、状态）
- 操作按钮：编辑、删除、租赁/释放

## 五、实现顺序

按照自顶向下原则，实现顺序为：
1. 创建数据库表结构
2. 后端实体类和Mapper
3. 后端DTO和Service
4. 后端Controller
5. 前端页面

## 六、数据关联关系

- community_house.building_id → community_building.id
- community_house.owner_id → community_owner.id
- community_owner.user_id → sys_user.id
- community_parking.owner_id → community_owner.id
