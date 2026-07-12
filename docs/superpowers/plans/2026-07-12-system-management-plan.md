# 系统管理模块实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现物业管理系统的系统管理模块，包括用户、角色、菜单、权限管理功能

**Architecture:** 采用SpringBoot+MyBatisPlus+MySQL后端架构，Vue3+Element Plus前端架构，前后端分离设计

**Tech Stack:** SpringBoot 4.0.8-SNAPSHOT、MyBatis-Plus、MySQL 8.0、JWT、Vue3、Vite、Element Plus

---

## 文件结构规划

### 后端文件结构
```
src/main/java/com/lsy/propertymanagementsystem/
├── common/
│   ├── result/
│   │   ├── Result.java              # 统一返回结果
│   │   └── ResultCode.java          # 返回状态码
│   ├── exception/
│   │   ├── BusinessException.java   # 业务异常
│   │   └── GlobalExceptionHandler.java # 全局异常处理
│   ├── utils/
│   │   ├── JwtUtils.java            # JWT工具类
│   │   └── PasswordUtils.java       # 密码加密工具
│   └── constant/
│       └── Constants.java           # 常量定义
├── entity/
│   ├── SysUser.java                 # 用户实体
│   ├── SysRole.java                 # 角色实体
│   ├── SysMenu.java                 # 菜单实体
│   ├── SysUserRole.java             # 用户角色关联实体
│   ├── SysRoleMenu.java             # 角色菜单关联实体
│   └── SysOperLog.java              # 操作日志实体
├── mapper/
│   ├── SysUserMapper.java           # 用户Mapper
│   ├── SysRoleMapper.java           # 角色Mapper
│   ├── SysMenuMapper.java           # 菜单Mapper
│   ├── SysUserRoleMapper.java       # 用户角色Mapper
│   ├── SysRoleMenuMapper.java       # 角色菜单Mapper
│   └── SysOperLogMapper.java        # 操作日志Mapper
├── service/
│   ├── SysUserService.java          # 用户Service接口
│   ├── impl/
│   │   └── SysUserServiceImpl.java  # 用户Service实现
│   ├── SysRoleService.java          # 角色Service接口
│   ├── impl/
│   │   └── SysRoleServiceImpl.java  # 角色Service实现
│   ├── SysMenuService.java          # 菜单Service接口
│   ├── impl/
│   │   └── SysMenuServiceImpl.java  # 菜单Service实现
│   ├── SysOperLogService.java       # 操作日志Service接口
│   ├── impl/
│   │   └── SysOperLogServiceImpl.java # 操作日志Service实现
│   └── AuthService.java             # 认证Service接口
│       └── impl/
│           └── AuthServiceImpl.java # 认证Service实现
├── controller/
│   ├── SysUserController.java       # 用户Controller
│   ├── SysRoleController.java       # 角色Controller
│   ├── SysMenuController.java       # 菜单Controller
│   └── SysOperLogController.java    # 操作日志Controller
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java        # 登录请求
│   │   ├── UserRequest.java         # 用户请求
│   │   ├── RoleRequest.java         # 角色请求
│   │   └── MenuRequest.java         # 菜单请求
│   └── response/
│       ├── LoginResponse.java       # 登录响应
│       ├── UserResponse.java        # 用户响应
│       └── MenuResponse.java        # 菜单响应
└── interceptor/
    └── JwtInterceptor.java          # JWT拦截器
```

### 前端文件结构
```
frontend/
├── src/
│   ├── api/
│   │   ├── user.js                  # 用户API
│   │   ├── role.js                  # 角色API
│   │   ├── menu.js                  # 菜单API
│   │   └── operlog.js               # 操作日志API
│   ├── views/
│   │   ├── login/
│   │   │   └── index.vue            # 登录页面
│   │   ├── layout/
│   │   │   └── index.vue            # 后台布局
│   │   ├── system/
│   │   │   ├── user/
│   │   │   │   └── index.vue        # 用户管理
│   │   │   ├── role/
│   │   │   │   └── index.vue        # 角色管理
│   │   │   ├── menu/
│   │   │   │   └── index.vue        # 菜单管理
│   │   │   └── operlog/
│   │   │       └── index.vue        # 操作日志
│   │   └── dashboard/
│   │       └── index.vue            # 首页
│   ├── router/
│   │   └── index.js                 # 路由配置
│   ├── store/
│   │   └── user.js                  # 用户状态管理
│   ├── utils/
│   │   ├── request.js               # 请求封装
│   │   └── auth.js                  # 认证工具
│   ├── components/
│   │   └── Pagination.vue           # 分页组件
│   ├── App.vue
│   └── main.js
├── package.json
├── vite.config.js
└── index.html
```

---

## Task 1: 创建数据库表结构

**Files:**
- Create: `sql/init.sql`

- [ ] **Step 1: 创建数据库初始化脚本**

```sql
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
VALUES ('root', '$2a$10$VQECw7SiNIH8Mn0CFqC3IOwBjKJYVqDQxQZQZQZQZQZQZQZQZQZQ', '超级管理员', 1, 1);

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
```

- [ ] **Step 2: 执行SQL脚本**

Run: `mysql -u root -p123456 < sql/init.sql`
Expected: 数据库和表创建成功，默认数据插入成功

- [ ] **Step 3: 提交**

```bash
git add sql/init.sql
git commit -m "feat: 创建数据库表结构和初始数据"
```

