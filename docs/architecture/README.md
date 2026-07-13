# 系统架构设计文档

## 一、概述

本文档描述物业管理系统的整体架构设计，包括技术架构、应用架构、数据架构和部署架构。

---

## 二、技术架构

### 2.1 技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **后端框架** | SpringBoot | 4.0.8-SNAPSHOT | 基于Spring Boot 4.x |
| **ORM框架** | MyBatis-Plus | 3.5.3.1 | 简化数据库操作 |
| **数据库** | MySQL | 8.0 | 关系型数据库 |
| **认证授权** | JWT | 0.11.5 | JSON Web Token |
| **参数校验** | Jakarta Validation | - | 兼容Spring Boot 4.x |
| **安全框架** | Spring Security | - | 安全认证（已禁用默认配置） |
| **前端框架** | Vue3 | 3.x | 渐进式JavaScript框架 |
| **构建工具** | Vite | - | 下一代前端构建工具 |
| **UI组件库** | Element Plus | - | Vue3组件库 |
| **路由** | Vue Router | 4.x | 官方路由管理 |
| **状态管理** | Pinia | - | 官方状态管理 |
| **HTTP客户端** | Axios | - | 基于Promise的HTTP客户端 |

### 2.2 技术架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      前端层 (Vue3 + Element Plus)             │
├─────────────────────────────────────────────────────────────┤
│  Vue Router (路由)  │  Pinia (状态)  │  Axios (HTTP)         │
├─────────────────────────────────────────────────────────────┤
│                      页面组件层                              │
│  系统管理  │  小区管理  │  收费管理  │  其他模块...            │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP请求
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      后端层 (SpringBoot 4.x)                 │
├─────────────────────────────────────────────────────────────┤
│  Controller层    │  Service层      │  Mapper层               │
│  (请求处理)      │  (业务逻辑)     │  (数据访问)             │
├─────────────────────────────────────────────────────────────┤
│  公共组件层                                               │
│  Result │ BusinessException │ JwtUtils │ PasswordUtils       │
├─────────────────────────────────────────────────────────────┤
│  配置层                                                    │
│  WebMvcConfig │ SecurityConfig │ MyBatisPlusConfig           │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ JDBC
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      数据层 (MySQL 8.0)                     │
├─────────────────────────────────────────────────────────────┤
│  系统管理表  │  小区信息表  │  收费记录表  │  其他表...        │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、应用架构

### 3.1 项目结构

