# 数据库设计文档

## 1. 概述

本文档详细描述物业管理系统的数据库设计，包括表结构、字段定义、索引、外键约束、枚举值等。

**数据库版本**: MySQL 8.0+
**字符集**: utf8mb4
**排序规则**: utf8mb4_general_ci

---

## 2. 表结构详细设计

### 2.1 系统基础表

#### 2.1.1 sys_user - 系统用户表
```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像地址',
    user_type TINYINT NOT NULL DEFAULT 3 COMMENT '用户类型：1超级管理员 2物业管理员 3业主',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：0禁用 1正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删 1已删'
) COMMENT '系统用户表';
```
**索引**: `idx_username (username)`, `idx_status (status)`

#### 2.1.2 sys_role - 角色表
```sql
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '权限标识',
    remark VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '角色表';
```

#### 2.1.3 sys_menu - 菜单权限表
```sql
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID，顶级为0',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(255) COMMENT '前端路由路径',
    component VARCHAR(255) COMMENT '前端组件路径',
    perms VARCHAR(100) COMMENT '权限标识，如 system:user:add',
    menu_type TINYINT COMMENT '菜单类型：0目录 1菜单 2按钮',
    sort INT DEFAULT 0 COMMENT '显示排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '启用状态：0禁用 1启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '菜单权限表';
```
**索引**: `idx_parent_id (parent_id)`, `idx_perms (perms)`

#### 2.1.4 sys_user_role - 用户角色关联表
```sql
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '用户角色关联表';
```
**索引**: `idx_user_role_user (user_id)`, `idx_user_role_role (role_id)`

#### 2.1.5 sys_role_menu - 角色菜单关联表
```sql
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id) ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT '角色菜单关联表';
```
**索引**: `idx_role_menu_role (role_id)`, `idx_role_menu_menu (menu_id)`

#### 2.1.6 sys_oper_log - 操作日志表
```sql
CREATE TABLE sys_oper_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NULL COMMENT '操作人ID',
    user_name VARCHAR(50) COMMENT '操作人账号(冗余)',
    oper_module VARCHAR(50) COMMENT '操作模块',
    oper_type VARCHAR(20) COMMENT '操作类型：新增/编辑/删除/查询/导出/导入',
    oper_ip VARCHAR(50) COMMENT '请求IP',
    oper_desc VARCHAR(500) COMMENT '操作描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    CONSTRAINT fk_oper_log_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '操作日志表';
```
**索引**: `idx_oper_log_user (user_id)`, `idx_oper_log_time (create_time)`

---

### 2.2 小区基础表

#### 2.2.1 community_building - 楼栋表
```sql
CREATE TABLE community_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '楼栋ID',
    building_no VARCHAR(50) NOT NULL COMMENT '楼栋编号',
    floor_count INT COMMENT '总楼层数',
    total_house INT COMMENT '总户数',
    build_year INT COMMENT '建成年份',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '楼栋表';
```

#### 2.2.2 community_house - 房屋表
```sql
CREATE TABLE community_house (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房屋ID',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    room_no VARCHAR(50) NOT NULL COMMENT '房间号',
    area DECIMAL(10,2) COMMENT '房屋面积(㎡)',
    house_type VARCHAR(50) COMMENT '户型，如 2室1厅',
    house_status TINYINT NOT NULL DEFAULT 0 COMMENT '房屋状态：0空置 1已入住 2出租',
    owner_id BIGINT COMMENT '业主ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_house_building FOREIGN KEY (building_id) REFERENCES community_building(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_house_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '房屋表';
```
**索引**: `idx_house_building (building_id)`, `idx_house_owner (owner_id)`

