-- 创建数据库
CREATE DATABASE IF NOT EXISTS property_management_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE property_management_system;

-- 创建系统用户表
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
) COMMENT '系统用户表';

-- 创建角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '权限标识',
    remark VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '角色表';

-- 创建用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户id',
    role_id BIGINT NOT NULL COMMENT '角色id'
) COMMENT '用户角色关联表';

-- 创建菜单权限表
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
) COMMENT '菜单权限表';

-- 创建角色菜单关联表
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色id',
    menu_id BIGINT NOT NULL COMMENT '菜单id'
) COMMENT '角色菜单关联表';

-- 创建操作日志表
CREATE TABLE sys_oper_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) COMMENT '操作人账号',
    oper_module VARCHAR(50) COMMENT '操作模块',
    oper_type VARCHAR(20) COMMENT '操作类型（新增/编辑/删除）',
    oper_ip VARCHAR(50) COMMENT '请求ip',
    oper_desc VARCHAR(500) COMMENT '操作描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '操作日志表';

-- 插入默认超级管理员账号
INSERT INTO sys_user (username, password, real_name, user_type, status) 
VALUES ('root', '$2b$10$6kyfYFRRM1W2cadzuB9OlOGL0f4nyKeLyEd1oyg2cq94lGQuFZRWC', '超级管理员', 1, 1);

-- 插入默认角色
INSERT INTO sys_role (role_name, role_key, remark) VALUES 
('超级管理员', 'admin', '系统最高权限'),
('物业管理员', 'property_admin', '小区日常业务处理'),
('业主', 'owner', '个人线上服务');

-- 插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 插入默认菜单
INSERT INTO sys_menu (parent_id, menu_name, path, component, perms, menu_type, sort, status) VALUES
(0, '系统管理', '/system', NULL, NULL, 0, 1, 1),
(1, '用户管理', '/system/user', 'system/user/index', 'system:user:list', 1, 1, 1),
(1, '角色管理', '/system/role', 'system/role/index', 'system:role:list', 1, 2, 1),
(1, '菜单管理', '/system/menu', 'system/menu/index', 'system:menu:list', 1, 3, 1),
(1, '操作日志', '/system/operlog', 'system/operlog/index', 'system:operlog:list', 1, 4, 1);

-- 插入角色菜单关联（超级管理员拥有所有权限）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5);