```
propertyManagementSystem/
├── src/main/java/com/lsy/propertymanagementsystem/
│   ├── common/                    # 公共组件
│   │   ├── Result.java           # 统一响应封装
│   │   ├── ResultCode.java       # 响应码枚举
│   │   ├── BusinessException.java # 业务异常
│   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   ├── JwtUtils.java         # JWT工具类
│   │   ├── PasswordUtils.java    # 密码工具类
│   │   └── Constants.java        # 常量定义
│   ├── config/                    # 配置类
│   │   ├── WebMvcConfig.java     # Web MVC配置
│   │   └── SecurityConfig.java   # 安全配置
│   ├── entity/                    # 实体类
│   │   ├── SysUser.java          # 系统用户
│   │   ├── SysRole.java          # 系统角色
│   │   ├── SysMenu.java          # 系统菜单
│   │   ├── SysOperLog.java       # 操作日志
│   │   ├── CommunityBuilding.java # 楼栋
│   │   ├── CommunityHouse.java   # 房屋
│   │   ├── CommunityOwner.java   # 业主
│   │   ├── CommunityParking.java  # 车位
│   │   ├── FeeItem.java          # 收费项目
│   │   └── FeeRecord.java        # 收费记录
│   ├── mapper/                    # Mapper接口
│   │   ├── SysUserMapper.java
│   │   ├── SysRoleMapper.java
│   │   ├── SysMenuMapper.java
│   │   ├── SysOperLogMapper.java
│   │   ├── CommunityBuildingMapper.java
│   │   ├── CommunityHouseMapper.java
│   │   ├── CommunityOwnerMapper.java
│   │   ├── CommunityParkingMapper.java
│   │   ├── FeeItemMapper.java
│   │   └── FeeRecordMapper.java
│   ├── dto/                       # 数据传输对象
│   │   └── request/              # 请求DTO
│   │       ├── LoginRequest.java
│   │       ├── UserRequest.java
│   │       ├── RoleRequest.java
│   │       ├── MenuRequest.java
│   │       ├── BuildingRequest.java
│   │       ├── HouseRequest.java
│   │       ├── OwnerRequest.java
│   │       ├── ParkingRequest.java
│   │       ├── FeeItemRequest.java
│   │       └── FeeRecordRequest.java
│   ├── service/                   # 服务层
│   │   ├── AuthService.java
│   │   ├── SysUserService.java
│   │   ├── SysRoleService.java
│   │   ├── SysMenuService.java
│   │   ├── SysOperLogService.java
│   │   ├── CommunityBuildingService.java
│   │   ├── CommunityHouseService.java
│   │   ├── CommunityOwnerService.java
│   │   ├── CommunityParkingService.java
│   │   ├── FeeItemService.java
│   │   ├── FeeRecordService.java
│   │   └── impl/                 # 服务实现
│   ├── controller/                # 控制器层
│   │   ├── AuthController.java
│   │   ├── SysUserController.java
│   │   ├── SysRoleController.java
│   │   ├── SysMenuController.java
│   │   ├── SysOperLogController.java
│   │   ├── CommunityBuildingController.java
│   │   ├── CommunityHouseController.java
│   │   ├── CommunityOwnerController.java
│   │   ├── CommunityParkingController.java
│   │   ├── FeeItemController.java
│   │   └── FeeRecordController.java
│   └── interceptor/               # 拦截器
│       └── JwtInterceptor.java
├── frontend/                      # 前端项目
│   └── src/
│       ├── api/                   # API接口
│       ├── views/                 # 页面组件
│       ├── router/                # 路由配置
│       ├── store/                 # 状态管理
│       └── utils/                 # 工具类
├── sql/                           # 数据库脚本
└── docs/                          # 项目文档
```

### 3.2 分层架构

#### 3.2.1 表现层（Controller）

- 接收HTTP请求，解析请求参数
- 调用Service层处理业务逻辑
- 返回统一的Result响应格式
- 使用Jakarta Validation进行参数校验

#### 3.2.2 业务层（Service）

- 实现业务逻辑
- 事务管理
- 数据校验
- 异常处理

#### 3.2.3 数据访问层（Mapper）

- 继承MyBatis-Plus的BaseMapper
- 提供基本的CRUD操作
- 支持复杂的SQL查询

#### 3.2.4 公共组件层（Common）

- Result: 统一响应封装
- BusinessException: 业务异常定义
- JwtUtils: JWT Token生成和解析
- PasswordUtils: 密码加密和验证

### 3.3 认证授权流程

```
用户登录 → AuthController.login()
    ↓
验证用户名密码 → AuthService.login()
    ↓
生成JWT Token → JwtUtils.generateToken()
    ↓
返回Token → 前端存储Token
    ↓
后续请求携带Token → JwtInterceptor拦截器验证
    ↓
验证通过 → 放行请求
验证失败 → 返回401错误
```

---

## 四、数据架构

### 4.1 数据库设计原则

1. **规范化设计**：遵循第三范式，减少数据冗余
2. **逻辑删除**：所有表使用deleted字段进行逻辑删除
3. **统一主键**：所有表使用BIGINT自增主键
4. **时间字段**：所有表包含create_time和update_time字段
5. **索引优化**：根据查询需求创建合适的索引

### 4.2 数据库表分类

| 模块 | 表名 | 说明 |
|------|------|------|
| **系统管理** | sys_user | 用户表 |
| | sys_role | 角色表 |
| | sys_menu | 菜单表 |
| | sys_user_role | 用户角色关联表 |
| | sys_role_menu | 角色菜单关联表 |
| | sys_oper_log | 操作日志表 |
| **小区信息** | community_building | 楼栋表 |
| | community_house | 房屋表 |
| | community_owner | 业主表 |
| | community_parking | 车位表 |
| **物业收费** | fee_item | 收费项目表 |
| | fee_record | 缴费记录表 |