#### 2.2.3 community_owner - 业主信息表
```sql
CREATE TABLE community_owner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '业主ID',
    user_id BIGINT COMMENT '关联系统用户ID',
    name VARCHAR(50) NOT NULL COMMENT '业主姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    id_card VARCHAR(18) COMMENT '身份证号',
    id_card_front VARCHAR(255) COMMENT '身份证正面照片',
    id_card_back VARCHAR(255) COMMENT '身份证反面照片',
    owner_type TINYINT NOT NULL DEFAULT 1 COMMENT '业主类型：1本人 2家属 3租客',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1正常',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_owner_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '业主信息表';
```
**索引**: `idx_owner_user (user_id)`, `idx_owner_phone (phone)`

#### 2.2.4 community_parking - 车位表
```sql
CREATE TABLE community_parking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车位ID',
    parking_no VARCHAR(50) NOT NULL COMMENT '车位编号',
    parking_type TINYINT NOT NULL DEFAULT 1 COMMENT '车位类型：1地上 2地下',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0空闲 1已租 2已售 3维修中',
    owner_id BIGINT COMMENT '业主ID',
    rent_price DECIMAL(10,2) COMMENT '月租金',
    sell_price DECIMAL(10,2) COMMENT '售价',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_parking_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '车位表';
```
**索引**: `idx_parking_owner (owner_id)`, `idx_parking_status (status)`

---

### 2.3 收费管理表

#### 2.3.1 fee_item - 收费项目表
```sql
CREATE TABLE fee_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
    item_name VARCHAR(100) NOT NULL COMMENT '收费项目名称',
    item_type TINYINT NOT NULL DEFAULT 1 COMMENT '收费类型：1物业费 2车位费 3水费 4电费 5燃气费 6暖气费 9其他',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    unit VARCHAR(20) COMMENT '单位：元/㎡/月、元/月、元/吨、元/度',
    cycle_type TINYINT NOT NULL DEFAULT 1 COMMENT '收费周期：1按月 2按季 3按半年 4按年 5一次性',
    description VARCHAR(500) COMMENT '项目描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '收费项目表';
```

#### 2.3.2 fee_notice - 收费通知表
```sql
CREATE TABLE fee_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    notice_title VARCHAR(100) NOT NULL COMMENT '通知标题',
    notice_content TEXT COMMENT '通知内容',
    notice_type TINYINT NOT NULL DEFAULT 1 COMMENT '通知类型：1缴费通知 2欠费催缴 3费率调整 4其他',
    send_scope TINYINT NOT NULL DEFAULT 1 COMMENT '发送范围：1全体 2指定楼栋 3指定业主',
    building_ids VARCHAR(500) COMMENT '指定楼栋ID，逗号分隔',
    owner_ids VARCHAR(500) COMMENT '指定业主ID，逗号分隔',
    send_status TINYINT NOT NULL DEFAULT 0 COMMENT '发送状态：0草稿 1已发送 2发送失败',
    send_time DATETIME COMMENT '发送时间',
    creator_id BIGINT COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '收费通知表';
```
**索引**: `idx_notice_creator (creator_id)`, `idx_notice_status (send_status)`

#### 2.3.3 fee_record - 缴费记录表
```sql
CREATE TABLE fee_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    fee_no VARCHAR(50) NOT NULL UNIQUE COMMENT '缴费单号',
    owner_id BIGINT NOT NULL COMMENT '业主ID',
    house_id BIGINT NOT NULL COMMENT '房屋ID',
    item_id BIGINT NOT NULL COMMENT '收费项目ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '应收金额',
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '实收金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '减免金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '缴费状态：0未缴费 1部分缴费 2已缴费 3逾期 4作废',
    pay_type TINYINT COMMENT '支付方式：1现金 2微信 3支付宝 4银行卡 5转账',
    pay_time DATETIME COMMENT '缴费时间',
    start_date DATE COMMENT '收费开始日期',
    end_date DATE COMMENT '收费结束日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_fee_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_fee_house FOREIGN KEY (house_id) REFERENCES community_house(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_fee_item FOREIGN KEY (item_id) REFERENCES fee_item(id) ON UPDATE CASCADE ON DELETE RESTRICT
) COMMENT '缴费记录表';
```
**索引**: `idx_fee_owner (owner_id)`, `idx_fee_house (house_id)`, `idx_fee_item (item_id)`, `idx_fee_status (status)`, `idx_fee_no (fee_no)`

