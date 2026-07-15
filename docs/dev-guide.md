# 后端开发指南

## 1. 开发环境搭建

### 1.1 必备工具
| 工具 | 版本要求 | 下载地址 |
|------|----------|----------|
| JDK | 17+ | https://adoptium.net/ |
| Maven | 3.8+ | https://maven.apache.org/ |
| MySQL | 8.0+ | https://dev.mysql.com/ |
| IDE | IntelliJ IDEA 2023+ / VS Code | - |
| Git | 2.30+ | https://git-scm.com/ |

### 1.2 IDE 配置 (IntelliJ IDEA)
1. **安装插件**: Lombok, MyBatis-Plus, MapStruct
2. **开启 Annotation Processors**: Settings → Build → Compiler → Annotation Processors → Enable annotation processing
3. **配置 Lombok**: Settings → Plugins → 搜索 Lombok → 安装重启
4. **代码格式化**: 导入 `google-java-format` 或使用项目 `.editorconfig`

### 1.3 项目导入
```bash
# 克隆项目
git clone <repository-url>

# 进入后端目录
cd propertyManagementSystem

# 编译项目 (首次需下载依赖)
mvn clean compile

# 运行测试
mvn test
```

---

## 2. 项目结构规范

### 2.1 包结构说明
```
com.lsy.propertymanagementsystem
├── PropertyManagementSystemApplication.java   # 启动类
├── config/                                    # 配置类包
│   ├── SecurityConfig.java                   # Spring Security 配置
│   ├── JwtConfig.java                        # JWT 配置属性类
│   ├── WebMvcConfig.java                     # Web MVC 配置
│   ├── MyMetaObjectHandler.java              # MP 自动填充处理器
│   └── DatabaseInitializer.java              # 数据库初始化
├── common/                                   # 通用基础包
│   ├── result/                               # 统一响应
│   │   ├── Result.java
│   │   └── ResultCode.java
│   ├── exception/                            # 异常处理
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   └── utils/                                # 工具类
│       ├── JwtUtils.java
│       └── PasswordUtils.java
├── interceptor/                              # 拦截器
│   └── JwtInterceptor.java
├── task/                                     # 定时任务
│   └── ScheduledTasks.java
└── module/                                   # 业务模块包
    ├── system/                               # 系统管理
    ├── community/                            # 小区基础
    ├── fee/                                  # 收费管理
    ├── equipment/                            # 设备管理
    ├── repair/                               # 报修管理
    ├── complaint/                            # 投诉建议
    ├── announcement/                         # 公告通知
    └── statistics/                           # 统计分析
```

### 2.2 模块内部结构 (标准)
```
module/xxx/
├── controller/           # 控制层 - 接收请求、参数校验、返回响应
├── service/              # 业务层接口
├── service/impl/         # 业务层实现
├── mapper/               # 数据访问层 (MyBatis-Plus Mapper)
├── domain/               # 实体类 (对应数据库表)
├── dto/                  # 数据传输对象
│   ├── request/          # 请求 DTO
│   └── response/         # 响应 DTO
├── vo/                   # 视图对象 (前端展示用)
├── query/                # 查询参数对象
├── converter/            # DTO/DO/VO 互转
├── enums/                # 枚举类
├── constant/             # 常量定义
└── handler/              # 类型处理器 (如枚举转换)
```

---

## 3. 代码开发规范

### 3.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 包名 | 全小写，多级用点分隔 | `com.lsy.propertymanagementsystem.module.system` |
| 类名 | PascalCase | `SysUserServiceImpl` |
| 接口名 | PascalCase | `SysUserService` |
| 抽象类 | Abstract + PascalCase | `AbstractBaseService` |
| 枚举类 | PascalCase + Enum/无后缀 | `UserStatus` / `UserType` |
| 异常类 | PascalCase + Exception | `BusinessException` |
| 测试类 | PascalCase + Test | `SysUserServiceTest` |
| 方法名 | camelCase | `getUserById` |
| 变量名 | camelCase | `userName` |
| 常量 | UPPER_SNAKE_CASE | `MAX_PAGE_SIZE` |
| 数据库表 | snake_case | `sys_user` |
| 数据库列 | snake_case | `user_name` |

### 3.2 注释规范

#### 类注释
```java
/**
 * 系统用户服务实现类
 *
 * @author lsy
 * @since 2024-01-15
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserDomain> implements SysUserService {
```

#### 方法注释
```java
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
```