---

## Task 2: 后端基础架构搭建

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/common/result/Result.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/common/result/ResultCode.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/common/exception/BusinessException.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/common/exception/GlobalExceptionHandler.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/common/utils/JwtUtils.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/common/utils/PasswordUtils.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/common/constant/Constants.java`
- Modify: `src/main/resources/application.properties`

- [ ] **Step 1: 创建统一返回结果类**

```java
package com.lsy.propertymanagementsystem.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        return result;
    }
}
```

- [ ] **Step 2: 创建返回状态码枚举**

```java
package com.lsy.propertymanagementsystem.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),
    BAD_REQUEST(400, "请求参数错误");

    private final Integer code;
    private final String msg;
}
```

- [ ] **Step 3: 创建业务异常类**

```java
package com.lsy.propertymanagementsystem.common.exception;

import com.lsy.propertymanagementsystem.common.result.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String msg) {
        super(msg);
        this.code = ResultCode.ERROR.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }
}
```

- [ ] **Step 4: 创建全局异常处理**

```java
package com.lsy.propertymanagementsystem.common.exception;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.common.result.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.error(ResultCode.ERROR);
    }
}
```

- [ ] **Step 5: 创建JWT工具类**

```java
package com.lsy.propertymanagementsystem.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    private static final String SECRET = "propertyManagementSystemSecretKeyForJwtTokenGeneration";
    private static final long EXPIRATION = 24 * 60 * 60 * 1000; // 24小时

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public static boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
```

- [ ] **Step 6: 创建密码加密工具类**

```java
package com.lsy.propertymanagementsystem.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

- [ ] **Step 7: 创建常量类**

```java
package com.lsy.propertymanagementsystem.common.constant;

public class Constants {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final Integer USER_TYPE_ADMIN = 1;
    public static final Integer USER_TYPE_PROPERTY_ADMIN = 2;
    public static final Integer USER_TYPE_OWNER = 3;
    public static final Integer STATUS_DISABLE = 0;
    public static final Integer STATUS_ENABLE = 1;
    public static final Integer DELETED_NO = 0;
    public static final Integer DELETED_YES = 1;
}
```

- [ ] **Step 8: 更新application.properties**

```properties
spring.application.name=propertyManagementSystem

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/property_management_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis-Plus配置
mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml
mybatis-plus.configuration.map-underscore-to-camel-case=true
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

# JWT配置
jwt.secret=propertyManagementSystemSecretKeyForJwtTokenGeneration
jwt.expiration=86400000

# 端口配置
server.port=8080
```

- [ ] **Step 9: 添加pom.xml依赖**

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.5.3.1</version>
    </dependency>
    
    <!-- 参数校验 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

- [ ] **Step 10: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/common/ src/main/resources/application.properties pom.xml
git commit -m "feat: 搭建后端基础架构（统一返回、异常处理、JWT、密码加密）"
```

---

## Task 3: 创建后端实体类和Mapper

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/SysUser.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/SysRole.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/SysMenu.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/SysUserRole.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/SysRoleMenu.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/SysOperLog.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/SysUserMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/SysRoleMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/SysMenuMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/SysUserRoleMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/SysRoleMenuMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/SysOperLogMapper.java`

- [ ] **Step 1: 创建用户实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String phone;
    
    private String avatar;
    
    private Integer userType;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 2: 创建角色实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String roleName;
    
    private String roleKey;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 3: 创建菜单实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long parentId;
    
    private String menuName;
    
    private String path;
    
    private String component;
    
    private String perms;
    
    private Integer menuType;
    
    private Integer sort;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 4: 创建用户角色关联实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class SysUserRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long roleId;
}
```

- [ ] **Step 5: 创建角色菜单关联实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_role_menu")
public class SysRoleMenu {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long roleId;
    
    private Long menuId;
}
```

- [ ] **Step 6: 创建操作日志实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_oper_log")
public class SysOperLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String userName;
    
    private String operModule;
    
    private String operType;
    
    private String operIp;
    
    private String operDesc;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

- [ ] **Step 7: 创建用户Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
```

- [ ] **Step 8: 创建角色Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
```

- [ ] **Step 9: 创建菜单Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
}
```

- [ ] **Step 10: 创建用户角色Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
```

- [ ] **Step 11: 创建角色菜单Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
}
```

- [ ] **Step 12: 创建操作日志Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
```

- [ ] **Step 13: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/ src/main/java/com/lsy/propertymanagementsystem/mapper/
git commit -m "feat: 创建后端实体类和Mapper"
```

---

## Task 4: 创建后端DTO和Service

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/LoginRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/UserRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/RoleRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/MenuRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/response/LoginResponse.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/response/UserResponse.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/response/MenuResponse.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/SysUserService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/SysUserServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/SysRoleService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/SysRoleServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/SysMenuService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/SysMenuServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/SysOperLogService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/SysOperLogServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/AuthService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/AuthServiceImpl.java`

- [ ] **Step 1: 创建登录请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

- [ ] **Step 2: 创建用户请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRequest {
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    private String phone;
    
    private Integer userType;
    
    private Integer status;
}
```

- [ ] **Step 3: 创建角色请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RoleRequest {
    private Long id;
    
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    
    @NotBlank(message = "权限标识不能为空")
    private String roleKey;
    
    private String remark;
    
    private List<Long> menuIds;
}
```