---

### 2.4 设备管理表

#### 2.4.1 equipment_category - 设备分类表
```sql
CREATE TABLE equipment_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，顶级为0',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '设备分类表';
```
**索引**: `idx_category_parent (parent_id)`

#### 2.4.2 equipment - 设备台账表
```sql
CREATE TABLE equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设备ID',
    equipment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    category_id BIGINT NOT NULL COMMENT '设备分类ID',
    brand VARCHAR(50) COMMENT '品牌',
    model VARCHAR(50) COMMENT '型号',
    spec VARCHAR(500) COMMENT '规格参数',
    location VARCHAR(200) COMMENT '安装位置',
    building_id BIGINT COMMENT '所属楼栋ID',
    floor VARCHAR(20) COMMENT '楼层',
    install_date DATE COMMENT '安装日期',
    warranty_end_date DATE COMMENT '保修截止日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 2故障 3维修中 4停用 5报废',
    qr_code VARCHAR(255) COMMENT '二维码地址',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_equip_category FOREIGN KEY (category_id) REFERENCES equipment_category(id) ON UPDATE CASCADE ON DELETE RESTRICT
) COMMENT '设备台账表';
```
**索引**: `idx_equip_category (category_id)`, `idx_equip_building (building_id)`, `idx_equip_status (status)`, `idx_equip_no (equipment_no)`

#### 2.4.3 equipment_maintenance - 设备维护记录表
```sql
CREATE TABLE equipment_maintenance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '维护记录ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    maintenance_type TINYINT NOT NULL COMMENT '维护类型：1日常巡检 2定期保养 3故障维修 4更换配件 5其他',
    maintenance_content TEXT COMMENT '维护内容',
    maintenance_personnel_id BIGINT COMMENT '维护人员ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    cost DECIMAL(10,2) COMMENT '维护费用',
    parts_replaced VARCHAR(500) COMMENT '更换配件',
    next_maintenance_date DATE COMMENT '下次维护日期',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待维护 1进行中 2已完成 3取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '设备维护记录表';
```
**索引**: `idx_maint_equip (equipment_id)`, `idx_maint_personnel (maintenance_personnel_id)`, `idx_maint_status (status)`

---

### 2.5 报修管理表

#### 2.5.1 repair_record - 报修记录表
```sql
CREATE TABLE repair_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报修ID',
    repair_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报修单号',
    owner_id BIGINT NOT NULL COMMENT '报修业主ID',
    house_id BIGINT NOT NULL COMMENT '报修房屋ID',
    repair_type VARCHAR(50) COMMENT '报修类型：水电/门窗/家电/公共设施/其他',
    repair_content TEXT NOT NULL COMMENT '报修内容',
    repair_images VARCHAR(1000) COMMENT '报修图片，逗号分隔',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待派单 1处理中 2待评价 3已完成 4已取消',
    priority TINYINT NOT NULL DEFAULT 1 COMMENT '优先级：1普通 2加急 3紧急',
    handler_id BIGINT NULL COMMENT '处理人ID',
    handle_content TEXT COMMENT '处理内容',
    handle_images VARCHAR(1000) COMMENT '处理图片，逗号分隔',
    handle_time DATETIME COMMENT '处理完成时间',
    evaluate_score TINYINT COMMENT '评价分数：1-5',
    evaluate_content VARCHAR(500) COMMENT '评价内容',
    evaluate_time DATETIME COMMENT '评价时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_repair_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_repair_house FOREIGN KEY (house_id) REFERENCES community_house(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_repair_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '报修记录表';
```
**索引**: `idx_repair_owner (owner_id)`, `idx_repair_house (house_id)`, `idx_repair_handler (handler_id)`, `idx_repair_status (status)`, `idx_repair_no (repair_no)`

