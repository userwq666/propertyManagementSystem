# 物业管理系统 - 后端文档

## 1. 项目概述

**物业管理系统** 是一个基于 Spring Boot 3.2+ 开发的现代化物业管理后端系统，提供小区基础管理、收费管理、设备维护、报修投诉、公告通知、统计分析等核心功能。

### 核心特性
- **RBAC 权限控制**：基于角色的访问控制，支持菜单/按钮级权限
- **JWT 无状态认证**：Token 过期自动续期，支持单点登录
- **MyBatis-Plus ORM**：代码生成、自动填充、乐观锁、逻辑删除
- **多模块架构**：系统管理、小区基础、收费管理、设备管理、报修投诉、公告通知、统计分析
- **定时任务调度**：费用自动生成、逾期提醒、设备巡检计划
- **统一异常处理**：全局异常拦截、统一响应格式、参数校验

---

## 2. 技术栈

| 类别 | 技术/框架 | 版本 |
|------|-----------|------|
| **核心框架** | Spring Boot | 4.0.7 |
| **Java 版本** | JDK | 17 |
| **ORM 框架** | MyBatis-Plus | 3.5.15 |
| **安全框架** | Spring Security + JWT | 6.x / 0.11.5 |
| **数据库** | MySQL | 8.0+ |
| **连接池** | HikariCP | 内置 |
| **构建工具** | Maven | 3.8+ |
| **验证框架** | Hibernate Validator | 内置 |
| **日志框架** | SLF4J + Logback | 内置 |
| **定时任务** | Spring Task + @EnableScheduling | 内置 |

### 核心依赖 (pom.xml)
```xml
<!-- Web MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<!-- Security + JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
    <version>3.5.15</version>
</dependency>

<!-- MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 3. 项目结构

```
src/main/java/com/lsy/propertymanagementsystem/
├── PropertyManagementSystemApplication.java    # 启动类
├── config/                                     # 配置类
│   ├── SecurityConfig.java                    # Spring Security 配置
│   ├── JwtConfig.java                         # JWT 配置
│   ├── WebMvcConfig.java                      # Web MVC 配置
│   ├── MyMetaObjectHandler.java               # MyBatis-Plus 自动填充
│   └── DatabaseInitializer.java               # 数据库初始化
├── common/                                     # 公共模块
│   ├── result/
│   │   ├── Result.java                        # 统一响应封装
│   │   └── ResultCode.java                    # 响应码枚举
│   ├── exception/
│   │   ├── BusinessException.java             # 业务异常
│   │   └── GlobalExceptionHandler.java        # 全局异常处理
│   └── utils/
│       ├── JwtUtils.java                      # JWT 工具类
│       └── PasswordUtils.java                 # 密码加密工具
├── interceptor/
│   └── JwtInterceptor.java                    # JWT 拦截器
├── task/
│   └── ScheduledTasks.java                    # 定时任务
└── module/                                    # 业务模块
    ├── system/                                # 系统管理
    │   ├── controller/                        # Auth, User, Role, Menu
    │   ├── service/                           # 业务逻辑
    │   ├── mapper/                            # MyBatis Mapper
    │   ├── domain/                            # 实体类
    │   ├── dto/                               # DTO 类
    │   └── enums/                             # 枚举类
    ├── community/                             # 小区基础
    │   ├── controller/                        # Building, House, Owner, Parking
    │   ├── service/
    │   ├── mapper/
    │   ├── domain/
    │   ├── dto/
    │   └── enums/
    ├── fee/                                   # 收费管理
    │   ├── controller/                        # FeeItem, FeeNotice, FeeRecord
    │   ├── service/
    │   ├── mapper/
    │   ├── domain/
    │   ├── dto/
    │   └── enums/
    ├── equipment/                             # 设备管理
    │   ├── controller/                        # Equipment, Category, Maintenance
    │   ├── service/
    │   ├── mapper/
    │   ├── domain/
    │   ├── dto/
    │   └── enums/
    ├── repair/                                # 报修管理
    ├── complaint/                             # 投诉建议
    ├── announcement/                          # 公告通知
    └── statistics/                            # 统计分析