- [ ] **Step 4: 创建菜单请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MenuRequest {
    private Long id;
    
    private Long parentId;
    
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;
    
    private String path;
    
    private String component;
    
    private String perms;
    
    private Integer menuType;
    
    private Integer sort;
    
    private Integer status;
}
```

- [ ] **Step 5: 创建登录响应DTO**

```java
package com.lsy.propertymanagementsystem.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private Integer userType;
    private String avatar;
}
```

- [ ] **Step 6: 创建用户响应DTO**

```java
package com.lsy.propertymanagementsystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String avatar;
    private Integer userType;
    private Integer status;
    private LocalDateTime createTime;
    private List<Long> roleIds;
}
```

- [ ] **Step 7: 创建菜单响应DTO**

```java
package com.lsy.propertymanagementsystem.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MenuResponse {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String perms;
    private Integer menuType;
    private Integer sort;
    private Integer status;
    private List<MenuResponse> children;
}
```

- [ ] **Step 8: 创建用户Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.UserRequest;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;
import com.lsy.propertymanagementsystem.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    IPage<UserResponse> getUserPage(Integer pageNum, Integer pageSize, String username, Integer status);
    
    void addUser(UserRequest request);
    
    void updateUser(UserRequest request);
    
    void deleteUser(Long id);
    
    void updateUserStatus(Long id, Integer status);
    
    void resetPassword(Long id, String newPassword);
    
    UserResponse getUserById(Long id);
}
```

- [ ] **Step 9: 创建用户Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.PasswordUtils;
import com.lsy.propertymanagementsystem.dto.request.UserRequest;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;
import com.lsy.propertymanagementsystem.entity.SysUser;
import com.lsy.propertymanagementsystem.entity.SysUserRole;
import com.lsy.propertymanagementsystem.mapper.SysUserMapper;
import com.lsy.propertymanagementsystem.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    
    @Override
    public IPage<UserResponse> getUserPage(Integer pageNum, Integer pageSize, String username, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        IPage<SysUser> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        
        return page.convert(user -> {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setRealName(user.getRealName());
            response.setPhone(user.getPhone());
            response.setAvatar(user.getAvatar());
            response.setUserType(user.getUserType());
            response.setStatus(user.getStatus());
            response.setCreateTime(user.getCreateTime());
            
            // 查询用户角色
            LambdaQueryWrapper<SysUserRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.eq(SysUserRole::getUserId, user.getId());
            List<SysUserRole> userRoles = userRoleMapper.selectList(roleWrapper);
            response.setRoleIds(userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
            
            return response;
        });
    }
    
    @Override
    @Transactional
    public void addUser(UserRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(request.getStatus());
        this.save(user);
    }
    
    @Override
    @Transactional
    public void updateUser(UserRequest request) {
        SysUser user = this.getById(request.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(request.getStatus());
        this.updateById(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        this.removeById(id);
    }
    
    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        this.updateById(user);
    }
    
    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setPassword(PasswordUtils.encode(newPassword));
        this.updateById(user);
    }
    
    @Override
    public UserResponse getUserById(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setUserType(user.getUserType());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());
        
        // 查询用户角色
        LambdaQueryWrapper<SysUserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRole::getUserId, user.getId());
        List<SysUserRole> userRoles = userRoleMapper.selectList(roleWrapper);
        response.setRoleIds(userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
        
        return response;
    }
}
```

- [ ] **Step 10: 创建角色Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.RoleRequest;
import com.lsy.propertymanagementsystem.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    List<SysRole> getRoleList();
    
    void addRole(RoleRequest request);
    
    void updateRole(RoleRequest request);
    
    void deleteRole(Long id);
    
    SysRole getRoleById(Long id);
    
    void assignMenus(Long roleId, List<Long> menuIds);
    
    List<Long> getRoleMenuIds(Long roleId);
}
```

- [ ] **Step 11: 创建角色Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.RoleRequest;
import com.lsy.propertymanagementsystem.entity.SysRole;
import com.lsy.propertymanagementsystem.entity.SysRoleMenu;
import com.lsy.propertymanagementsystem.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.mapper.SysRoleMenuMapper;
import com.lsy.propertymanagementsystem.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    
    @Autowired
    private SysRoleMenuMapper roleMenuMapper;
    
    @Override
    public List<SysRole> getRoleList() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRole::getId);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addRole(RoleRequest request) {
        // 检查角色名称是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, request.getRoleName());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        
        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRemark(request.getRemark());
        this.save(role);
    }
    
    @Override
    @Transactional
    public void updateRole(RoleRequest request) {
        SysRole role = this.getById(request.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRemark(request.getRemark());
        this.updateById(role);
    }
    
    @Override
    @Transactional
    public void deleteRole(Long id) {
        this.removeById(id);
        // 删除角色菜单关联
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, id);
        roleMenuMapper.delete(wrapper);
    }
    
    @Override
    public SysRole getRoleById(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有关联
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);
        
        // 插入新关联
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }
    
    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }
}
```

- [ ] **Step 12: 创建菜单Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.MenuRequest;
import com.lsy.propertymanagementsystem.dto.response.MenuResponse;
import com.lsy.propertymanagementsystem.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<MenuResponse> getMenuTree();
    
    List<MenuResponse> getMenuList();
    
    void addMenu(MenuRequest request);
    
    void updateMenu(MenuRequest request);
    
    void deleteMenu(Long id);
    
    SysMenu getMenuById(Long id);
}
```