#### 字段注释 (实体类)
```java
@Data
@TableName("sys_user")
public class SysUserDomain {

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    @TableField("username")
    private String username;

    /** BCrypt加密密码 */
    @TableField("password")
    private String password;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
```

### 3.3 实体类规范

```java
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
@KeySequence("sys_user")  // Oracle/PostgreSQL序列，MySQL可省略
public class SysUserDomain extends BaseDomain {  // 可继承基类包含公共字段

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    // 密码不序列化到前端
    @JSONField(serialize = false)
    @TableField("password")
    private String password;

    @TableField("real_name")
    private String realName;

    @TableField("user_type")
    private Integer userType;

    @TableField("status")
    private Integer status;

    // 自动填充字段
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 逻辑删除
    @TableLogic(value = "0", delval = "1")
    @TableField("deleted")
    private Integer deleted;

    // 非数据库字段
    @TableField(exist = false)
    private List<Long> roleIds;

    @TableField(exist = false)
    private List<String> roleNames;
}
```

### 3.4 DTO 规范

```java
@Data
@ApiModel("用户新增请求")
public class UserAddRequest {

    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50")
    private String username;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;

    @ApiModelProperty("真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50")
    private String realName;

    @ApiModelProperty("手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty("用户类型")
    @NotNull(message = "用户类型不能为空")
    @Range(min = 1, max = 3, message = "用户类型不正确")
    private Integer userType;

    @ApiModelProperty("角色ID列表")
    private List<Long> roleIds;
}
```

### 3.5 Controller 规范

```java
@RestController
@RequestMapping("/api/system/users")
@Api(tags = "用户管理")
@RequiredArgsConstructor  // Lombok 自动生成构造器注入
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/page")
    @ApiOperation("分页查询用户")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    public Result<IPage<SysUserDomain>> page(
            @Valid PageQuery pageQuery,
            @Valid SysUserQuery query) {
        IPage<SysUserDomain> page = sysUserService.page(pageQuery.toPage(), query);
        return Result.success(page);
    }

    @PostMapping
    @ApiOperation("新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @OperationLog(module = "用户管理", type = "新增")
    public Result<Void> add(@Valid @RequestBody UserAddRequest request) {
        sysUserService.add(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public Result<Void> edit(@PathVariable Long id, @Valid @RequestBody UserEditRequest request) {
        sysUserService.edit(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteById(id);
        return Result.success();
    }
}
```