```

---

## 4. 数据库设计

### 4.1 数据库配置
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/property_management_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis-Plus
mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml
mybatis-plus.configuration.map-underscore-to-camel-case=true
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

### 4.2 核心表结构

#### 系统基础表
| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `sys_user` | 系统用户 | id, username, password, real_name, phone, user_type, status |
| `sys_role` | 角色表 | id, role_name, role_key, remark |
| `sys_menu` | 菜单权限 | id, parent_id, menu_name, path, component, perms, menu_type |
| `sys_user_role` | 用户角色关联 | user_id, role_id |
| `sys_role_menu` | 角色菜单关联 | role_id, menu_id |
| `sys_oper_log` | 操作日志 | user_id, oper_module, oper_type, oper_desc, create_time |

#### 小区基础表
| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `community_building` | 楼栋表 | id, building_no, floor_count, total_house |
| `community_house` | 房屋表 | id, building_id, room_no, area, house_type, house_status, owner_id |
| `community_owner` | 业主信息 | id, user_id, name, phone, id_card, owner_type, status |
| `community_parking` | 车位表 | id, parking_no, parking_type, status, owner_id, rent_price, sell_price |

#### 收费管理表
| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `fee_item` | 收费项目 | id, item_name, item_type, fee_cycle, unit_price, unit |
| `fee_notice` | 收费通知 | id, notice_no, owner_id, house_id, fee_item_id, total_amount, status, due_date |
| `fee_record` | 缴费记录 | id, record_no, notice_id, pay_amount, pay_type, pay_time, status |

#### 设备管理表
| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `equipment_category` | 设备分类 | id, category_name, parent_id, sort |
| `equipment` | 设备台账 | id, equipment_no, name, category_id, building_id, location, status |
| `equipment_maintenance` | 维修记录 | id, equipment_id, maintenance_type, status, cost, maintenance_time |

#### 其他业务表
| 表名 | 说明 |
|------|------|
| `repair_record` | 报修记录 |
| `complaint_suggest` | 投诉建议 |
| `announcement` | 公告通知 |
| `announcement_read` | 公告阅读记录 |
| `inspection_plan` | 巡检计划 |
| `inspection_record` | 巡检记录 |

### 4.3 核心枚举值

```java
// 用户类型
UserType: 1-超级管理员, 2-物业管理员, 3-业主

// 用户状态
UserStatus: 0-禁用, 1-正常

// 菜单类型
MenuType: 0-目录, 1-菜单, 2-按钮

// 房屋状态
HouseStatus: 0-空置, 1-已入住, 2-出租

// 业主类型
OwnerType: 1-本人, 2-家属, 3-租客

// 车位状态
ParkingStatus: 0-空闲, 1-已租, 2-已售, 3-维修中

// 收费项目类型
FeeItemType: 1-物业费, 2-车位费, 3-水费, 4-电费, 5-燃气费, 6-其他

// 收费周期
FeeCycleType: 1-按月, 2-按季, 3-按半年, 4-按年, 5-一次性

// 缴费状态
FeeRecordStatus: 0-待缴费, 1-已缴费, 2-逾期, 3-减免, 4-作废

// 缴费方式
PayType: 1-现金, 2-微信, 3-支付宝, 4-银行卡, 5-转账, 6-其他

// 设备状态
EquipmentStatus: 1-运行中, 2-停用, 3-维修中, 4-报废

// 维修类型
MaintenanceType: 1-日常维护, 2-故障维修, 3-定期检修, 4-技改升级

// 维修状态
MaintenanceStatus: 0-待派单, 1-处理中, 2-已完成, 3-已取消

// 报修状态
RepairStatus: 0-待受理, 1-处理中, 2-已完成, 3-已取消, 4-已评价

// 投诉状态
ComplaintStatus: 0-待受理, 1-处理中, 2-已回复, 3-已关闭