- [ ] **Step 13: 创建菜单Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.MenuRequest;
import com.lsy.propertymanagementsystem.dto.response.MenuResponse;
import com.lsy.propertymanagementsystem.entity.SysMenu;
import com.lsy.propertymanagementsystem.mapper.SysMenuMapper;
import com.lsy.propertymanagementsystem.service.SysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    
    @Override
    public List<MenuResponse> getMenuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = this.list(wrapper);
        return buildMenuTree(menus, 0L);
    }
    
    @Override
    public List<MenuResponse> getMenuList() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = this.list(wrapper);
        return buildMenuTree(menus, 0L);
    }
    
    @Override
    @Transactional
    public void addMenu(MenuRequest request) {
        SysMenu menu = new SysMenu();
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPerms(request.getPerms());
        menu.setMenuType(request.getMenuType());
        menu.setSort(request.getSort());
        menu.setStatus(request.getStatus());
        this.save(menu);
    }
    
    @Override
    @Transactional
    public void updateMenu(MenuRequest request) {
        SysMenu menu = this.getById(request.getId());
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPerms(request.getPerms());
        menu.setMenuType(request.getMenuType());
        menu.setSort(request.getSort());
        menu.setStatus(request.getStatus());
        this.updateById(menu);
    }
    
    @Override
    @Transactional
    public void deleteMenu(Long id) {
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
        this.removeById(id);
    }
    
    @Override
    public SysMenu getMenuById(Long id) {
        return this.getById(id);
    }
    
    private List<MenuResponse> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<MenuResponse> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                MenuResponse response = new MenuResponse();
                response.setId(menu.getId());
                response.setParentId(menu.getParentId());
                response.setMenuName(menu.getMenuName());
                response.setPath(menu.getPath());
                response.setComponent(menu.getComponent());
                response.setPerms(menu.getPerms());
                response.setMenuType(menu.getMenuType());
                response.setSort(menu.getSort());
                response.setStatus(menu.getStatus());
                response.setChildren(buildMenuTree(menus, menu.getId()));
                tree.add(response);
            }
        }
        return tree;
    }
}
```

- [ ] **Step 14: 创建操作日志Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.entity.SysOperLog;

public interface SysOperLogService extends IService<SysOperLog> {
    IPage<SysOperLog> getOperLogPage(Integer pageNum, Integer pageSize);
    
    void addOperLog(String userName, String operModule, String operType, String operIp, String operDesc);
    
    void cleanOperLog();
}
```

- [ ] **Step 15: 创建操作日志Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.entity.SysOperLog;
import com.lsy.propertymanagementsystem.mapper.SysOperLogMapper;
import com.lsy.propertymanagementsystem.service.SysOperLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {
    
    @Override
    public IPage<SysOperLog> getOperLogPage(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysOperLog::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    @Transactional
    public void addOperLog(String userName, String operModule, String operType, String operIp, String operDesc) {
        SysOperLog log = new SysOperLog();
        log.setUserName(userName);
        log.setOperModule(operModule);
        log.setOperType(operType);
        log.setOperIp(operIp);
        log.setOperDesc(operDesc);
        this.save(log);
    }
    
    @Override
    @Transactional
    public void cleanOperLog() {
        this.remove(null);
    }
}
```

- [ ] **Step 16: 创建认证Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.lsy.propertymanagementsystem.dto.request.LoginRequest;
import com.lsy.propertymanagementsystem.dto.response.LoginResponse;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    
    void logout(String token);
    
    UserResponse getCurrentUser(String token);
}
```

- [ ] **Step 17: 创建认证Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import com.lsy.propertymanagementsystem.common.utils.PasswordUtils;
import com.lsy.propertymanagementsystem.dto.request.LoginRequest;
import com.lsy.propertymanagementsystem.dto.response.LoginResponse;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;
import com.lsy.propertymanagementsystem.entity.SysUser;
import com.lsy.propertymanagementsystem.entity.SysUserRole;
import com.lsy.propertymanagementsystem.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.service.AuthService;
import com.lsy.propertymanagementsystem.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = userService.getOne(wrapper);
        
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 验证密码
        if (!PasswordUtils.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 生成token
        String token = JwtUtils.generateToken(user.getId(), user.getUsername());
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setUserType(user.getUserType());
        response.setAvatar(user.getAvatar());
        
        return response;
    }
    
    @Override
    public void logout(String token) {
        // JWT是无状态的，退出登录只需要前端删除token即可
        // 这里可以扩展为将token加入黑名单
    }
    
    @Override
    public UserResponse getCurrentUser(String token) {
        Long userId = JwtUtils.getUserId(token);
        SysUser user = userService.getById(userId);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setUserType(user.getUserType());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());
        
        // 查询用户角色
        LambdaQueryWrapper<SysUserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRole::getUserId, user.getId());
        List<SysUserRole> userRoles = userRoleMapper.selectList(roleWrapper);
        response.setRoleIds(userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
        
        return response;
    }
}
```

- [ ] **Step 18: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/dto/ src/main/java/com/lsy/propertymanagementsystem/service/
git commit -m "feat: 创建后端DTO和Service层"
```

---

## Task 5: 创建后端Controller和拦截器

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/interceptor/JwtInterceptor.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/config/WebMvcConfig.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/SysUserController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/SysRoleController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/SysMenuController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/SysOperLogController.java`

- [ ] **Step 1: 创建JWT拦截器**

```java
package com.lsy.propertymanagementsystem.interceptor;