### 3.6 Service 规范

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserDomain> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordUtils passwordUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(UserAddRequest request) {
        // 1. 校验用户名唯一
        if (lambdaQuery().eq(SysUserDomain::getUsername, request.getUsername()).count() > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 2. 构建实体
        SysUserDomain user = new SysUserDomain();
        user.setUsername(request.getUsername());
        user.setPassword(passwordUtils.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        // 3. 保存用户
        save(user);

        // 4. 保存用户角色关联
        if (CollUtil.isNotEmpty(request.getRoleIds())) {
            List<SysUserRoleDomain> list = request.getRoleIds().stream()
                    .map(roleId -> {
                        SysUserRoleDomain ur = new SysUserRoleDomain();
                        ur.setUserId(user.getId());
                        ur.setRoleId(roleId);
                        return ur;
                    }).toList();
            sysUserRoleMapper.insertBatch(list);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, UserEditRequest request) {
        SysUserDomain user = getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 校验用户名唯一 (排除自己)
        if (lambdaQuery()
                .eq(SysUserDomain::getUsername, request.getUsername())
                .ne(SysUserDomain::getId, id)
                .count() > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(request.getStatus());
        updateById(user);

        // 更新角色关联
        if (CollUtil.isNotEmpty(request.getRoleIds())) {
            sysUserRoleMapper.delete(new LambdaUpdateWrapper<SysUserRoleDomain>()
                    .eq(SysUserRoleDomain::getUserId, id));
            List<SysUserRoleDomain> list = request.getRoleIds().stream()
                    .map(roleId -> {
                        SysUserRoleDomain ur = new SysUserRoleDomain();
                        ur.setUserId(id);
                        ur.setRoleId(roleId);
                        return ur;
                    }).toList();
            sysUserRoleMapper.insertBatch(list);
        }
    }
}
```

### 3.7 Mapper 规范

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserDomain> {

    /**
     * 根据用户名查询用户
     */
    SysUserDomain selectByUsername(String username);

    /**
     * 查询用户拥有的角色权限标识
     */
    List<String> selectRoleKeysByUserId(Long userId);

    /**
     * 查询用户拥有的菜单权限标识
     */
    List<String> selectPermsByUserId(Long userId);
}
```

### 3.8 XML 映射文件规范

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper">

    <resultMap id="BaseResultMap" type="com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain">
        <id column="id" property="id" jdbcType="BIGINT"/>
        <result column="username" property="username" jdbcType="VARCHAR"/>
        <result column="password" property="password" jdbcType="VARCHAR"/>
        <result column="real_name" property="realName" jdbcType="VARCHAR"/>
        <result column="phone" property="phone" jdbcType="VARCHAR"/>
        <result column="avatar" property="avatar" jdbcType="VARCHAR"/>
        <result column="user_type" property="userType" jdbcType="TINYINT"/>
        <result column="status" property="status" jdbcType="TINYINT"/>
        <result column="create_time" property="createTime" jdbcType="TIMESTAMP"/>
        <result column="update_time" property="updateTime" jdbcType="TIMESTAMP"/>
        <result column="deleted" property="deleted" jdbcType="TINYINT"/>
    </resultMap>

    <sql id="Base_Column_List">
        id, username, password, real_name, phone, avatar, user_type, status, create_time, update_time, deleted
    </sql>

    <select id="selectByUsername" resultMap="BaseResultMap">
        SELECT <include refid="Base_Column_List"/>
        FROM sys_user
        WHERE username = #{username} AND deleted = 0
    </select>

    <select id="selectRoleKeysByUserId" resultType="java.lang.String">
        SELECT r.role_key
        FROM sys_role r
        INNER JOIN sys_user_role ur ON r.id = ur.role_id
        WHERE ur.user_id = #{userId} AND r.deleted = 0 AND r.status = 1
    </select>

    <select id="selectPermsByUserId" resultType="java.lang.String">
        SELECT DISTINCT m.perms
        FROM sys_menu m
        INNER JOIN sys_role_menu rm ON m.id = rm.menu_id
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
        AND m.deleted = 0 AND m.status = 1
        AND m.perms IS NOT NULL AND m.perms != ''
    </select>

</mapper>
```

---

## 4. 核心功能开发指南

### 4.1 新增业务模块步骤

#### 步骤 1: 创建数据库表
```sql
-- 在 sql/ 目录下创建迁移脚本或直接执行
CREATE TABLE module_xxx (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '名称',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '业务表';
```

#### 步骤 2: 创建实体类
```java
// module/xxx/domain/XxxDomain.java
@Data
@TableName("module_xxx")
public class XxxDomain extends BaseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("name")
    private String name;
    
    @TableField("status")
    private Integer status;
}
```

#### 步骤 3: 创建 Mapper 接口
```java
// module/xxx/mapper/XxxMapper.java
@Mapper
public interface XxxMapper extends BaseMapper<XxxDomain> {
}
```

#### 步骤 4: 创建 Service 接口和实现
```java
// module/xxx/service/XxxService.java
public interface XxxService extends IService<XxxDomain> {
    IPage<XxxDomain> page(IPage<XxxDomain> page, XxxQuery query);
    void add(XxxAddRequest request);
    void edit(Long id, XxxEditRequest request);
    void deleteById(Long id);
}

// module/xxx/service/impl/XxxServiceImpl.java
@Service
@RequiredArgsConstructor
public class XxxServiceImpl extends ServiceImpl<XxxMapper, XxxDomain> implements XxxService {
    // 实现业务逻辑
}
```

#### 步骤 5: 创建 Controller
```java
// module/xxx/controller/XxxController.java
@RestController
@RequestMapping("/api/xxx/items")
@Api(tags = "XXX管理")
@RequiredArgsConstructor
public class XxxController {
    private final XxxService xxxService;
    
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('xxx:item:list')")
    public Result<IPage<XxxDomain>> page(PageQuery pageQuery, XxxQuery query) {
        return Result.success(xxxService.page(pageQuery.toPage(), query));
    }
    // 其他 CRUD 方法...
}
```

#### 步骤 6: 配置菜单权限
在数据库 `sys_menu` 表插入菜单数据，包含权限标识如 `xxx:item:list`

#### 步骤 7: 编写单元测试
```java
// test/java/.../XxxServiceTest.java
@SpringBootTest
class XxxServiceTest {
    @Autowired
    private XxxService xxxService;
    
    @Test
    void testAdd() {
        XxxAddRequest request = new XxxAddRequest();
        request.setName("测试");
        xxxService.add(request);
        // 断言验证
    }
}
```

---

### 4.2 事务管理

```java
@Service
@RequiredArgsConstructor
public class XxxServiceImpl {

    @Transactional(rollbackFor = Exception.class)
    public void complexBusinessMethod() {
        // 多表操作，自动事务管理
        // 抛出 RuntimeException 或 Error 会回滚
        // 抛出 checked exception 需要配置 rollbackFor
    }
    
    // 只读事务优化
    @Transactional(readOnly = true)
    public XxxDomain getDetail(Long id) {
        return getById(id);
    }
}
```

**事务传播行为选择**:
| 场景 | 传播行为 | 注解 |
|------|----------|------|
| 默认，加入现有事务 | REQUIRED | `@Transactional` |
| 新事务，挂起现有 | REQUIRES_NEW | `@Transactional(propagation = Propagation.REQUIRES_NEW)` |
| 不支持事务 | NOT_SUPPORTED | `@Transactional(propagation = Propagation.NOT_SUPPORTED)` |

---

### 4.3 分页查询最佳实践

```java
// 通用分页查询参数基类
@Data
public class PageQuery {
    private Integer current = 1;
    private Integer size = 10;
    
    public <T> Page<T> toPage() {
        return new Page<>(current, Math.min(size, 100)); // 限制最大100
    }
}

// Service 中使用
public IPage<XxxDomain> page(IPage<XxxDomain> page, XxxQuery query) {
    return lambdaQuery()
            .like(StrUtil.isNotBlank(query.getName()), XxxDomain::getName, query.getName())
            .eq(query.getStatus() != null, XxxDomain::getStatus, query.getStatus())
            .ge(query.getStartDate() != null, XxxDomain::getCreateTime, query.getStartDate())
            .le(query.getEndDate() != null, XxxDomain::getCreateTime, query.getEndDate())
            .orderByDesc(XxxDomain::getCreateTime)
            .page(page);
}
```

---

### 4.4 参数校验

```java
// Controller 方法参数校验
@PostMapping
public Result<Void> add(@Valid @RequestBody XxxAddRequest request) {
    // @Valid 触发 JSR-303 校验
    // 校验失败自动抛出 MethodArgumentNotValidException
    // 由 GlobalExceptionHandler 统一处理
    xxxService.add(request);
    return Result.success();
}

// 自定义校验注解
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface ValidPhone {
    String message() default "手机号格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 校验器实现
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && PHONE_PATTERN.matcher(value).matches();
    }
}
```

---

### 4.5 异常处理

```java
// 业务异常
public class BusinessException extends RuntimeException {
    private final Integer code;
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
    }
    
    public BusinessException(ResultCode resultCode, String message) {
        this(resultCode.getCode(), message);
    }
}

// 抛出业务异常
if (user == null) {
    throw new BusinessException(ResultCode.USER_NOT_FOUND);
}
if (lambdaQuery().eq(SysUserDomain::getUsername, username).count() > 0) {
    throw new BusinessException(ResultCode.USERNAME_EXISTS);
}
```

---

## 5. 常用工具类

### 5.1 密码工具
```java
@Component
public class PasswordUtils {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(10);
    
    public String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}
```

### 5.2 JWT 工具
```java
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtUtils {
    private String secret;
    private long expiration;
    
    public String generateToken(UserDetails userDetails) {
        // 生成 JWT Token
    }
    
    public String getUsernameFromToken(String token) {
        // 解析用户名
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        // 验证 Token
    }
}
```

### 5.3 通用工具
```java
// Bean 拷贝工具
public class BeanUtils {
    public static <S, T> T copyProperties(S source, Class<T> targetClass) {
        T target = BeanUtils.instantiateClass(targetClass);
        org.springframework.beans.BeanUtils.copyProperties(source, target);
        return target;
    }
    
    public static <S, T> List<T> copyList(Collection<S> sources, Class<T> targetClass) {
        return sources.stream()
                .map(s -> copyProperties(s, targetClass))
                .collect(Collectors.toList());
    }
}
```

---

## 6. 测试规范

### 6.1 单元测试
```java
@SpringBootTest
@Transactional
@Rollback
class SysUserServiceTest {

    @Autowired
    private SysUserService sysUserService;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Test
    @DisplayName("新增用户 - 成功")
    void testAddUser() {
        // given
        UserAddRequest request = new UserAddRequest();
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setRealName("测试用户");
        request.setUserType(2);
        
        // when
        sysUserService.add(request);
        
        // then
        SysUserDomain user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUserDomain>()
                        .eq(SysUserDomain::getUsername, "testuser"));
        assertNotNull(user);
        assertEquals("测试用户", user.getRealName());
        assertTrue(passwordUtils.matches("123456", user.getPassword()));
    }
    
    @Test
    @DisplayName("新增用户 - 用户名已存在")
    void testAddUserDuplicate() {
        // given - 先插入一个用户
        // when & then
        assertThrows(BusinessException.class, () -> {
            sysUserService.add(request);
        });
    }
}
```

### 6.2 集成测试
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testPage() throws Exception {
        mockMvc.perform(get("/api/system/users/page")
                .header("Authorization", "Bearer " + adminToken)
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

---

## 7. 性能优化指南

### 7.1 数据库优化
- **索引**: 高频查询字段建立索引，联合索引遵循最左前缀原则
- **分页**: 避免深分页，使用 `id > lastId` 方式优化
- **批量操作**: 使用 `insertBatch`、`updateBatchById` 批量处理
- **只查所需**: 避免 `SELECT *`，指定查询列

### 7.2 缓存策略
```java
@Service
public class SysConfigServiceImpl {
    