// 公告类型
AnnouncementType: 1-通知公告, 2-政策法规, 3-便民服务, 4-活动通知, 5-紧急通知

// 发布状态
PublishStatus: 0-草稿, 1-发布, 2-下架
```

---

## 5. API 接口文档

### 5.1 统一响应格式

```json
// 成功响应
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}

// 失败响应
{
  "code": 400,
  "message": "参数错误",
  "data": null
}

// 分页响应
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 5.2 认证模块 (/api/auth)

| 接口 | 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|------|
| 登录 | POST | /api/auth/login | 用户名密码登录，返回 Token | 否 |
| 退出 | POST | /api/auth/logout | 使 Token 失效 | 是 |
| 获取当前用户 | GET | /api/auth/me | 获取当前登录用户信息 | 是 |

**登录请求**
```json
POST /api/auth/login
{
  "username": "admin",
  "password": "123456"
}
```

**登录响应**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "超级管理员",
      "userType": 1,
      "roles": ["super_admin"],
      "permissions": ["system:user:list", "system:role:add", ...]
    }
  }
}
```

**请求头**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 5.3 系统管理模块

#### 用户管理 (/api/system/users)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/system/users/page | system:user:list |
| 新增 | POST | /api/system/users | system:user:add |
| 修改 | PUT | /api/system/users/{id} | system:user:edit |
| 删除 | DELETE | /api/system/users/{id} | system:user:delete |
| 重置密码 | PUT | /api/system/users/{id}/reset-pwd | system:user:resetPwd |
| 分配角色 | PUT | /api/system/users/{id}/roles | system:user:role |

#### 角色管理 (/api/system/roles)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/system/roles/page | system:role:list |
| 全部列表 | GET | /api/system/roles/all | system:role:list |
| 新增 | POST | /api/system/roles | system:role:add |
| 修改 | PUT | /api/system/roles/{id} | system:role:edit |
| 删除 | DELETE | /api/system/roles/{id} | system:role:delete |
| 分配菜单 | PUT | /api/system/roles/{id}/menus | system:role:menu |

#### 菜单管理 (/api/system/menus)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 树形列表 | GET | /api/system/menus/tree | system:menu:list |
| 新增 | POST | /api/system/menus | system:menu:add |
| 修改 | PUT | /api/system/menus/{id} | system:menu:edit |
| 删除 | DELETE | /api/system/menus/{id} | system:menu:delete |

### 5.4 小区基础模块

#### 楼栋管理 (/api/community/buildings)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/community/buildings/page | community:building:list |
| 列表查询 | GET | /api/community/buildings/list | community:building:list |
| 新增 | POST | /api/community/buildings | community:building:add |
| 修改 | PUT | /api/community/buildings/{id} | community:building:edit |
| 删除 | DELETE | /api/community/buildings/{id} | community:building:delete |
| 详情 | GET | /api/community/buildings/{id} | community:building:query |

#### 房屋管理 (/api/community/houses)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/community/houses/page | community:house:list |
| 新增 | POST | /api/community/houses | community:house:add |
| 修改 | PUT | /api/community/houses/{id} | community:house:edit |
| 删除 | DELETE | /api/community/houses/{id} | community:house:delete |
| 根据楼栋查询 | GET | /api/community/houses/by-building/{buildingId} | community:house:list |

#### 业主管理 (/api/community/owners)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/community/owners/page | community:owner:list |
| 新增 | POST | /api/community/owners | community:owner:add |
| 修改 | PUT | /api/community/owners/{id} | community:owner:edit |
| 删除 | DELETE | /api/community/owners/{id} | community:owner:delete |
| 关联用户 | PUT | /api/community/owners/{id}/bind-user | community:owner:bind |

#### 车位管理 (/api/community/parkings)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/community/parkings/page | community:parking:list |
| 新增 | POST | /api/community/parkings | community:parking:add |
| 修改 | PUT | /api/community/parkings/{id} | community:parking:edit |
| 删除 | DELETE | /api/community/parkings/{id} | community:parking:delete |
| 统计信息 | GET | /api/community/parkings/stats | community:parking:list |

### 5.5 收费管理模块

#### 收费项目 (/api/fee/items)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/fee/items/page | fee:item:list |
| 列表查询 | GET | /api/fee/items/list | fee:item:list |
| 新增 | POST | /api/fee/items | fee:item:add |
| 修改 | PUT | /api/fee/items/{id} | fee:item:edit |
| 删除 | DELETE | /api/fee/items/{id} | fee:item:delete |

#### 收费通知 (/api/fee/notices)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/fee/notices/page | fee:notice:list |
| 生成通知 | POST | /api/fee/notices/generate | fee:notice:generate |
| 批量发布 | POST | /api/fee/notices/batch-publish | fee:notice:publish |
| 详情 | GET | /api/fee/notices/{id} | fee:notice:query |

#### 缴费记录 (/api/fee/records)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/fee/records/page | fee:record:list |
| 缴费 | POST | /api/fee/records/pay | fee:record:pay |
| 退费 | POST | /api/fee/records/{id}/refund | fee:record:refund |
| 导出 | GET | /api/fee/records/export | fee:record:export |

### 5.6 设备管理模块

#### 设备分类 (/api/equipment/categories)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 树形列表 | GET | /api/equipment/categories/tree | equipment:category:list |
| 列表查询 | GET | /api/equipment/categories/list | equipment:category:list |
| 新增 | POST | /api/equipment/categories | equipment:category:add |
| 修改 | PUT | /api/equipment/categories/{id} | equipment:category:edit |
| 删除 | DELETE | /api/equipment/categories/{id} | equipment:category:delete |

#### 设备台账 (/api/equipment/equipments)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/equipment/equipments/page | equipment:equipment:list |
| 新增 | POST | /api/equipment/equipments | equipment:equipment:add |
| 修改 | PUT | /api/equipment/equipments/{id} | equipment:equipment:edit |
| 删除 | DELETE | /api/equipment/equipments/{id} | equipment:equipment:delete |
| 统计 | GET | /api/equipment/equipments/stats | equipment:equipment:list |

#### 维修记录 (/api/equipment/maintenances)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/equipment/maintenances/page | equipment:maintenance:list |
| 新增 | POST | /api/equipment/maintenances | equipment:maintenance:add |
| 修改 | PUT | /api/equipment/maintenances/{id} | equipment:maintenance:edit |
| 完成维修 | PUT | /api/equipment/maintenances/{id}/complete | equipment:maintenance:complete |
| 删除 | DELETE | /api/equipment/maintenances/{id} | equipment:maintenance:delete |

### 5.7 报修管理 (/api/repair/records)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/repair/records/page | repair:record:list |
| 我的报修 | GET | /api/repair/records/my | repair:record:my |
| 新增报修 | POST | /api/repair/records | repair:record:add |
| 受理 | PUT | /api/repair/records/{id}/accept | repair:record:accept |
| 处理完成 | PUT | /api/repair/records/{id}/complete | repair:record:complete |
| 评价 | POST | /api/repair/records/{id}/evaluate | repair:record:evaluate |

### 5.8 投诉建议 (/api/complaint/suggests)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/complaint/suggests/page | complaint:suggest:list |
| 我的投诉 | GET | /api/complaint/suggests/my | complaint:suggest:my |
| 新增投诉 | POST | /api/complaint/suggests | complaint:suggest:add |
| 回复处理 | PUT | /api/complaint/suggests/{id}/reply | complaint:suggest:reply |

### 5.9 公告通知 (/api/announcement/announcements)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 分页查询 | GET | /api/announcement/announcements/page | announcement:list |
| 发布列表 | GET | /api/announcement/announcements/published | announcement:list |
| 新增 | POST | /api/announcement/announcements | announcement:add |
| 修改 | PUT | /api/announcement/announcements/{id} | announcement:edit |
| 发布/下架 | PUT | /api/announcement/announcements/{id}/publish | announcement:publish |
| 置顶/取消 | PUT | /api/announcement/announcements/{id}/top | announcement:top |
| 阅读 | POST | /api/announcement/announcements/{id}/read | announcement:read |
| 详情 | GET | /api/announcement/announcements/{id} | announcement:query |

### 5.10 统计分析 (/api/statistics)
| 接口 | 方法 | 路径 | 权限标识 |
|------|------|------|----------|
| 收费统计 | GET | /api/statistics/fee | statistics:fee |
| 报修统计 | GET | /api/statistics/repair | statistics:repair |
| 投诉统计 | GET | /api/statistics/complaint | statistics:complaint |
| 设备统计 | GET | /api/statistics/equipment | statistics:equipment |
| 业主统计 | GET | /api/statistics/owner | statistics:owner |

---

## 6. 权限控制设计

### 6.1 RBAC 模型
```
用户 (User) 
    └── 拥有角色
        角色 (Role)
            └── 拥有权限
                菜单/按钮 (Menu/Permission)
                    └── 权限标识 (perms: "system:user:add")