import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证token
        if (token != null && JwtUtils.isTokenValid(token)) {
            // 将用户信息存入request
            request.setAttribute("userId", JwtUtils.getUserId(token));
            request.setAttribute("username", JwtUtils.getUsername(token));
            return true;
        }
        
        // token无效，返回401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"未登录或token已过期\"}");
        return false;
    }
}
```

- [ ] **Step 2: 创建WebMvc配置**

```java
package com.lsy.propertymanagementsystem.config;

import com.lsy.propertymanagementsystem.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/login");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

- [ ] **Step 3: 创建用户Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.LoginRequest;
import com.lsy.propertymanagementsystem.dto.request.UserRequest;
import com.lsy.propertymanagementsystem.dto.response.LoginResponse;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;
import com.lsy.propertymanagementsystem.service.AuthService;
import com.lsy.propertymanagementsystem.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class SysUserController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private SysUserService userService;
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
    
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return Result.success();
    }
    
    @GetMapping("/info")
    public Result<UserResponse> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        UserResponse response = authService.getCurrentUser(token);
        return Result.success(response);
    }
    
    @PostMapping
    public Result<Void> addUser(@RequestBody @Valid UserRequest request) {
        userService.addUser(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateUser(@RequestBody @Valid UserRequest request) {
        userService.updateUser(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
    
    @PutMapping("/status")
    public Result<Void> updateUserStatus(@RequestParam Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }
    
    @PutMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestParam Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
    
    @GetMapping("/page")
    public Result<IPage<UserResponse>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        IPage<UserResponse> page = userService.getUserPage(pageNum, pageSize, username, status);
        return Result.success(page);
    }
}
```

- [ ] **Step 4: 创建角色Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.RoleRequest;
import com.lsy.propertymanagementsystem.entity.SysRole;
import com.lsy.propertymanagementsystem.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class SysRoleController {
    
    @Autowired
    private SysRoleService roleService;
    
    @PostMapping
    public Result<Void> addRole(@RequestBody @Valid RoleRequest request) {
        roleService.addRole(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateRole(@RequestBody @Valid RoleRequest request) {
        roleService.updateRole(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<SysRole>> getRoleList() {
        List<SysRole> roles = roleService.getRoleList();
        return Result.success(roles);
    }
    
    @GetMapping("/{id}")
    public Result<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        return Result.success(role);
    }
    
    @PutMapping("/menu")
    public Result<Void> assignMenus(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.success();
    }
    
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        List<Long> menuIds = roleService.getRoleMenuIds(id);
        return Result.success(menuIds);
    }
}
```

- [ ] **Step 5: 创建菜单Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.MenuRequest;
import com.lsy.propertymanagementsystem.dto.response.MenuResponse;
import com.lsy.propertymanagementsystem.entity.SysMenu;
import com.lsy.propertymanagementsystem.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class SysMenuController {
    
    @Autowired
    private SysMenuService menuService;
    
    @PostMapping
    public Result<Void> addMenu(@RequestBody @Valid MenuRequest request) {
        menuService.addMenu(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateMenu(@RequestBody @Valid MenuRequest request) {
        menuService.updateMenu(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<MenuResponse>> getMenuList() {
        List<MenuResponse> menus = menuService.getMenuList();
        return Result.success(menus);
    }
    
    @GetMapping("/{id}")
    public Result<SysMenu> getMenuById(@PathVariable Long id) {
        SysMenu menu = menuService.getMenuById(id);
        return Result.success(menu);
    }
    
    @GetMapping("/tree")
    public Result<List<MenuResponse>> getMenuTree() {
        List<MenuResponse> menus = menuService.getMenuTree();
        return Result.success(menus);
    }
}
```

- [ ] **Step 6: 创建操作日志Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.entity.SysOperLog;
import com.lsy.propertymanagementsystem.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operlog")
public class SysOperLogController {
    
    @Autowired
    private SysOperLogService operLogService;
    
    @GetMapping("/page")
    public Result<IPage<SysOperLog>> getOperLogPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<SysOperLog> page = operLogService.getOperLogPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @DeleteMapping("/clean")
    public Result<Void> cleanOperLog() {
        operLogService.cleanOperLog();
        return Result.success();
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/interceptor/ src/main/java/com/lsy/propertymanagementsystem/config/ src/main/java/com/lsy/propertymanagementsystem/controller/
git commit -m "feat: 创建后端Controller和拦截器"
```

---

## Task 6: 搭建前端框架

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.js`
- Create: `frontend/index.html`
- Create: `frontend/src/main.js`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/router/index.js`
- Create: `frontend/src/store/user.js`
- Create: `frontend/src/utils/request.js`
- Create: `frontend/src/utils/auth.js`
- Create: `frontend/src/api/user.js`
- Create: `frontend/src/api/role.js`
- Create: `frontend/src/api/menu.js`
- Create: `frontend/src/api/operlog.js`
- Create: `frontend/src/views/login/index.vue`
- Create: `frontend/src/views/layout/index.vue`
- Create: `frontend/src/views/dashboard/index.vue`
- Create: `frontend/src/components/Pagination.vue`

- [ ] **Step 1: 创建package.json**

```json
{
  "name": "property-management-system-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.21",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.7",
    "element-plus": "^2.6.1",
    "axios": "^1.6.7",
    "echarts": "^5.5.0",
    "nprogress": "^0.2.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.4",
    "vite": "^5.1.6"
  }
}
```

- [ ] **Step 2: 创建vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <link rel="icon" type="image/svg+xml" href="/vite.svg">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>物业管理系统</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: 创建main.js**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
```

- [ ] **Step 5: 创建App.vue**

```vue
<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
}
</style>
```

- [ ] **Step 6: 创建路由配置**

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '../utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('../views/system/user/index.vue'),
        meta: { title: '用户管理', parent: '系统管理' }
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('../views/system/role/index.vue'),
        meta: { title: '角色管理', parent: '系统管理' }
      },
      {
        path: 'system/menu',
        name: 'SystemMenu',
        component: () => import('../views/system/menu/index.vue'),
        meta: { title: '菜单管理', parent: '系统管理' }
      },
      {
        path: 'system/operlog',
        name: 'SystemOperLog',
        component: () => import('../views/system/operlog/index.vue'),
        meta: { title: '操作日志', parent: '系统管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  const token = getToken()
  
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
```

- [ ] **Step 7: 创建用户状态管理**

```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo, login as loginApi, logout as logoutApi } from '../api/user'
import { getToken, setToken, removeToken } from '../utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref({})
  const menuList = ref([])
  
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    token.value = res.data.token
    setToken(res.data.token)
    return res.data
  }
  
  async function getInfo() {
    const res = await getUserInfo()
    userInfo.value = res.data
    return res.data
  }
  
  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      userInfo.value = {}
      menuList.value = []
      removeToken()
    }
  }
  
  return {
    token,
    userInfo,
    menuList,
    login,
    getInfo,
    logout
  }
})
```

- [ ] **Step 8: 创建请求封装**

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import router from '../router'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '操作失败')
      if (res.code === 401) {
        removeToken()
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
```

- [ ] **Step 9: 创建认证工具**

```javascript
const TokenKey = 'Admin-Token'

export function getToken() {
  return localStorage.getItem(TokenKey)
}

export function setToken(token) {
  return localStorage.setItem(TokenKey, token)
}

export function removeToken() {
  return localStorage.removeItem(TokenKey)
}
```

- [ ] **Step 10: 创建用户API**

```javascript
import request from '../utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

export function addUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: '/user/status',
    method: 'put',
    params: { id, status }
  })
}

export function resetPassword(id, newPassword) {
  return request({
    url: '/user/resetPassword',
    method: 'put',
    params: { id, newPassword }
  })
}
```

- [ ] **Step 11: 创建角色API**

```javascript
import request from '../utils/request'

export function getRoleList() {
  return request({
    url: '/role/list',
    method: 'get'
  })
}

export function getRoleById(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

export function assignMenus(roleId, menuIds) {
  return request({
    url: '/role/menu',
    method: 'put',
    params: { roleId },
    data: menuIds
  })
}

export function getRoleMenuIds(roleId) {
  return request({
    url: `/role/${roleId}/menus`,
    method: 'get'
  })
}
```

- [ ] **Step 12: 创建菜单API**

```javascript
import request from '../utils/request'

export function getMenuTree() {
  return request({
    url: '/menu/tree',
    method: 'get'
  })
}

export function getMenuList() {
  return request({
    url: '/menu/list',
    method: 'get'
  })
}

export function getMenuById(id) {
  return request({
    url: `/menu/${id}`,
    method: 'get'
  })
}

export function addMenu(data) {
  return request({
    url: '/menu',
    method: 'post',
    data
  })
}

export function updateMenu(data) {
  return request({
    url: '/menu',
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  })
}
```

- [ ] **Step 13: 创建操作日志API**

```javascript
import request from '../utils/request'

export function getOperLogPage(params) {
  return request({
    url: '/operlog/page',
    method: 'get',
    params
  })
}

export function cleanOperLog() {
  return request({
    url: '/operlog/clean',
    method: 'delete'
  })
}
```

- [ ] **Step 14: 创建登录页面**

```vue
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h2>物业管理系统</h2>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: 'root',
  password: '123456'
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 5px 30px rgba(0, 0, 0, 0.2);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
}

.login-title h2 {
  color: #333;
  font-size: 24px;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
}
</style>
```

- [ ] **Step 15: 创建后台布局**

```vue
<template>
  <div class="layout-container">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '210px'" class="aside">
        <div class="logo">
          <img src="" alt="" />
          <span v-show="!isCollapse">物业管理系统</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :router="true"
          class="aside-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user">
              <el-icon><User /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/system/role">
              <el-icon><UserFilled /></el-icon>
              <template #title>角色管理</template>
            </el-menu-item>
            <el-menu-item index="/system/menu">
              <el-icon><Menu /></el-icon>
              <template #title>菜单管理</template>
            </el-menu-item>
            <el-menu-item index="/system/operlog">
              <el-icon><Document /></el-icon>
              <template #title>操作日志</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-icon class="collapse-btn" @click="toggleCollapse">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item v-if="route.meta.parent">{{ route.meta.parent }}</el-breadcrumb-item>
              <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                {{ userStore.userInfo.realName || '管理员' }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await userStore.logout()
      router.push('/login')
    })
  }
}