---

### 2.6 投诉建议表

#### 2.6.1 complaint_suggest - 投诉建议表
```sql
CREATE TABLE complaint_suggest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投诉ID',
    complaint_no VARCHAR(50) NOT NULL UNIQUE COMMENT '投诉单号',
    owner_id BIGINT NOT NULL COMMENT '投诉业主ID',
    house_id BIGINT NOT NULL COMMENT '投诉房屋ID',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1投诉 2建议 3咨询 4表扬',
    category VARCHAR(50) COMMENT '分类：环境卫生/噪音扰民/车辆管理/服务态度/设施损坏/其他',
    content TEXT NOT NULL COMMENT '投诉建议内容',
    images VARCHAR(1000) COMMENT '图片，逗号分隔',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待受理 1处理中 2已回复 3已关闭 4已撤销',
    priority TINYINT NOT NULL DEFAULT 1 COMMENT '优先级：1普通 2重要 3紧急',
    handler_id BIGINT NULL COMMENT '处理人ID',
    handle_content TEXT COMMENT '处理回复内容',
    handle_time DATETIME COMMENT '处理时间',
    is_anonymous TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：0否 1是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_complaint_owner FOREIGN KEY (owner_id) REFERENCES community_owner(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_complaint_house FOREIGN KEY (house_id) REFERENCES community_house(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_complaint_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '投诉建议表';
```
**索引**: `idx_complaint_owner (owner_id)`, `idx_complaint_house (house_id)`, `idx_complaint_handler (handler_id)`, `idx_complaint_status (status)`, `idx_complaint_no (complaint_no)`

---

### 2.7 公告通知表

#### 2.7.1 announcement - 公告表
```sql
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
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
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_announcement_creator FOREIGN KEY (creator_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '公告表';
```
**索引**: `idx_announcement_creator (creator_id)`, `idx_announcement_status (publish_status)`, `idx_announcement_type (type)`

#### 2.7.2 announcement_read - 公告阅读记录表
```sql
CREATE TABLE announcement_read (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    announcement_id BIGINT NOT NULL COMMENT '公告ID',
    user_id BIGINT NOT NULL COMMENT '阅读用户ID',
    read_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    UNIQUE KEY uk_announcement_user (announcement_id, user_id)
) COMMENT '公告阅读记录表';
```
**索引**: `idx_read_announcement (announcement_id)`, `idx_read_user (user_id)`

---

### 2.8 巡检管理表

#### 2.8.1 inspection_plan - 巡检计划表
```sql
CREATE TABLE inspection_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    plan_type TINYINT NOT NULL DEFAULT 1 COMMENT '计划类型：1日常巡检 2专项巡检 3季节性巡检 4临时巡检',
    frequency_type TINYINT COMMENT '频次类型：1每日 2每周 3每月 4每季度 5每半年 6每年 7一次性',
    frequency_value VARCHAR(100) COMMENT '频次值：如每周几、每月几号、cron表达式等',
    start_date DATE COMMENT '生效开始日期',
    end_date DATE COMMENT '生效结束日期',
    start_time TIME COMMENT '巡检开始时间',
    end_time TIME COMMENT '巡检结束时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    creator_id BIGINT COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_plan_creator FOREIGN KEY (creator_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '巡检计划表';
```
**索引**: `idx_plan_status (status)`, `idx_plan_creator (creator_id)`

#### 2.8.2 inspection_plan_equipment - 巡检计划-设备关联表
```sql
CREATE TABLE inspection_plan_equipment (
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    PRIMARY KEY (plan_id, equipment_id),
    CONSTRAINT fk_ipe_plan FOREIGN KEY (plan_id) REFERENCES inspection_plan(id) ON DELETE CASCADE,
    CONSTRAINT fk_ipe_equip FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
) COMMENT '巡检计划-设备关联表';
```

