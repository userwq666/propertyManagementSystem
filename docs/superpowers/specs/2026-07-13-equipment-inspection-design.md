# 设备巡检模块设计文档

> **版本：** v1.0
> **日期：** 2026-07-13
> **状态：** 已批准

---

## 一、模块概述

设备巡检模块用于管理小区设备信息、制定巡检计划、记录巡检结果。支持手动创建和周期自动生成巡检计划，记录设备巡检状态和异常情况。

### 1.1 功能范围

| 功能 | 说明 |
|------|------|
| 设备分类管理 | 预设常用分类 + 支持自定义扩展 |
| 设备管理 | 设备台账、状态跟踪、维护人员分配 |
| 巡检计划 | 手动创建 + 周期自动生成 |
| 巡检记录 | 记录巡检结果、异常描述、维修建议、预算、工时 |

### 1.2 不包含的功能

- 文件上传和拍照功能
- 维修工单管理（后续模块）
- 设备报废流程（简化为停用状态）

---

## 二、数据库设计

### 2.1 设备分类表 `equipment_category`

```sql
CREATE TABLE equipment_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否预设分类：0否 1是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '设备分类表';
```

**预设数据：**

| 分类名称 | 排序 | 是否预设 |
|----------|------|----------|
| 电梯 | 1 | 是 |
| 消防设备 | 2 | 是 |
| 门禁系统 | 3 | 是 |
| 监控设备 | 4 | 是 |
| 水泵 | 5 | 是 |
| 配电设备 | 6 | 是 |

### 2.2 设备表 `equipment`

```sql
CREATE TABLE equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    equipment_code VARCHAR(50) NOT NULL COMMENT '设备编号',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    location VARCHAR(200) COMMENT '安装位置',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0正常 1维修中 2停用',
    maintenance_user VARCHAR(50) COMMENT '维护人员',
    install_date DATE COMMENT '安装日期',
    warranty_date DATE COMMENT '保修到期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '设备表';
```

**字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| equipment_code | VARCHAR(50) | 设备编号，唯一标识 |
| category_id | BIGINT | 关联 equipment_category.id |
| status | TINYINT | 0正常、1维修中、2停用 |
| maintenance_user | VARCHAR(50) | 负责维护的人员姓名 |

### 2.3 巡检计划表 `inspection_plan`

```sql
CREATE TABLE inspection_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    plan_type TINYINT NOT NULL COMMENT '计划类型：0手动创建 1周期生成',
    cycle_type TINYINT COMMENT '周期类型：0每天 1每周 2每月 3自定义',
    cycle_value INT COMMENT '自定义周期值（每N天/周/月）',
    plan_date DATE COMMENT '计划日期（手动创建时使用）',
    start_date DATE COMMENT '周期开始日期',
    end_date DATE COMMENT '周期结束日期',
    equipment_ids VARCHAR(500) COMMENT '设备ID列表（逗号分隔）',
    inspector_ids VARCHAR(500) COMMENT '巡检人员ID列表（逗号分隔）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待执行 1执行中 2已完成 3已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '巡检计划表';
```

**字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| plan_type | TINYINT | 0手动创建、1周期生成 |
| cycle_type | TINYINT | 0每天、1每周、2每月、3自定义 |
| cycle_value | INT | 当 cycle_type=3 时，表示每N天/周/月 |
| equipment_ids | VARCHAR(500) | 逗号分隔的设备ID列表 |
| inspector_ids | VARCHAR(500) | 逗号分隔的巡检人员ID列表 |

### 2.4 巡检记录表 `inspection_record`

```sql
CREATE TABLE inspection_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT COMMENT '关联计划ID（可为空，支持临时巡检）',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    inspector_id BIGINT NOT NULL COMMENT '巡检人员ID',
    inspect_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '巡检时间',
    result TINYINT NOT NULL COMMENT '巡检结果：0正常 1一般异常 2严重异常',
    fault_desc VARCHAR(500) COMMENT '功能异常描述',
    repair_suggestion VARCHAR(500) COMMENT '维修建议',
    budget DECIMAL(10,2) COMMENT '预估费用',
    duration VARCHAR(50) COMMENT '预估工时',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
) COMMENT '巡检记录表';
```