onMounted(async () => {
  try {
    await userStore.getInfo()
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
})
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.aside-menu {
  border-right: none;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
```

- [ ] **Step 16: 创建首页**

```vue
<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">用户总数</div>
              <div class="card-value">{{ stats.userCount }}</div>
            </div>
            <el-icon class="card-icon" style="color: #409eff"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">角色总数</div>
              <div class="card-value">{{ stats.roleCount }}</div>
            </div>
            <el-icon class="card-icon" style="color: #67c23a"><UserFilled /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">菜单总数</div>
              <div class="card-value">{{ stats.menuCount }}</div>
            </div>
            <el-icon class="card-icon" style="color: #e6a23c"><Menu /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">今日操作</div>
              <div class="card-value">{{ stats.todayOperCount }}</div>
            </div>
            <el-icon class="card-icon" style="color: #f56c6c"><Document /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎使用物业管理系统</h2>
        <p>这是一个基于SpringBoot+Vue3+MyBatisPlus+MySQL的物业管理系统</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({
  userCount: 0,
  roleCount: 0,
  menuCount: 0,
  todayOperCount: 0
})

onMounted(() => {
  // 这里可以调用接口获取统计数据
  stats.value = {
    userCount: 1,
    roleCount: 3,
    menuCount: 5,
    todayOperCount: 0
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.card-icon {
  font-size: 48px;
}

.welcome-card {
  margin-top: 20px;
}

.welcome-content {
  text-align: center;
  padding: 40px 0;
}

.welcome-content h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.welcome-content p {
  font-size: 14px;
  color: #909399;
}
</style>
```

- [ ] **Step 17: 创建分页组件**

```vue
<template>
  <div class="pagination-container">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  total: {
    type: Number,
    default: 0
  },
  page: {
    type: Number,
    default: 1
  },
  limit: {
    type: Number,
    default: 10
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  }
})

const emit = defineEmits(['update:page', 'update:limit', 'pagination'])

const currentPage = ref(props.page)
const pageSize = ref(props.limit)

watch(() => props.page, (val) => {
  currentPage.value = val
})

watch(() => props.limit, (val) => {
  pageSize.value = val
})

const handleSizeChange = (val) => {
  emit('update:limit', val)
  emit('pagination', { page: currentPage.value, limit: val })
}

const handleCurrentChange = (val) => {
  emit('update:page', val)
  emit('pagination', { page: val, limit: pageSize.value })
}
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
}
</style>
```

- [ ] **Step 18: 安装依赖并启动前端**

Run: `cd frontend && npm install && npm run dev`
Expected: 前端项目启动成功，访问 http://localhost:3000

- [ ] **Step 19: 提交**

```bash
git add frontend/
git commit -m "feat: 搭建前端框架和基础页面"
```

---

## Task 7: 实现前端业务页面

**Files:**
- Create: `frontend/src/views/system/user/index.vue`
- Create: `frontend/src/views/system/role/index.vue`
- Create: `frontend/src/views/system/menu/index.vue`
- Create: `frontend/src/views/system/operlog/index.vue`

- [ ] **Step 1: 创建用户管理页面**

```vue
<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="userType" label="用户类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.userType === 1" type="danger">超级管理员</el-tag>
            <el-tag v-else-if="row.userType === 2" type="warning">物业管理员</el-tag>
            <el-tag v-else type="info">业主</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <Pagination :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="fetchData" />
    </el-card>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" placeholder="请选择用户类型">
            <el-option label="超级管理员" :value="1" />
            <el-option label="物业管理员" :value="2" />
            <el-option label="业主" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, addUser, updateUser, deleteUser, updateUserStatus, resetPassword } from '../../../api/user'
import Pagination from '../../../components/Pagination.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  status: undefined
})