#### 2.8.3 inspection_plan_inspector - 巡检计划-巡检人员关联表
```sql
CREATE TABLE inspection_plan_inspector (
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    inspector_id BIGINT NOT NULL COMMENT '巡检人员ID',
    PRIMARY KEY (plan_id, inspector_id),
    CONSTRAINT fk_ipi_plan FOREIGN KEY (plan_id) REFERENCES inspection_plan(id) ON DELETE CASCADE,
    CONSTRAINT fk_ipi_inspector FOREIGN KEY (inspector_id) REFERENCES sys_user(id) ON DELETE CASCADE
) COMMENT '巡检计划-巡检人员关联表';
```

#### 2.8.4 inspection_record - 巡检记录表
```sql
CREATE TABLE inspection_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    plan_id BIGINT COMMENT '计划ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    inspector_user_id BIGINT COMMENT '巡检人员ID',
    inspection_time DATETIME NOT NULL COMMENT '巡检时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '巡检结果：1正常 2异常 3未巡检',
    abnormal_desc VARCHAR(500) COMMENT '异常描述',
    abnormal_images VARCHAR(1000) COMMENT '异常图片，逗号分隔',
    handle_status TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态：0待处理 1处理中 2已处理 3忽略',
    handle_content TEXT COMMENT '处理内容',
    handle_time DATETIME COMMENT '处理时间',
    handler_id BIGINT COMMENT '处理人ID',
    location_lat DECIMAL(10,7) COMMENT '纬度',
    location_lng DECIMAL(10,7) COMMENT '经度',
    location_address VARCHAR(200) COMMENT '定位地址',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    CONSTRAINT fk_inspect_plan FOREIGN KEY (plan_id) REFERENCES inspection_plan(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_inspect_equip FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_inspect_inspector_user FOREIGN KEY (inspector_user_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_inspect_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id) ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT '巡检记录表';
```
**索引**: `idx_inspect_plan (plan_id)`, `idx_inspect_equip (equipment_id)`, `idx_inspect_inspector_user (inspector_user_id)`, `idx_inspect_handler (handler_id)`, `idx_inspect_time (inspection_time)`

---

## 3. 枚举值参考

### 3.1 系统模块枚举

| 枚举名 | 值 | 说明 |
|--------|-----|------|
| **UserType** | 1 | 超级管理员 |
| | 2 | 物业管理员 |
| | 3 | 业主 |
| **UserStatus** | 0 | 禁用 |
| | 1 | 正常 |
| **MenuType** | 0 | 目录 |
| | 1 | 菜单 |
| | 2 | 按钮 |
| **EnableStatus** | 0 | 禁用 |
| | 1 | 启用 |

### 3.2 小区模块枚举

| 枚举名 | 值 | 说明 |
|--------|-----|------|
| **HouseStatus** | 0 | 空置 |
| | 1 | 已入住 |
| | 2 | 出租 |
| **OwnerType** | 1 | 本人 |
| | 2 | 家属 |
| | 3 | 租客 |
| **OwnerStatus** | 0 | 禁用 |
| | 1 | 正常 |
| **ParkingType** | 1 | 地上 |
| | 2 | 地下 |
| **ParkingStatus** | 0 | 空闲 |
| | 1 | 已租 |
| | 2 | 已售 |
| | 3 | 维修中 |

### 3.3 收费模块枚举

