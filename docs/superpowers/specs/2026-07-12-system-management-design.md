# 系统管理模块设计文档

## 一、模块概述

系统管理模块是物业管理系统的基础模块，提供用户、角色、菜单、权限的管理功能。该模块为其他业务模块提供权限控制基础。

### 1.1 技术栈
- 后端：SpringBoot、MyBatis-Plus、MySQL 8.0、JWT、Spring Validation
- 前端：Vue3、Vite、Element Plus、Vue Router、Pinia、ECharts
- 架构：前后端分离架构

### 1.2 系统角色
1. 超级管理员：系统最高权限，负责权限、系统配置
2. 物业管理员：小区日常业务处理
3. 业主住户：个人线上服务

## 二、数据库设计

### 2.1 sys_user 系统用户表
```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像地址',
    user_type TINYINT NOT NULL DEFAULT 3 COMMENT '用户类型：1超级管理员 2物业管理员 3业主',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：0禁用 1正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
);
```

### 2.2 sys_role 角色表
```sql
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '权限标识',
    remark VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);
```

### 2.3 sys_user_role 用户角色关联表
```sql
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户id',
    role_id BIGINT NOT NULL COMMENT '角色id'
);
```

### 2.4 sys_menu 菜单权限表
```sql
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单id',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(255) COMMENT '前端路由',
    component VARCHAR(255) COMMENT '前端组件地址',
    perms VARCHAR(100) COMMENT '权限标识',
    menu_type TINYINT COMMENT '类型：0目录 1菜单 2按钮',
    sort INT DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '启用状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);
```

### 2.5 sys_role_menu 角色菜单关联表
```sql
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色id',
    menu_id BIGINT NOT NULL COMMENT '菜单id'
);
```

### 2.6 sys_oper_log 操作日志表
```sql
CREATE TABLE sys_oper_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) COMMENT '操作人账号',
    oper_module VARCHAR(50) COMMENT '操作模块',
    oper_type VARCHAR(20) COMMENT '操作类型（新增/编辑/删除）',
    oper_ip VARCHAR(50) COMMENT '请求ip',
    oper_desc VARCHAR(500) COMMENT '操作描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 三、后端API设计

### 3.1 用户管理API
```
POST   /api/user/login          # 用户登录
POST   /api/user/logout         # 退出登录
GET    /api/user/info           # 获取当前用户信息
POST   /api/user                # 新增用户
PUT    /api/user                # 编辑用户
DELETE /api/user/{id}           # 删除用户
PUT    /api/user/status         # 修改用户状态
PUT    /api/user/resetPassword  # 重置密码
GET    /api/user/page           # 分页查询用户列表
```

### 3.2 角色管理API
```
POST   /api/role                # 新增角色
PUT    /api/role                # 编辑角色
DELETE /api/role/{id}           # 删除角色
GET    /api/role/list           # 获取所有角色列表
GET    /api/role/{id}           # 获取角色详情
PUT    /api/role/menu           # 分配菜单权限
GET    /api/role/{id}/menus     # 获取角色菜单权限
```

### 3.3 菜单权限API
```
POST   /api/menu                # 新增菜单
PUT    /api/menu                # 编辑菜单
DELETE /api/menu/{id}           # 删除菜单
GET    /api/menu/list           # 获取菜单列表（树形）
GET    /api/menu/{id}           # 获取菜单详情
GET    /api/menu/tree           # 获取菜单树（用于角色分配）
```

### 3.4 操作日志API
```
GET    /api/operlog/page        # 分页查询操作日志
DELETE /api/operlog/clean       # 清空操作日志
```

## 四、前端页面设计

### 4.1 登录页面
- 用户名/密码输入框
- 登录按钮
- 记住密码选项

### 4.2 后台管理布局
- 左侧菜单栏（动态渲染）
- 顶部导航栏（用户信息、退出按钮）
- 主内容区域

### 4.3 用户管理页面
- 用户列表表格（分页）
- 新增/编辑用户弹窗
- 用户状态切换
- 重置密码功能

### 4.4 角色管理页面
- 角色列表表格
- 新增/编辑角色弹窗
- 分配菜单权限弹窗

### 4.5 菜单管理页面
- 菜单树形表格
- 新增/编辑菜单弹窗
- 菜单图标选择

### 4.6 操作日志页面
- 日志列表表格（分页）
- 清空日志功能

## 五、实现顺序

按照自顶向下原则，实现顺序为：
1. 创建数据库表结构
2. 后端基础架构搭建
3. 后端API实现
4. 前端框架搭建
5. 前端页面实现

## 六、数据库连接配置

- 数据库名称：property_management_system
- 用户名：root
- 密码：123456
- 默认管理员账号：root
- 默认管理员密码：123456