**字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| plan_id | BIGINT | 可为空，支持临时巡检 |
| result | TINYINT | 0正常、1一般异常、2严重异常 |
| budget | DECIMAL(10,2) | 预估维修费用 |
| duration | VARCHAR(50) | 预估工时（如：2小时、1天） |

### 2.5 ER关系图

```
equipment_category ──< equipment
                          │
inspection_plan ──────────>│
                          │
                    inspection_record
```

---

## 三、后端设计

### 3.1 包结构

```
src/main/java/com/lsy/propertymanagementsystem/
├── entity/
│   ├── EquipmentCategory.java
│   ├── Equipment.java
│   ├── InspectionPlan.java
│   └── InspectionRecord.java
├── mapper/
│   ├── EquipmentCategoryMapper.java
│   ├── EquipmentMapper.java
│   ├── InspectionPlanMapper.java
│   └── InspectionRecordMapper.java
├── dto/request/
│   ├── EquipmentCategoryRequest.java
│   ├── EquipmentRequest.java
│   ├── InspectionPlanRequest.java
│   └── InspectionRecordRequest.java
├── service/
│   ├── EquipmentCategoryService.java
│   ├── EquipmentService.java
│   ├── InspectionPlanService.java
│   └── InspectionRecordService.java
├── service/impl/
│   ├── EquipmentCategoryServiceImpl.java
│   ├── EquipmentServiceImpl.java
│   ├── InspectionPlanServiceImpl.java
│   └── InspectionRecordServiceImpl.java
└── controller/
    ├── EquipmentCategoryController.java
    ├── EquipmentController.java
    ├── InspectionPlanController.java
    └── InspectionRecordController.java
```

### 3.2 Service接口设计

#### EquipmentCategoryService

```java
public interface EquipmentCategoryService {
    void add(EquipmentCategoryRequest request);
    void update(EquipmentCategoryRequest request);
    void delete(Long id);
    EquipmentCategory getById(Long id);
    List<EquipmentCategory> list();
}
```

#### EquipmentService

```java
public interface EquipmentService {
    void add(EquipmentRequest request);
    void update(EquipmentRequest request);
    void delete(Long id);
    Equipment getById(Long id);
    Page<Equipment> page(int pageNum, int pageSize, Long categoryId, Integer status);
    void updateStatus(Long id, Integer status);
}
```

#### InspectionPlanService

```java
public interface InspectionPlanService {
    void add(InspectionPlanRequest request);
    void update(InspectionPlanRequest request);
    void delete(Long id);
    InspectionPlan getById(Long id);
    Page<InspectionPlan> page(int pageNum, int pageSize, Integer status);
    void updateStatus(Long id, Integer status);
    void generateByCycle();
}
```

#### InspectionRecordService

```java
public interface InspectionRecordService {
    void add(InspectionRecordRequest request);
    void update(InspectionRecordRequest request);
    void delete(Long id);
    InspectionRecord getById(Long id);
    Page<InspectionRecord> page(int pageNum, int pageSize, Long equipmentId, Integer result);
    List<InspectionRecord> getByEquipmentId(Long equipmentId);
}
```

### 3.3 Controller设计

#### EquipmentCategoryController

```java
@RestController
@RequestMapping("/api/equipment/category")
public class EquipmentCategoryController {
    @PostMapping                    // 新增
    @PutMapping                     // 更新
    @DeleteMapping("/{id}")         // 删除
    @GetMapping("/{id}")            // 详情
    @GetMapping("/list")            // 列表（不分页）
}
```

#### EquipmentController

```java
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    @PostMapping                    // 新增
    @PutMapping                     // 更新
    @DeleteMapping("/{id}")         // 删除
    @GetMapping("/{id}")            // 详情
    @GetMapping("/page")            // 分页查询
    @PutMapping("/status")          // 更新状态
}
```