const form = reactive({
  id: undefined,
  username: '',
  password: '',
  realName: '',
  phone: '',
  userType: 3,
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryParams.username = ''
  queryParams.status = undefined
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  Object.assign(form, {
    id: undefined,
    username: '',
    password: '',
    realName: '',
    phone: '',
    userType: 3,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateUser(form)
          ElMessage.success('编辑成功')
        } else {
          await addUser(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error(error.message || '状态修改失败')
  }
}

const handleResetPassword = (row) => {
  ElMessageBox.confirm(`确定要重置用户"${row.username}"的密码吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await resetPassword(row.id, '123456')
      ElMessage.success('密码已重置为123456')
    } catch (error) {
      ElMessage.error(error.message || '重置失败')
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 15px;
}
</style>
```

- [ ] **Step 2: 创建角色管理页面**

```vue
<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleKey" label="权限标识" width="150" />
        <el-table-column prop="remark" label="描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAssignMenu(row)">分配菜单</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="描述" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 分配菜单弹窗 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单" width="500px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole, assignMenus, getRoleMenuIds } from '../../../api/role'
import { getMenuTree } from '../../../api/menu'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const menuDialogVisible = ref(false)
const menuTreeRef = ref(null)
const menuTree = ref([])
const checkedMenuIds = ref([])
const currentRoleId = ref(null)

const form = reactive({
  id: undefined,
  roleName: '',
  roleKey: '',
  remark: ''
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入权限标识', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoleList()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  Object.assign(form, {
    id: undefined,
    roleName: '',
    roleKey: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateRole(form)
          ElMessage.success('编辑成功')
        } else {
          await addRole(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除角色"${row.roleName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRole(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

const handleAssignMenu = async (row) => {
  currentRoleId.value = row.id
  try {
    const [menuRes, roleMenuRes] = await Promise.all([
      getMenuTree(),
      getRoleMenuIds(row.id)
    ])
    menuTree.value = menuRes.data
    checkedMenuIds.value = roleMenuRes.data
    menuDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取菜单数据失败')
  }
}

const handleMenuSubmit = async () => {
  try {
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    const menuIds = [...checkedKeys, ...halfCheckedKeys]
    await assignMenus(currentRoleId.value, menuIds)
    ElMessage.success('分配菜单成功')
    menuDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '分配菜单失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.role-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

- [ ] **Step 3: 创建菜单管理页面**

```vue
<template>
  <div class="menu-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" @click="handleAdd">新增菜单</el-button>
        </div>
      </template>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="path" label="路由地址" width="150" />
        <el-table-column prop="component" label="组件路径" width="150" />
        <el-table-column prop="perms" label="权限标识" width="150" />
        <el-table-column prop="menuType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 0" type="primary">目录</el-tag>
            <el-tag v-else-if="row.menuType === 1" type="success">菜单</el-tag>
            <el-tag v-else type="warning">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAdd(row)">新增</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select v-model="form.parentId" :data="menuTree" :props="{ label: 'menuName', value: 'id' }" placeholder="请选择上级菜单" clearable />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 2" label="路由地址" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 1" label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 0" label="权限标识" prop="perms">
          <el-input v-model="form.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuList, addMenu, updateMenu, deleteMenu } from '../../../api/menu'

const loading = ref(false)
const tableData = ref([])
const menuTree = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = reactive({
  id: undefined,
  parentId: 0,
  menuName: '',
  path: '',
  component: '',
  perms: '',
  menuType: 0,
  sort: 0,
  status: 1
})

const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMenuList()
    tableData.value = res.data
    menuTree.value = [{ id: 0, menuName: '主类目', children: res.data }]
  } finally {
    loading.value = false
  }
}

const handleAdd = (row) => {
  dialogTitle.value = '新增菜单'
  Object.assign(form, {
    id: undefined,
    parentId: row ? row.id : 0,
    menuName: '',
    path: '',
    component: '',
    perms: '',
    menuType: row ? (row.menuType === 0 ? 1 : 2) : 0,
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateMenu(form)
          ElMessage.success('编辑成功')
        } else {
          await addMenu(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除菜单"${row.menuName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMenu(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.menu-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

- [ ] **Step 4: 创建操作日志页面**

```vue
<template>
  <div class="operlog-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <el-button type="danger" @click="handleClean">清空日志</el-button>
        </div>
      </template>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="操作人" width="120" />
        <el-table-column prop="operModule" label="操作模块" width="120" />
        <el-table-column prop="operType" label="操作类型" width="100" />
        <el-table-column prop="operIp" label="操作IP" width="120" />
        <el-table-column prop="operDesc" label="操作描述" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>
      
      <!-- 分页 -->
      <Pagination :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOperLogPage, cleanOperLog } from '../../../api/operlog'
import Pagination from '../../../components/Pagination.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getOperLogPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleClean = () => {
  ElMessageBox.confirm('确定要清空所有操作日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cleanOperLog()
      ElMessage.success('清空成功')
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '清空失败')
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.operlog-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

- [ ] **Step 5: 提交**

```bash
git add frontend/src/views/system/
git commit -m "feat: 实现系统管理前端页面"
```

---

## Task 8: 测试和优化

**Files:**
- Modify: 各个文件根据测试结果进行调整

- [ ] **Step 1: 启动后端服务**

Run: `mvn spring-boot:run`
Expected: 后端服务启动成功，访问 http://localhost:8080

- [ ] **Step 2: 测试登录接口**

Run: `curl -X POST http://localhost:8080/api/user/login -H "Content-Type: application/json" -d '{"username":"root","password":"123456"}'`
Expected: 返回token和用户信息

- [ ] **Step 3: 启动前端服务**

Run: `cd frontend && npm run dev`
Expected: 前端服务启动成功，访问 http://localhost:3000

- [ ] **Step 4: 测试登录功能**

访问 http://localhost:3000/login，输入用户名root，密码123456，点击登录
Expected: 登录成功，跳转到首页

- [ ] **Step 5: 测试用户管理功能**

访问 http://localhost:3000/system/user，测试用户列表、新增、编辑、删除功能
Expected: 功能正常

- [ ] **Step 6: 测试角色管理功能**

访问 http://localhost:3000/system/role，测试角色列表、新增、编辑、删除、分配菜单功能
Expected: 功能正常

- [ ] **Step 7: 测试菜单管理功能**

访问 http://localhost:3000/system/menu，测试菜单列表、新增、编辑、删除功能
Expected: 功能正常

- [ ] **Step 8: 测试操作日志功能**

访问 http://localhost:3000/system/operlog，测试日志列表、清空功能
Expected: 功能正常

- [ ] **Step 9: 优化和修复问题**

根据测试结果，修复发现的问题

- [ ] **Step 10: 最终提交**

```bash
git add -A
git commit -m "feat: 完成系统管理模块实现和测试"
```

---

## 完成

系统管理模块已实现完成，包括：
1. 数据库表结构创建
2. 后端基础架构搭建
3. 后端API实现
4. 前端框架搭建
5. 前端页面实现
6. 功能测试

下一步可以继续实现其他业务模块。