```

### 6.2 权限标识命名规范
```
模块:功能:操作
system:user:list     - 用户列表
system:user:add      - 用户新增
system:user:edit     - 用户修改
system:user:delete   - 用户删除
system:user:resetPwd - 重置密码
system:role:list     - 角色列表
system:menu:tree     - 菜单树
community:building:list - 楼栋列表
fee:item:add         - 收费项目新增
equipment:maintenance:complete - 维修完成
```

### 6.3 JWT 配置
```properties
# application.properties
jwt.secret=propertyManagementSystemSecretKeyForJwtTokenGeneration
jwt.expiration=86400000  # 24小时，单位毫秒
```

### 6.4 无需认证的接口
```java
// SecurityConfig.java
.requestMatchers(
    "/api/auth/**",           // 认证相关
    "/api/announcement/announcements/published",  // 公开公告
    "/api/announcement/announcements/{id}",       // 公告详情
    "/error"                  // 错误页面
).permitAll()
```

---

## 7. 核心功能实现

### 7.1 统一响应封装
```java
// Result.java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) { ... }
    public static <T> Result<T> error(Integer code, String message) { ... }
    public static <T> Result<T> error(ResultCode resultCode) { ... }
}

// ResultCode.java
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "服务器内部错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    VALID_ERROR(400, "参数校验失败"),
    BUSINESS_ERROR(500, "业务异常");
}
```

### 7.2 全局异常处理
```java
// GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(ResultCode.VALID_ERROR.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.ERROR);
    }
}
```

### 7.3 MyBatis-Plus 自动填充
```java
// MyMetaObjectHandler.java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now);
        this.strictInsertFill(metaObject, "deleted", () -> 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now);
    }
}
```

### 7.4 逻辑删除配置
```java
// 实体类注解
@TableLogic(value = "0", delval = "1")
private Integer deleted;
```

### 7.5 定时任务
```java
// ScheduledTasks.java
@Component
@EnableScheduling
@Slf4j
public class ScheduledTasks {