#### InspectionPlanController

```java
@RestController
@RequestMapping("/api/inspection/plan")
public class InspectionPlanController {
    @PostMapping                    // 新增
    @PutMapping                     // 更新
    @DeleteMapping("/{id}")         // 删除
    @GetMapping("/{id}")            // 详情
    @GetMapping("/page")            // 分页查询
    @PutMapping("/status")          // 更新状态
    @PostMapping("/generate")       // 手动生成周期计划
}
```

#### InspectionRecordController

```java
@RestController
@RequestMapping("/api/inspection/record")
public class InspectionRecordController {
    @PostMapping                    // 新增
    @PutMapping                     // 更新
    @DeleteMapping("/{id}")         // 删除
    @GetMapping("/{id}")            // 详情
    @GetMapping("/page")            // 分页查询
    @GetMapping("/equipment/{id}")  // 按设备查询历史
}
```

### 3.4 业务逻辑

#### 巡检计划自动生成

```
1. 检查周期性计划（plan_type=1）
2. 根据 cycle_type 和 cycle_value 计算下次执行日期
3. 如果下次执行日期 <= 今天，自动生成新的待执行任务
4. 更新计划的 start_date 为下次执行日期
```

#### 设备状态联动

```
巡检记录提交时：
- 如果 result=2（严重异常），提示是否将设备状态更新为"维修中"
- 维修完成后，手动将设备状态改回"正常"
```

---

## 四、前端设计

### 4.1 目录结构

```
frontend/src/
├── api/
│   ├── equipmentCategory.js
│   ├── equipment.js
│   ├── inspectionPlan.js
│   └── inspectionRecord.js
├── views/equipment/
│   ├── category/
│   │   └── index.vue          # 设备分类管理
│   ├── device/
│   │   └── index.vue          # 设备管理
│   └── inspection/
│       ├── plan/
│       │   ├── index.vue      # 巡检计划列表
│       │   └── add.vue        # 新增/编辑计划
│       └── record/
│           ├── index.vue      # 巡检记录列表
│           └── add.vue        # 新增巡检记录
```

### 4.2 页面设计

#### 设备分类页面

- 分类列表表格（分类名称、描述、排序、是否预设、操作）
- 新增/编辑弹窗
- 预设分类不可删除

#### 设备管理页面

- 搜索栏：按分类筛选、按状态筛选
- 设备列表表格（设备名称、编号、分类、位置、状态、维护人员、操作）
- 新增/编辑弹窗：选择分类、填写设备信息
- 状态切换按钮

#### 巡检计划页面

- 计划列表表格（计划名称、类型、周期、日期、状态、操作）
- 新增计划页面：
  - 计划类型选择（手动/周期）
  - 手动：选择日期 + 选择设备 + 选择巡检人员
  - 周期：设置周期类型 + 选择设备 + 选择巡检人员 + 生效时间段
- 执行/取消按钮

#### 巡检记录页面

- 搜索栏：按设备筛选、按结果筛选
- 记录列表表格（设备名称、巡检人员、时间、结果、操作）
- 新增记录页面：
  - 选择关联计划（可选）
  - 选择设备
  - 填写巡检结果、异常描述、维修建议、预算、工时

### 4.3 路由配置

```javascript
{
  path: 'equipment',
  name: 'Equipment',
  meta: { title: '设备巡检' },
  children: [
    {
      path: 'category',
      name: 'EquipmentCategory',
      component: () => import('../views/equipment/category/index.vue'),
      meta: { title: '设备分类' }
    },
    {
      path: 'device',
      name: 'Equipment',
      component: () => import('../views/equipment/device/index.vue'),
      meta: { title: '设备管理' }
    },
    {
      path: 'inspection/plan',
      name: 'InspectionPlan',
      component: () => import('../views/equipment/inspection/plan/index.vue'),
      meta: { title: '巡检计划' }
    },
    {
      path: 'inspection/plan/add',
      name: 'InspectionPlanAdd',
      component: () => import('../views/equipment/inspection/plan/add.vue'),
      meta: { title: '新增计划' }
    },
    {
      path: 'inspection/record',
      name: 'InspectionRecord',
      component: () => import('../views/equipment/inspection/record/index.vue'),
      meta: { title: '巡检记录' }
    },
    {
      path: 'inspection/record/add',
      name: 'InspectionRecordAdd',
      component: () => import('../views/equipment/inspection/record/add.vue'),
      meta: { title: '新增记录' }
    }
  ]
}
```