| 枚举名 | 值 | 说明 |
|--------|-----|------|
| **FeeItemType** | 1 | 物业费 |
| | 2 | 车位费 |
| | 3 | 水费 |
| | 4 | 电费 |
| | 5 | 燃气费 |
| | 6 | 暖气费 |
| | 9 | 其他 |
| **FeeCycleType** | 1 | 按月 |
| | 2 | 按季 |
| | 3 | 按半年 |
| | 4 | 按年 |
| | 5 | 一次性 |
| **FeeRecordStatus** | 0 | 未缴费 |
| | 1 | 部分缴费 |
| | 2 | 已缴费 |
| | 3 | 逾期 |
| | 4 | 作废 |
| **PayType** | 1 | 现金 |
| | 2 | 微信 |
| | 3 | 支付宝 |
| | 4 | 银行卡 |
| | 5 | 转账 |
| | 6 | 其他 |

### 3.4 设备模块枚举

| 枚举名 | 值 | 说明 |
|--------|-----|------|
| **EquipmentStatus** | 1 | 正常 |
| | 2 | 故障 |
| | 3 | 维修中 |
| | 4 | 停用 |
| | 5 | 报废 |
| **MaintenanceType** | 1 | 日常巡检 |
| | 2 | 定期保养 |
| | 3 | 故障维修 |
| | 4 | 更换配件 |
| | 5 | 其他 |
| **MaintenanceStatus** | 0 | 待维护 |
| | 1 | 进行中 |
| | 2 | 已完成 |
| | 3 | 取消 |

### 3.5 报修模块枚举

| 枚举名 | 值 | 说明 |
|--------|-----|------|
| **RepairStatus** | 0 | 待派单 |
| | 1 | 处理中 |
| | 2 | 待评价 |
| | 3 | 已完成 |
| | 4 | 已取消 |
| **RepairPriority** | 1 | 普通 |
| | 2 | 加急 |
| | 3 | 紧急 |

### 3.6 投诉模块枚举

| 枚举名 | 值 | 说明 |
|--------|-----|------|
| **ComplaintType** | 1 | 投诉 |
| | 2 | 建议 |
| | 3 | 咨询 |
| | 4 | 表扬 |
| **ComplaintStatus** | 0 | 待受理 |
| | 1 | 处理中 |
| | 2 | 已回复 |
| | 3 | 已关闭 |
| | 4 | 已撤销 |
| **ComplaintPriority** | 1 | 普通 |
| | 2 | 重要 |
| | 3 | 紧急 |

### 3.7 公告模块枚举

| 枚举名 | 值 | 说明 |
|--------|-----|------|
| **AnnouncementType** | 1 | 通知公告 |
| | 2 | 政策法规 |
| | 3 | 便民服务 |
| | 4 | 活动通知 |
| | 5 | 紧急通知 |
| **PublishStatus** | 0 | 草稿 |
| | 1 | 发布 |
| | 2 | 下架 |

### 3.8 巡检模块枚举

| 枚举名 | 值 | 说明 |
|--------|-----|------|
| **InspectionPlanType** | 1 | 日常巡检 |
| | 2 | 专项巡检 |
| | 3 | 季节性巡检 |
| | 4 | 临时巡检 |
| **FrequencyType** | 1 | 每日 |
| | 2 | 每周 |
| | 3 | 每月 |
| | 4 | 每季度 |
| | 5 | 每半年 |
| | 6 | 每年 |
| | 7 | 一次性 |
| **InspectionStatus** | 1 | 正常 |
| | 2 | 异常 |
| | 3 | 未巡检 |
| **HandleStatus** | 0 | 待处理 |
| | 1 | 处理中 |
| | 2 | 已处理 |
| | 3 | 忽略 |

---

## 4. 视图设计 (可选)

### 4.1 业主房屋关联视图
```sql
CREATE VIEW v_owner_house AS
SELECT 
    o.id AS owner_id,
    o.name AS owner_name,
    o.phone AS owner_phone,
    o.owner_type,
    h.id AS house_id,
    h.room_no,
    h.area,
    h.house_type,
    h.house_status,
    b.id AS building_id,
    b.building_no,
    b.floor_count
FROM community_owner o
LEFT JOIN community_house h ON o.id = h.owner_id AND h.deleted = 0
LEFT JOIN community_building b ON h.building_id = b.id AND b.deleted = 0
WHERE o.deleted = 0;
```