    @Cacheable(value = "config", key = "'config:' + #key")
    public String getConfig(String key) {
        return sysConfigMapper.selectValueByKey(key);
    }
    
    @CacheEvict(value = "config", key = "'config:' + #key")
    public void refreshConfig(String key) {
        // 清除缓存
    }
}
```

### 7.3 异步处理
```java
@Service
public class NotificationService {
    
    @Async("taskExecutor")
    public void sendAsyncNotification(Long userId, String content) {
        // 发送通知、短信、邮件等耗时操作
    }
}
```

---

## 8. 部署与运维

### 8.1 打包
```bash
# 跳过测试打包
mvn clean package -DskipTests

# 包含测试打包
mvn clean package
```

### 8.2 运行参数
```bash
# 生产环境运行
java -Xms512m -Xmx1024m -XX:+UseG1GC \
  -Dspring.profiles.active=prod \
  -jar propertyManagementSystem.jar

# 指定配置文件
java -jar propertyManagementSystem.jar \
  --spring.config.location=file:/opt/config/application-prod.yml
```

### 8.3 健康检查
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

### 8.4 日志配置
```xml
<!-- logback-spring.xml -->
<configuration>
    <springProperty scope="context" name="appName" source="spring.application.name"/>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/${appName}.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/${appName}.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