---

## 五、API接口设计

### 5.1 设备分类接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/equipment/category | 新增分类 |
| PUT | /api/equipment/category | 更新分类 |
| DELETE | /api/equipment/category/{id} | 删除分类 |
| GET | /api/equipment/category/{id} | 获取分类详情 |
| GET | /api/equipment/category/list | 获取分类列表 |

### 5.2 设备管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/equipment | 新增设备 |
| PUT | /api/equipment | 更新设备 |
| DELETE | /api/equipment/{id} | 删除设备 |
| GET | /api/equipment/{id} | 获取设备详情 |
| GET | /api/equipment/page | 分页查询设备 |
| PUT | /api/equipment/status | 更新设备状态 |

### 5.3 巡检计划接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/inspection/plan | 新增计划 |
| PUT | /api/inspection/plan | 更新计划 |
| DELETE | /api/inspection/plan/{id} | 删除计划 |
| GET | /api/inspection/plan/{id} | 获取计划详情 |
| GET | /api/inspection/plan/page | 分页查询计划 |
| PUT | /api/inspection/plan/status | 更新计划状态 |
| POST | /api/inspection/plan/generate | 手动生成周期计划 |

### 5.4 巡检记录接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/inspection/record | 新增记录 |
| PUT | /api/inspection/record | 更新记录 |
| DELETE | /api/inspection/record/{id} | 删除记录 |
| GET | /api/inspection/record/{id} | 获取记录详情 |
| GET | /api/inspection/record/page | 分页查询记录 |
| GET | /api/inspection/record/equipment/{id} | 按设备查询历史 |

---

## 六、状态枚举

### 6.1 设备状态

| 值 | 说明 |
|----|------|
| 0 | 正常 |
| 1 | 维修中 |
| 2 | 停用 |

### 6.2 巡检计划类型

| 值 | 说明 |
|----|------|
| 0 | 手动创建 |
| 1 | 周期生成 |

### 6.3 巡检周期类型

| 值 | 说明 |
|----|------|
| 0 | 每天 |
| 1 | 每周 |
| 2 | 每月 |
| 3 | 自定义 |

### 6.4 巡检计划状态

| 值 | 说明 |
|----|------|
| 0 | 待执行 |
| 1 | 执行中 |
| 2 | 已完成 |
| 3 | 已取消 |

### 6.5 巡检结果

| 值 | 说明 |
|----|------|
| 0 | 正常 |
| 1 | 一般异常 |
| 2 | 严重异常 |

---

## 七、开发任务

### 7.1 后端任务

1. 创建数据库表结构
2. 创建实体类
3. 创建Mapper接口
4. 创建DTO请求类
5. 创建Service接口和实现
6. 创建Controller

### 7.2 前端任务

1. 创建API文件
2. 创建设备分类页面
3. 创建设备管理页面
4. 创建巡检计划页面
5. 创建巡检记录页面
6. 配置路由

### 7.3 测试任务

1. 后端编译验证
2. 前端构建验证
3. 接口测试

---

## 八、风险与注意事项

1. **巡检计划自动生成**：需要考虑定时任务的实现方式（Spring Scheduler或手动触发）
2. **设备编号唯一性**：需要在数据库层面添加唯一索引
3. **巡检人员选择**：需要从具有"维护人员"角色的用户中选择
4. **预设分类保护**：预设分类不可删除，需要在前端和后端双重校验

---

**设计文档版本：** v1.0
**最后更新：** 2026-07-13