### 4.2 缴费明细视图
```sql
CREATE VIEW v_fee_record_detail AS
SELECT 
    fr.id,
    fr.fee_no,
    o.name AS owner_name,
    o.phone AS owner_phone,
    h.room_no,
    b.building_no,
    fi.item_name,
    fi.item_type,
    fi.unit_price,
    fi.unit,
    fr.amount,
    fr.paid_amount,
    fr.discount_amount,
    fr.status,
    fr.pay_type,
    fr.pay_time,
    fr.start_date,
    fr.end_date
FROM fee_record fr
JOIN community_owner o ON fr.owner_id = o.id
JOIN community_house h ON fr.house_id = h.id
JOIN community_building b ON h.building_id = b.id
JOIN fee_item fi ON fr.item_id = fi.id
WHERE fr.deleted = 0 AND o.deleted = 0 AND h.deleted = 0 AND b.deleted = 0 AND fi.deleted = 0;
```

---

## 5. 存储过程/函数 (可选)

### 5.1 生成单号函数
```sql
DELIMITER //
CREATE FUNCTION generate_no(prefix VARCHAR(10)) 
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE seq INT;
    DECLARE no VARCHAR(50);
    SET seq = (SELECT COALESCE(MAX(CAST(SUBSTRING(fee_no, LENGTH(prefix)+1) AS UNSIGNED)), 0) + 1
               FROM fee_record WHERE fee_no LIKE CONCAT(prefix, '%'));
    SET no = CONCAT(prefix, DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(seq, 4, '0'));
    RETURN no;
END //
DELIMITER ;
```

---

## 6. 字典数据初始化

### 6.1 系统初始数据
```sql
-- 默认超级管理员 (密码: 123456 -> BCrypt加密)
INSERT INTO sys_user (username, password, real_name, user_type, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '超级管理员', 1, 1);

-- 默认角色
INSERT INTO sys_role (role_name, role_key, remark) VALUES 
('超级管理员', 'super_admin', '系统最高权限'),
('物业管理员', 'property_admin', '物业日常管理权限'),
('业主', 'owner', '业主基础权限');

-- 默认菜单
INSERT INTO sys_menu (parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES 
(0, '系统管理', '/system', 'Layout', '', 0, 1, 1),
(1, '用户管理', '/system/user', 'system/user/index', 'system:user:list', 1, 1, 1),
(1, '角色管理', '/system/role', 'system/role/index', 'system:role:list', 1, 2, 1),
(1, '菜单管理', '/system/menu', 'system/menu/index', 'system:menu:list', 1, 3, 1),
(0, '小区基础', '/community', 'Layout', '', 0, 2, 1),
(5, '楼栋管理', '/community/building', 'community/building/index', 'community:building:list', 1, 1, 1),
(5, '房屋管理', '/community/house', 'community/house/index', 'community:house:list', 1, 2, 1),
(5, '业主管理', '/community/owner', 'community/owner/index', 'community:owner:list', 1, 3, 1),
(5, '车位管理', '/community/parking', 'community/parking/index', 'community:parking:list', 1, 4, 1);
```

---

## 7. 维护建议

### 7.1 定期维护
- **每日**: 检查定时任务执行日志
- **每周**: 清理3个月前的操作日志、巡检记录
- **每月**: 分析慢查询，优化索引
- **每季度**: 归档历史缴费记录、报修记录

### 7.2 索引优化建议
- 高频查询字段建立联合索引
- 避免在WHERE子句中对索引列使用函数
- 定期执行 `ANALYZE TABLE` 更新统计信息

### 7.3 备份策略
- **全量备份**: 每日凌晨2点
- **增量备份**: 每小时
- **保留策略**: 全量保留30天，增量保留7天

---

*文档版本: v1.0*
*更新时间: 2024-07-14*