---

## 9. 常见问题解决

### Q1: 实体类字段与数据库列名不一致
**A**: 使用 `@TableField("db_column_name")` 指定映射，或开启 `map-underscore-to-camel-case=true`

### Q2: 逻辑删除不生效
**A**: 
1. 实体类字段添加 `@TableLogic(value = "0", delval = "1")`
2. Mapper 继承 `BaseMapper`
3. 查询时会自动添加 `deleted = 0` 条件

### Q3: 事务不回滚
**A**: 
1. 确保方法是 `public` 且抛出 `RuntimeException` 或配置 `rollbackFor`
2. 不要在类内部调用事务方法 (需通过 AOP 代理)
3. 检查数据库引擎是否支持事务

### Q4: 分页查询总数为 0
**A**: MyBatis-Plus 分页插件需要配置 `MybatisPlusInterceptor` 中的 `PaginationInnerInterceptor`

### Q5: 枚举存储为数字但查询为字符串
**A**: 使用 `@EnumValue` 注解或实现 `IEnum` 接口，配合 MyBatis-Plus 类型处理器

---

## 10. 代码生成器使用

### 10.1 MyBatis-Plus 代码生成
```java
// 可使用 MyBatis-Plus Generator 快速生成基础代码
// 配置示例：
FastAutoGenerator.create("jdbc:mysql://localhost:3306/db", "root", "123456")
    .globalConfig(builder -> builder
        .author("lsy")
        .outputDir("src/main/java")
        .enableSwagger()
        .fileOverride())
    .packageConfig(builder -> builder
        .parent("com.lsy.propertymanagementsystem")
        .moduleName("module-name")
        .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "src/main/resources/mapper")))
    .strategyConfig(builder -> builder
        .addInclude("table_name")
        .addTablePrefix("prefix_")
        .entityBuilder()
        .enableLombok()
        .controllerBuilder()
        .enableRestStyle())
    .execute();
```

---

*文档版本: v1.0*
*更新时间: 2024-07-14*