### 4.3 ER关系图

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  sys_user   │────<│sys_user_role│>────│  sys_role   │
└─────────────┘     └─────────────┘     └─────────────┘
                                               │
                                               │
                                        ┌─────────────┐
                                        │sys_role_menu│
                                        └─────────────┘
                                               │
                                               │
                                        ┌─────────────┐
                                        │  sys_menu   │
                                        └─────────────┘

┌─────────────────┐     ┌─────────────────┐
│community_building│────<│ community_house │
└─────────────────┘     └─────────────────┘
                               │
                               │
                        ┌─────────────────┐
                        │community_owner  │
                        └─────────────────┘
                               │
                               │
                        ┌─────────────────┐
                        │community_parking│
                        └─────────────────┘

┌─────────────┐     ┌─────────────┐
│  fee_item   │────<│ fee_record  │
└─────────────┘     └─────────────┘
```

---

## 五、部署架构

### 5.1 开发环境

```
┌─────────────────────────────────────┐
│         开发机器                      │
├─────────────────────────────────────┤
│  后端服务 (localhost:8080)           │
│  前端服务 (localhost:5173)           │
│  MySQL (localhost:3306)             │
└─────────────────────────────────────┘
```

### 5.2 生产环境

```
┌─────────────────────────────────────┐
│         Nginx (反向代理)              │
├─────────────────────────────────────┤
│  前端静态资源                         │
└─────────────────────────────────────┘
              │
              │ 转发API请求
              ▼
┌─────────────────────────────────────┐
│      SpringBoot应用服务器             │
│      (端口: 8080)                    │
└─────────────────────────────────────┘
              │
              │ JDBC连接
              ▼
┌─────────────────────────────────────┐
│      MySQL数据库服务器               │
│      (端口: 3306)                    │
└─────────────────────────────────────┘
```

### 5.3 Nginx配置示例

```nginx
server {
    listen 80;
    server_name example.com;
    
    # 前端静态资源
    location / {
        root /path/to/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 六、安全设计

### 6.1 认证机制

- 采用JWT Token进行身份认证
- Token有效期为24小时
- Token存储在前端localStorage中
- 每次请求携带Token进行验证

### 6.2 授权机制

- 基于RBAC（基于角色的访问控制）模型
- 用户 → 角色 → 菜单/权限
- 支持细粒度的权限控制

### 6.3 安全措施

1. **密码安全**：使用BCrypt加密存储
2. **SQL注入防护**：使用MyBatis-Plus预编译
3. **XSS防护**：前端输入过滤，后端输出编码
4. **CSRF防护**：已禁用Spring Security默认CSRF
5. **CORS配置**：已配置跨域资源共享

### 6.4 接口安全

- 所有API接口需要携带JWT Token
- Token过期返回401错误
- 无权限返回403错误
- 操作日志记录所有关键操作

---

## 七、扩展性设计

### 7.1 模块化设计

- 系统采用模块化架构
- 每个业务模块独立开发
- 模块间通过接口交互
- 支持按需启用/禁用模块

### 7.2 多小区支持（预留）

- 数据库设计预留community_id字段
- 支持后续扩展多小区功能
- 租户数据隔离

### 7.3 性能优化

- 数据库索引优化
- 分页查询支持
- 接口响应数据最小化
- 前端路由懒加载

---

## 八、开发规范

### 8.1 代码规范

- 后端遵循阿里巴巴Java开发手册
- 前端遵循Vue3 Composition API风格
- 使用ESLint进行代码检查
- 使用Prettier进行代码格式化

### 8.2 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | 大驼峰 | SysUser, CommunityBuilding |
| 方法名 | 小驼峰 | getPageList, addData |
| 变量名 | 小驼峰 | userName, createTime |
| 常量 | 全大写下划线 | MAX_PAGE_SIZE |
| 数据库表名 | 小写下划线 | sys_user, community_house |
| 数据库字段名 | 小写下划线 | user_name, create_time |

### 8.3 提交规范

- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关