    @Autowired
    private FeeNoticeService feeNoticeService;

    @Autowired
    private FeeRecordService feeRecordService;

    // 每天凌晨 1 点生成次月费用通知
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateMonthlyFeeNotices() {
        feeNoticeService.generateMonthlyNotices();
    }

    // 每天上午 9 点检查逾期缴费
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkOverdueFees() {
        feeRecordService.checkAndUpdateOverdue();
    }

    // 每周一上午 8 点生成设备巡检计划
    @Scheduled(cron = "0 0 8 ? * MON")
    public void generateInspectionPlans() {
        // 生成下周巡检计划
    }
}
```

---

## 8. 开发规范

### 8.1 代码结构规范
```
Controller -> Service -> Mapper -> Domain/DTO
```

### 8.2 命名规范
| 类型 | 规范 | 示例 |
|------|------|------|
| 包名 | 全小写，多级用点分隔 | com.lsy.propertymanagementsystem.module.system |
| 类名 | PascalCase | SysUserServiceImpl |
| 接口名 | PascalCase + Interface/无后缀 | SysUserService |
| 方法名 | camelCase | getUserById |
| 变量名 | camelCase | userName |
| 常量 | UPPER_SNAKE_CASE | MAX_PAGE_SIZE |
| 数据库表 | snake_case | sys_user |
| 数据库列 | snake_case | user_name |

### 8.3 注释规范
```java
/**
 * 用户服务实现类
 *
 * @author lsy
 * @since 2024-01-15
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserDomain> implements SysUserService {

    /**
     * 分页查询用户列表
     *
     * @param page 分页参数
     * @param query 查询条件
     * @return 用户分页列表
     */
    @Override
    public IPage<SysUserDomain> page(IPage<SysUserDomain> page, SysUserQuery query) {
        return lambdaQuery()
                .like(StrUtil.isNotBlank(query.getUsername()), SysUserDomain::getUsername, query.getUsername())
                .eq(query.getStatus() != null, SysUserDomain::getStatus, query.getStatus())
                .orderByDesc(SysUserDomain::getCreateTime)
                .page(page);
    }
}
```

### 8.4 Git 提交规范
```
feat: 新增功能
fix: 修复 Bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具/依赖更新
```

---

## 9. 部署指南

### 9.1 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.8+

### 9.2 数据库初始化
```bash
# 1. 创建数据库
mysql -u root -p < sql/01_schema.sql

# 2. 初始化基础数据
mysql -u root -p < sql/02_data.sql
```

### 9.3 修改配置
```properties
# application.properties
spring.datasource.url=jdbc:mysql://your-mysql-host:3306/property_management_system?...
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT 密钥（生产环境必须修改）
jwt.secret=your-secure-random-secret-key-at-least-32-chars
```

### 9.4 打包运行
```bash
# 编译打包
mvn clean package -DskipTests

# 运行
java -jar target/propertyManagementSystem-0.0.1-SNAPSHOT.jar

# 后台运行
nohup java -jar target/propertyManagementSystem-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### 9.5 Docker 部署
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/propertyManagementSystem-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# 构建镜像
docker build -t property-management-system .

# 运行容器
docker run -d \
  --name pms-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/property_management_system \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=123456 \
  -e JWT_SECRET=your-secret-key \
  property-management-system
```

---

## 10. 常见问题

### Q1: 启动报错 "Table 'sys_user' doesn't exist"
**A**: 请先执行 `sql/01_schema.sql` 创建表结构，再执行 `sql/02_data.sql` 初始化数据。

### Q2: JWT Token 过期时间如何修改？
**A**: 修改 `application.properties` 中的 `jwt.expiration`（单位：毫秒），默认 86400000（24小时）。

### Q3: 如何新增一个业务模块？
**A**: 
1. 在 `module` 下创建新模块包
2. 创建 Domain、DTO、Mapper、Service、Controller
3. 在数据库执行建表 SQL
4. 在 `sys_menu` 添加菜单权限
5. 配置 MyBatis-Plus Mapper 扫描路径

### Q4: 如何自定义 SQL？
**A**: 在 `resources/mapper` 下创建对应的 XML 文件，Mapper 接口方法名与 XML 中 `id` 一致。

### Q5: 密码加密方式？
**A**: 使用 BCrypt 加密，`PasswordUtils.encode(rawPassword)` 加密，`PasswordUtils.matches(rawPassword, encodedPassword)` 验证。

---

## 11. 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 0.0.1-SNAPSHOT | 2024-01-15 | 初始版本，基础框架搭建 |
| 0.0.2-SNAPSHOT | 2024-02-01 | 新增收费管理、设备管理模块 |
| 0.0.3-SNAPSHOT | 2024-03-01 | 新增报修、投诉、公告、统计模块 |
| 0.0.4-SNAPSHOT | 2024-04-01 | 优化权限控制，新增定时任务 |

---

## 12. 联系方式

- 项目地址：https://github.com/your-repo/property-management-system
- 问题反馈：https://github.com/your-repo/property-management-system/issues

---

*文档更新时间：2024-07-14*