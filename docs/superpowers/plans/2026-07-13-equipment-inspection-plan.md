# 设备巡检模块实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现设备巡检模块，包括设备分类管理、设备管理、巡检计划、巡检记录四个子模块

**Architecture:** 采用与现有模块一致的架构：4个数据库表（equipment_category、equipment、inspection_plan、inspection_record），后端标准四层架构（Controller→Service→Mapper→DB），前端Vue3 + Element Plus

**Tech Stack:** SpringBoot 4.x + MyBatis-Plus + MySQL + Vue3 + Element Plus

---

## 文件结构

### 后端文件
- Create: `sql/equipment.sql` — 数据库表结构
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/EquipmentCategory.java` — 设备分类实体类
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/Equipment.java` — 设备实体类
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/InspectionPlan.java` — 巡检计划实体类
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/InspectionRecord.java` — 巡检记录实体类
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/EquipmentCategoryMapper.java` — 设备分类Mapper
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/EquipmentMapper.java` — 设备Mapper
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/InspectionPlanMapper.java` — 巡检计划Mapper
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/InspectionRecordMapper.java` — 巡检记录Mapper
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/EquipmentCategoryRequest.java` — 设备分类请求DTO
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/EquipmentRequest.java` — 设备请求DTO
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/InspectionPlanRequest.java` — 巡检计划请求DTO
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/InspectionRecordRequest.java` — 巡检记录请求DTO
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/EquipmentCategoryService.java` — 设备分类Service接口
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/EquipmentService.java` — 设备Service接口
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/InspectionPlanService.java` — 巡检计划Service接口
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/InspectionRecordService.java` — 巡检记录Service接口
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/EquipmentCategoryServiceImpl.java` — 设备分类Service实现
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/EquipmentServiceImpl.java` — 设备Service实现
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/InspectionPlanServiceImpl.java` — 巡检计划Service实现
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/InspectionRecordServiceImpl.java` — 巡检记录Service实现
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/EquipmentCategoryController.java` — 设备分类Controller
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/EquipmentController.java` — 设备Controller
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/InspectionPlanController.java` — 巡检计划Controller
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/InspectionRecordController.java` — 巡检记录Controller

### 前端文件
- Create: `frontend/src/api/equipmentCategory.js` — 设备分类API
- Create: `frontend/src/api/equipment.js` — 设备API
- Create: `frontend/src/api/inspectionPlan.js` — 巡检计划API
- Create: `frontend/src/api/inspectionRecord.js` — 巡检记录API
- Create: `frontend/src/views/equipment/category/index.vue` — 设备分类页面
- Create: `frontend/src/views/equipment/device/index.vue` — 设备管理页面
- Create: `frontend/src/views/equipment/inspection/plan/index.vue` — 巡检计划列表页面
- Create: `frontend/src/views/equipment/inspection/plan/add.vue` — 巡检计划新增/编辑页面
- Create: `frontend/src/views/equipment/inspection/record/index.vue` — 巡检记录列表页面
- Create: `frontend/src/views/equipment/inspection/record/add.vue` — 巡检记录新增页面
- Modify: `frontend/src/router/index.js` — 添加路由配置

---

## Task 1: 创建数据库表结构

**Files:**
- Create: `sql/equipment.sql`

- [ ] **Step 1: 创建数据库表**

```sql
-- 设备分类表
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

-- 插入预设分类数据
INSERT INTO equipment_category (category_name, description, sort_order, is_default) VALUES
('电梯', '电梯设备', 1, 1),
('消防设备', '消防设备', 2, 1),
('门禁系统', '门禁系统', 3, 1),
('监控设备', '监控设备', 4, 1),
('水泵', '水泵设备', 5, 1),
('配电设备', '配电设备', 6, 1);

-- 设备表
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
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_equipment_code (equipment_code)
) COMMENT '设备表';

-- 巡检计划表
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

-- 巡检记录表
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

- [ ] **Step 2: 验证表结构**

```bash
mysql -u root -p123456 property_management_system < sql/equipment.sql
```

- [ ] **Step 3: 提交**

```bash
git add sql/equipment.sql
git commit -m "feat: 创建设备巡检模块数据库表结构"
```

---

## Task 2: 创建后端实体类

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/EquipmentCategory.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/Equipment.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/InspectionPlan.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/InspectionRecord.java`

- [ ] **Step 1: 创建设备分类实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("equipment_category")
public class EquipmentCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String categoryName;
    private String description;
    private Integer sortOrder;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 2: 创建设备实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String equipmentName;
    private String equipmentCode;
    private Long categoryId;
    private String location;
    private Integer status;
    private String maintenanceUser;
    private LocalDate installDate;
    private LocalDate warrantyDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 3: 创建巡检计划实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("inspection_plan")
public class InspectionPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String planName;
    private Integer planType;
    private Integer cycleType;
    private Integer cycleValue;
    private LocalDate planDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String equipmentIds;
    private String inspectorIds;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 4: 创建巡检记录实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inspection_record")
public class InspectionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long equipmentId;
    private Long inspectorId;
    private LocalDateTime inspectTime;
    private Integer result;
    private String faultDesc;
    private String repairSuggestion;
    private BigDecimal budget;
    private String duration;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/EquipmentCategory.java
git add src/main/java/com/lsy/propertymanagementsystem/entity/Equipment.java
git add src/main/java/com/lsy/propertymanagementsystem/entity/InspectionPlan.java
git add src/main/java/com/lsy/propertymanagementsystem/entity/InspectionRecord.java
git commit -m "feat: 创建设备巡检模块实体类"
```

---

## Task 3: 创建后端Mapper接口

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/EquipmentCategoryMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/EquipmentMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/InspectionPlanMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/InspectionRecordMapper.java`

- [ ] **Step 1: 创建设备分类Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.EquipmentCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EquipmentCategoryMapper extends BaseMapper<EquipmentCategory> {
}
```

- [ ] **Step 2: 创建设备Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {
}
```

- [ ] **Step 3: 创建巡检计划Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.InspectionPlan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InspectionPlanMapper extends BaseMapper<InspectionPlan> {
}
```

- [ ] **Step 4: 创建巡检记录Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.InspectionRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InspectionRecordMapper extends BaseMapper<InspectionRecord> {
}
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/mapper/EquipmentCategoryMapper.java
git add src/main/java/com/lsy/propertymanagementsystem/mapper/EquipmentMapper.java
git add src/main/java/com/lsy/propertymanagementsystem/mapper/InspectionPlanMapper.java
git add src/main/java/com/lsy/propertymanagementsystem/mapper/InspectionRecordMapper.java
git commit -m "feat: 创建设备巡检模块Mapper接口"
```

---

## Task 4: 创建后端DTO和Service

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/EquipmentCategoryRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/EquipmentRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/InspectionPlanRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/InspectionRecordRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/EquipmentCategoryService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/EquipmentService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/InspectionPlanService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/InspectionRecordService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/EquipmentCategoryServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/EquipmentServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/InspectionPlanServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/InspectionRecordServiceImpl.java`

- [ ] **Step 1: 创建设备分类Request DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipmentCategoryRequest {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    private String description;
    private Integer sortOrder;
}
```

- [ ] **Step 2: 创建设备Request DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EquipmentRequest {
    private Long id;

    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @NotBlank(message = "设备编号不能为空")
    private String equipmentCode;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    private String location;
    private Integer status;
    private String maintenanceUser;
    private LocalDate installDate;
    private LocalDate warrantyDate;
    private String remark;
}
```

- [ ] **Step 3: 创建巡检计划Request DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InspectionPlanRequest {
    private Long id;

    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @NotNull(message = "计划类型不能为空")
    private Integer planType;

    private Integer cycleType;
    private Integer cycleValue;
    private LocalDate planDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String equipmentIds;
    private String inspectorIds;
}
```

- [ ] **Step 4: 创建巡检记录Request DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InspectionRecordRequest {
    private Long id;
    private Long planId;

    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    @NotNull(message = "巡检人员ID不能为空")
    private Long inspectorId;

    @NotNull(message = "巡检结果不能为空")
    private Integer result;

    private String faultDesc;
    private String repairSuggestion;
    private BigDecimal budget;
    private String duration;
}
```

- [ ] **Step 5: 创建设备分类Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.lsy.propertymanagementsystem.dto.request.EquipmentCategoryRequest;
import com.lsy.propertymanagementsystem.entity.EquipmentCategory;

import java.util.List;

public interface EquipmentCategoryService {
    void add(EquipmentCategoryRequest request);
    void update(EquipmentCategoryRequest request);
    void delete(Long id);
    EquipmentCategory getById(Long id);
    List<EquipmentCategory> list();
}
```

- [ ] **Step 6: 创建设备Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.EquipmentRequest;
import com.lsy.propertymanagementsystem.entity.Equipment;

public interface EquipmentService {
    void add(EquipmentRequest request);
    void update(EquipmentRequest request);
    void delete(Long id);
    Equipment getById(Long id);
    Page<Equipment> page(int pageNum, int pageSize, Long categoryId, Integer status);
    void updateStatus(Long id, Integer status);
}
```

- [ ] **Step 7: 创建巡检计划Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.InspectionPlanRequest;
import com.lsy.propertymanagementsystem.entity.InspectionPlan;

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

- [ ] **Step 8: 创建巡检记录Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.InspectionRecordRequest;
import com.lsy.propertymanagementsystem.entity.InspectionRecord;

import java.util.List;

public interface InspectionRecordService {
    void add(InspectionRecordRequest request);
    void update(InspectionRecordRequest request);
    void delete(Long id);
    InspectionRecord getById(Long id);
    Page<InspectionRecord> page(int pageNum, int pageSize, Long equipmentId, Integer result);
    List<InspectionRecord> getByEquipmentId(Long equipmentId);
}
```

- [ ] **Step 9: 创建设备分类Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.EquipmentCategoryRequest;
import com.lsy.propertymanagementsystem.entity.EquipmentCategory;
import com.lsy.propertymanagementsystem.mapper.EquipmentCategoryMapper;
import com.lsy.propertymanagementsystem.service.EquipmentCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentCategoryServiceImpl implements EquipmentCategoryService {

    @Autowired
    private EquipmentCategoryMapper equipmentCategoryMapper;

    @Override
    @Transactional
    public void add(EquipmentCategoryRequest request) {
        EquipmentCategory category = new EquipmentCategory();
        BeanUtils.copyProperties(request, category);
        category.setIsDefault(0);
        equipmentCategoryMapper.insert(category);
    }

    @Override
    @Transactional
    public void update(EquipmentCategoryRequest request) {
        EquipmentCategory category = equipmentCategoryMapper.selectById(request.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        if (category.getIsDefault() == 1) {
            throw new BusinessException("预设分类不可修改");
        }
        BeanUtils.copyProperties(request, category);
        equipmentCategoryMapper.updateById(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EquipmentCategory category = equipmentCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        if (category.getIsDefault() == 1) {
            throw new BusinessException("预设分类不可删除");
        }
        equipmentCategoryMapper.deleteById(id);
    }

    @Override
    public EquipmentCategory getById(Long id) {
        return equipmentCategoryMapper.selectById(id);
    }

    @Override
    public List<EquipmentCategory> list() {
        LambdaQueryWrapper<EquipmentCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(EquipmentCategory::getSortOrder);
        return equipmentCategoryMapper.selectList(wrapper);
    }
}
```

- [ ] **Step 10: 创建设备Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.EquipmentRequest;
import com.lsy.propertymanagementsystem.entity.Equipment;
import com.lsy.propertymanagementsystem.mapper.EquipmentMapper;
import com.lsy.propertymanagementsystem.service.EquipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    @Transactional
    public void add(EquipmentRequest request) {
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(request, equipment);
        equipment.setStatus(0);
        equipmentMapper.insert(equipment);
    }

    @Override
    @Transactional
    public void update(EquipmentRequest request) {
        Equipment equipment = equipmentMapper.selectById(request.getId());
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        BeanUtils.copyProperties(request, equipment);
        equipmentMapper.updateById(equipment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        equipmentMapper.deleteById(id);
    }

    @Override
    public Equipment getById(Long id) {
        return equipmentMapper.selectById(id);
    }

    @Override
    public Page<Equipment> page(int pageNum, int pageSize, Long categoryId, Integer status) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Equipment::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Equipment::getStatus, status);
        }
        wrapper.orderByDesc(Equipment::getCreateTime);
        return equipmentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        equipment.setStatus(status);
        equipmentMapper.updateById(equipment);
    }
}
```

- [ ] **Step 11: 创建巡检计划Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.InspectionPlanRequest;
import com.lsy.propertymanagementsystem.entity.InspectionPlan;
import com.lsy.propertymanagementsystem.mapper.InspectionPlanMapper;
import com.lsy.propertymanagementsystem.service.InspectionPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class InspectionPlanServiceImpl implements InspectionPlanService {

    @Autowired
    private InspectionPlanMapper inspectionPlanMapper;

    @Override
    @Transactional
    public void add(InspectionPlanRequest request) {
        InspectionPlan plan = new InspectionPlan();
        BeanUtils.copyProperties(request, plan);
        plan.setStatus(0);
        inspectionPlanMapper.insert(plan);
    }

    @Override
    @Transactional
    public void update(InspectionPlanRequest request) {
        InspectionPlan plan = inspectionPlanMapper.selectById(request.getId());
        if (plan == null) {
            throw new BusinessException("计划不存在");
        }
        BeanUtils.copyProperties(request, plan);
        inspectionPlanMapper.updateById(plan);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        inspectionPlanMapper.deleteById(id);
    }

    @Override
    public InspectionPlan getById(Long id) {
        return inspectionPlanMapper.selectById(id);
    }

    @Override
    public Page<InspectionPlan> page(int pageNum, int pageSize, Integer status) {
        LambdaQueryWrapper<InspectionPlan> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(InspectionPlan::getStatus, status);
        }
        wrapper.orderByDesc(InspectionPlan::getCreateTime);
        return inspectionPlanMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        InspectionPlan plan = inspectionPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("计划不存在");
        }
        plan.setStatus(status);
        inspectionPlanMapper.updateById(plan);
    }

    @Override
    @Transactional
    public void generateByCycle() {
        LambdaQueryWrapper<InspectionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionPlan::getPlanType, 1);
        wrapper.eq(InspectionPlan::getStatus, 0);
        wrapper.le(InspectionPlan::getStartDate, LocalDate.now());
        
        java.util.List<InspectionPlan> plans = inspectionPlanMapper.selectList(wrapper);
        for (InspectionPlan plan : plans) {
            // 简化实现：实际应该根据cycle_type和cycle_value计算下次执行日期
            // 这里只是示例逻辑
            LocalDate nextDate = calculateNextDate(plan);
            if (nextDate != null && !nextDate.isAfter(LocalDate.now())) {
                plan.setStartDate(nextDate);
                inspectionPlanMapper.updateById(plan);
            }
        }
    }

    private LocalDate calculateNextDate(InspectionPlan plan) {
        if (plan.getCycleType() == null || plan.getStartDate() == null) {
            return null;
        }
        switch (plan.getCycleType()) {
            case 0: // 每天
                return plan.getStartDate().plusDays(1);
            case 1: // 每周
                return plan.getStartDate().plusWeeks(1);
            case 2: // 每月
                return plan.getStartDate().plusMonths(1);
            case 3: // 自定义
                return plan.getStartDate().plusDays(plan.getCycleValue() != null ? plan.getCycleValue() : 1);
            default:
                return null;
        }
    }
}
```

- [ ] **Step 12: 创建巡检记录Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.InspectionRecordRequest;
import com.lsy.propertymanagementsystem.entity.InspectionRecord;
import com.lsy.propertymanagementsystem.mapper.InspectionRecordMapper;
import com.lsy.propertymanagementsystem.service.InspectionRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InspectionRecordServiceImpl implements InspectionRecordService {

    @Autowired
    private InspectionRecordMapper inspectionRecordMapper;

    @Override
    @Transactional
    public void add(InspectionRecordRequest request) {
        InspectionRecord record = new InspectionRecord();
        BeanUtils.copyProperties(request, record);
        record.setInspectTime(LocalDateTime.now());
        inspectionRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void update(InspectionRecordRequest request) {
        InspectionRecord record = inspectionRecordMapper.selectById(request.getId());
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        BeanUtils.copyProperties(request, record);
        inspectionRecordMapper.updateById(record);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        inspectionRecordMapper.deleteById(id);
    }

    @Override
    public InspectionRecord getById(Long id) {
        return inspectionRecordMapper.selectById(id);
    }

    @Override
    public Page<InspectionRecord> page(int pageNum, int pageSize, Long equipmentId, Integer result) {
        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<>();
        if (equipmentId != null) {
            wrapper.eq(InspectionRecord::getEquipmentId, equipmentId);
        }
        if (result != null) {
            wrapper.eq(InspectionRecord::getResult, result);
        }
        wrapper.orderByDesc(InspectionRecord::getInspectTime);
        return inspectionRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<InspectionRecord> getByEquipmentId(Long equipmentId) {
        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionRecord::getEquipmentId, equipmentId);
        wrapper.orderByDesc(InspectionRecord::getInspectTime);
        return inspectionRecordMapper.selectList(wrapper);
    }
}
```

- [ ] **Step 13: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/dto/request/EquipmentCategoryRequest.java
git add src/main/java/com/lsy/propertymanagementsystem/dto/request/EquipmentRequest.java
git add src/main/java/com/lsy/propertymanagementsystem/dto/request/InspectionPlanRequest.java
git add src/main/java/com/lsy/propertymanagementsystem/dto/request/InspectionRecordRequest.java
git add src/main/java/com/lsy/propertymanagementsystem/service/EquipmentCategoryService.java
git add src/main/java/com/lsy/propertymanagementsystem/service/EquipmentService.java
git add src/main/java/com/lsy/propertymanagementsystem/service/InspectionPlanService.java
git add src/main/java/com/lsy/propertymanagementsystem/service/InspectionRecordService.java
git add src/main/java/com/lsy/propertymanagementsystem/service/impl/EquipmentCategoryServiceImpl.java
git add src/main/java/com/lsy/propertymanagementsystem/service/impl/EquipmentServiceImpl.java
git add src/main/java/com/lsy/propertymanagementsystem/service/impl/InspectionPlanServiceImpl.java
git add src/main/java/com/lsy/propertymanagementsystem/service/impl/InspectionRecordServiceImpl.java
git commit -m "feat: 创建设备巡检模块DTO和Service"
```

---

## Task 5: 创建后端Controller

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/EquipmentCategoryController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/EquipmentController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/InspectionPlanController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/InspectionRecordController.java`

- [ ] **Step 1: 创建设备分类Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.EquipmentCategoryRequest;
import com.lsy.propertymanagementsystem.entity.EquipmentCategory;
import com.lsy.propertymanagementsystem.service.EquipmentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/equipment/category")
public class EquipmentCategoryController {

    @Autowired
    private EquipmentCategoryService equipmentCategoryService;

    @PostMapping
    public Result add(@Valid @RequestBody EquipmentCategoryRequest request) {
        equipmentCategoryService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody EquipmentCategoryRequest request) {
        equipmentCategoryService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentCategoryService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        EquipmentCategory category = equipmentCategoryService.getById(id);
        return Result.success(category);
    }

    @GetMapping("/list")
    public Result list() {
        List<EquipmentCategory> list = equipmentCategoryService.list();
        return Result.success(list);
    }
}
```

- [ ] **Step 2: 创建设备Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.EquipmentRequest;
import com.lsy.propertymanagementsystem.entity.Equipment;
import com.lsy.propertymanagementsystem.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public Result add(@Valid @RequestBody EquipmentRequest request) {
        equipmentService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody EquipmentRequest request) {
        equipmentService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getById(id);
        return Result.success(equipment);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Integer status) {
        Page<Equipment> page = equipmentService.page(pageNum, pageSize, categoryId, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        equipmentService.updateStatus(id, status);
        return Result.success();
    }
}
```

- [ ] **Step 3: 创建巡检计划Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.InspectionPlanRequest;
import com.lsy.propertymanagementsystem.entity.InspectionPlan;
import com.lsy.propertymanagementsystem.service.InspectionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inspection/plan")
public class InspectionPlanController {

    @Autowired
    private InspectionPlanService inspectionPlanService;

    @PostMapping
    public Result add(@Valid @RequestBody InspectionPlanRequest request) {
        inspectionPlanService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody InspectionPlanRequest request) {
        inspectionPlanService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        inspectionPlanService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        InspectionPlan plan = inspectionPlanService.getById(id);
        return Result.success(plan);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Integer status) {
        Page<InspectionPlan> page = inspectionPlanService.page(pageNum, pageSize, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        inspectionPlanService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/generate")
    public Result generate() {
        inspectionPlanService.generateByCycle();
        return Result.success();
    }
}
```

- [ ] **Step 4: 创建巡检记录Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.InspectionRecordRequest;
import com.lsy.propertymanagementsystem.entity.InspectionRecord;
import com.lsy.propertymanagementsystem.service.InspectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inspection/record")
public class InspectionRecordController {

    @Autowired
    private InspectionRecordService inspectionRecordService;

    @PostMapping
    public Result add(@Valid @RequestBody InspectionRecordRequest request) {
        inspectionRecordService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody InspectionRecordRequest request) {
        inspectionRecordService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        inspectionRecordService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        InspectionRecord record = inspectionRecordService.getById(id);
        return Result.success(record);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long equipmentId,
                       @RequestParam(required = false) Integer result) {
        Page<InspectionRecord> page = inspectionRecordService.page(pageNum, pageSize, equipmentId, result);
        return Result.success(page);
    }

    @GetMapping("/equipment/{id}")
    public Result getByEquipmentId(@PathVariable Long id) {
        List<InspectionRecord> list = inspectionRecordService.getByEquipmentId(id);
        return Result.success(list);
    }
}
```

- [ ] **Step 5: 编译验证**

```bash
mvn compile -DskipTests
```

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/controller/EquipmentCategoryController.java
git add src/main/java/com/lsy/propertymanagementsystem/controller/EquipmentController.java
git add src/main/java/com/lsy/propertymanagementsystem/controller/InspectionPlanController.java
git add src/main/java/com/lsy/propertymanagementsystem/controller/InspectionRecordController.java
git commit -m "feat: 创建设备巡检模块Controller"
```

---

## Task 6: 创建前端API文件

**Files:**
- Create: `frontend/src/api/equipmentCategory.js`
- Create: `frontend/src/api/equipment.js`
- Create: `frontend/src/api/inspectionPlan.js`
- Create: `frontend/src/api/inspectionRecord.js`

- [ ] **Step 1: 创建设备分类API**

```javascript
import request from '../utils/request'

export function addEquipmentCategory(data) {
  return request({ url: '/api/equipment/category', method: 'post', data })
}

export function updateEquipmentCategory(data) {
  return request({ url: '/api/equipment/category', method: 'put', data })
}

export function deleteEquipmentCategory(id) {
  return request({ url: `/api/equipment/category/${id}`, method: 'delete' })
}

export function getEquipmentCategory(id) {
  return request({ url: `/api/equipment/category/${id}`, method: 'get' })
}

export function listEquipmentCategory() {
  return request({ url: '/api/equipment/category/list', method: 'get' })
}
```

- [ ] **Step 2: 创建设备API**

```javascript
import request from '../utils/request'

export function addEquipment(data) {
  return request({ url: '/api/equipment', method: 'post', data })
}

export function updateEquipment(data) {
  return request({ url: '/api/equipment', method: 'put', data })
}

export function deleteEquipment(id) {
  return request({ url: `/api/equipment/${id}`, method: 'delete' })
}

export function getEquipment(id) {
  return request({ url: `/api/equipment/${id}`, method: 'get' })
}

export function pageEquipment(params) {
  return request({ url: '/api/equipment/page', method: 'get', params })
}

export function updateEquipmentStatus(params) {
  return request({ url: '/api/equipment/status', method: 'put', params })
}
```

- [ ] **Step 3: 创建巡检计划API**

```javascript
import request from '../utils/request'

export function addInspectionPlan(data) {
  return request({ url: '/api/inspection/plan', method: 'post', data })
}

export function updateInspectionPlan(data) {
  return request({ url: '/api/inspection/plan', method: 'put', data })
}

export function deleteInspectionPlan(id) {
  return request({ url: `/api/inspection/plan/${id}`, method: 'delete' })
}

export function getInspectionPlan(id) {
  return request({ url: `/api/inspection/plan/${id}`, method: 'get' })
}

export function pageInspectionPlan(params) {
  return request({ url: '/api/inspection/plan/page', method: 'get', params })
}

export function updateInspectionPlanStatus(params) {
  return request({ url: '/api/inspection/plan/status', method: 'put', params })
}

export function generateInspectionPlan() {
  return request({ url: '/api/inspection/plan/generate', method: 'post' })
}
```

- [ ] **Step 4: 创建巡检记录API**

```javascript
import request from '../utils/request'

export function addInspectionRecord(data) {
  return request({ url: '/api/inspection/record', method: 'post', data })
}

export function updateInspectionRecord(data) {
  return request({ url: '/api/inspection/record', method: 'put', data })
}

export function deleteInspectionRecord(id) {
  return request({ url: `/api/inspection/record/${id}`, method: 'delete' })
}

export function getInspectionRecord(id) {
  return request({ url: `/api/inspection/record/${id}`, method: 'get' })
}

export function pageInspectionRecord(params) {
  return request({ url: '/api/inspection/record/page', method: 'get', params })
}

export function getInspectionRecordByEquipment(id) {
  return request({ url: `/api/inspection/record/equipment/${id}`, method: 'get' })
}
```

- [ ] **Step 5: 提交**

```bash
git add frontend/src/api/equipmentCategory.js
git add frontend/src/api/equipment.js
git add frontend/src/api/inspectionPlan.js
git add frontend/src/api/inspectionRecord.js
git commit -m "feat: 创建设备巡检模块前端API"
```

---

## Task 7: 创建前端页面

**Files:**
- Create: `frontend/src/views/equipment/category/index.vue`
- Create: `frontend/src/views/equipment/device/index.vue`
- Create: `frontend/src/views/equipment/inspection/plan/index.vue`
- Create: `frontend/src/views/equipment/inspection/plan/add.vue`
- Create: `frontend/src/views/equipment/inspection/record/index.vue`
- Create: `frontend/src/views/equipment/inspection/record/add.vue`

- [ ] **Step 1: 创建设备分类页面**

```vue
<template>
  <div class="equipment-category">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备分类管理</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="isDefault" label="是否预设" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="warning">预设</el-tag>
            <el-tag v-else type="info">自定义</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.isDefault === 0" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listEquipmentCategory,
  addEquipmentCategory,
  updateEquipmentCategory,
  deleteEquipmentCategory
} from '../../../api/equipmentCategory'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const form = ref({
  categoryName: '',
  description: '',
  sortOrder: 0
})

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await listEquipmentCategory()
  tableData.value = res.data
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增分类'
  form.value = { categoryName: '', description: '', sortOrder: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateEquipmentCategory(form.value)
    ElMessage.success('更新成功')
  } else {
    await addEquipmentCategory(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该分类？', '提示')
  await deleteEquipmentCategory(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.equipment-category { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
```

- [ ] **Step 2: 创建设备管理页面**

```vue
<template>
  <div class="equipment-device">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备管理</span>
          <el-button type="primary" @click="handleAdd">新增设备</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="设备分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option v-for="item in categoryList" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="维修中" :value="1" />
            <el-option label="停用" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentName" label="设备名称" />
        <el-table-column prop="equipmentCode" label="设备编号" />
        <el-table-column prop="categoryId" label="分类" width="120">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maintenanceUser" label="维护人员" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="warning" @click="handleStatusChange(row, 1)">维修</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" @click="handleStatusChange(row, 0)">恢复正常</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="handleStatusChange(row, 2)">停用</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
        :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备名称" prop="equipmentName">
          <el-input v-model="form.equipmentName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备编号" prop="equipmentCode">
          <el-input v-model="form.equipmentCode" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="安装位置">
          <el-input v-model="form.location" placeholder="请输入安装位置" />
        </el-form-item>
        <el-form-item label="维护人员">
          <el-input v-model="form.maintenanceUser" placeholder="请输入维护人员" />
        </el-form-item>
        <el-form-item label="安装日期">
          <el-date-picker v-model="form.installDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="保修到期">
          <el-date-picker v-model="form.warrantyDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="3" placeholder="请输入备注" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listEquipmentCategory } from '../../../api/equipmentCategory'
import {
  pageEquipment,
  addEquipment,
  updateEquipment,
  deleteEquipment,
  updateEquipmentStatus
} from '../../../api/equipment'

const tableData = ref([])
const categoryList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const searchForm = ref({ categoryId: null, status: null })

const form = ref({
  equipmentName: '',
  equipmentCode: '',
  categoryId: null,
  location: '',
  maintenanceUser: '',
  installDate: null,
  warrantyDate: null,
  remark: ''
})

const rules = {
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  equipmentCode: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const getCategoryName = (id) => {
  const category = categoryList.value.find(item => item.id === id)
  return category ? category.categoryName : ''
}

const getStatusType = (s) => ({ 0: 'success', 1: 'warning', 2: 'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0: '正常', 1: '维修中', 2: '停用' }[s] || '未知')

const loadCategories = async () => {
  const res = await listEquipmentCategory()
  categoryList.value = res.data
}

const loadData = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    categoryId: searchForm.value.categoryId,
    status: searchForm.value.status
  }
  const res = await pageEquipment(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => { currentPage.value = 1; loadData() }
const handleReset = () => { searchForm.value = { categoryId: null, status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; loadData() }
const handleCurrentChange = () => loadData()

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增设备'
  form.value = { equipmentName: '', equipmentCode: '', categoryId: null, location: '', maintenanceUser: '', installDate: null, warrantyDate: null, remark: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑设备'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateEquipment(form.value)
    ElMessage.success('更新成功')
  } else {
    await addEquipment(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleStatusChange = async (row, status) => {
  const text = getStatusText(status)
  await ElMessageBox.confirm(`确认将设备状态更改为"${text}"？`, '提示')
  await updateEquipmentStatus({ id: row.id, status })
  ElMessage.success('状态更新成功')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该设备？', '提示')
  await deleteEquipment(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.equipment-device { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
```

- [ ] **Step 3: 创建巡检计划列表页面**

```vue
<template>
  <div class="inspection-plan">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>巡检计划管理</span>
          <el-button type="primary" @click="handleAdd">新增计划</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待执行" :value="0" />
            <el-option label="执行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="planName" label="计划名称" />
        <el-table-column prop="planType" label="类型" width="100">
          <template #default="{ row }">
            {{ row.planType === 0 ? '手动创建' : '周期生成' }}
          </template>
        </el-table-column>
        <el-table-column prop="planDate" label="计划日期" />
        <el-table-column prop="startDate" label="开始日期" />
        <el-table-column prop="endDate" label="结束日期" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" @click="handleStatusChange(row, 1)">开始执行</el-button>
            <el-button v-if="row.status === 1" size="small" type="warning" @click="handleStatusChange(row, 2)">完成</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="handleStatusChange(row, 3)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
        :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageInspectionPlan, updateInspectionPlanStatus } from '../../../../api/inspectionPlan'

const router = useRouter()
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({ status: null })

const getStatusType = (s) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0: '待执行', 1: '执行中', 2: '已完成', 3: '已取消' }[s] || '未知')

const loadData = async () => {
  const params = { pageNum: currentPage.value, pageSize: pageSize.value, status: searchForm.value.status }
  const res = await pageInspectionPlan(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => { currentPage.value = 1; loadData() }
const handleReset = () => { searchForm.value = { status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; loadData() }
const handleCurrentChange = () => loadData()

const handleAdd = () => router.push('/inspection/plan/add')
const handleDetail = (row) => router.push(`/inspection/plan/add?id=${row.id}`)
const handleEdit = (row) => router.push(`/inspection/plan/add?id=${row.id}`)

const handleStatusChange = async (row, status) => {
  const text = getStatusText(status)
  await ElMessageBox.confirm(`确认将计划状态更改为"${text}"？`, '提示')
  await updateInspectionPlanStatus({ id: row.id, status })
  ElMessage.success('状态更新成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.inspection-plan { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
```

- [ ] **Step 4: 创建巡检计划新增/编辑页面**

```vue
<template>
  <div class="inspection-plan-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑计划' : '新增计划' }}</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="计划类型" prop="planType">
          <el-radio-group v-model="form.planType">
            <el-radio :value="0">手动创建</el-radio>
            <el-radio :value="1">周期生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.planType === 0" label="计划日期" prop="planDate">
          <el-date-picker v-model="form.planDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item v-if="form.planType === 1" label="周期类型" prop="cycleType">
          <el-select v-model="form.cycleType" placeholder="请选择周期">
            <el-option label="每天" :value="0" />
            <el-option label="每周" :value="1" />
            <el-option label="每月" :value="2" />
            <el-option label="自定义" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.planType === 1 && form.cycleType === 3" label="周期值">
          <el-input-number v-model="form.cycleValue" :min="1" />
          <span style="margin-left: 10px">天</span>
        </el-form-item>
        <el-form-item v-if="form.planType === 1" label="开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item v-if="form.planType === 1" label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="选择设备" prop="equipmentIds">
          <el-select v-model="selectedEquipmentIds" multiple placeholder="请选择设备" style="width: 100%">
            <el-option v-for="item in equipmentList" :key="item.id" :label="item.equipmentName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检人员" prop="inspectorIds">
          <el-select v-model="selectedInspectorIds" multiple placeholder="请选择巡检人员" style="width: 100%">
            <el-option v-for="item in inspectorList" :key="item.id" :label="item.realName || item.username" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { pageEquipment } from '../../../../api/equipment'
import { addInspectionPlan, updateInspectionPlan, getInspectionPlan } from '../../../../api/inspectionPlan'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const isEdit = ref(false)

const equipmentList = ref([])
const inspectorList = ref([])
const selectedEquipmentIds = ref([])
const selectedInspectorIds = ref([])

const form = ref({
  planName: '',
  planType: 0,
  cycleType: null,
  cycleValue: null,
  planDate: null,
  startDate: null,
  endDate: null,
  equipmentIds: '',
  inspectorIds: ''
})

const rules = {
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  planType: [{ required: true, message: '请选择计划类型', trigger: 'change' }]
}

const handleCancel = () => router.back()

const handleSubmit = async () => {
  await formRef.value.validate()
  form.value.equipmentIds = selectedEquipmentIds.value.join(',')
  form.value.inspectorIds = selectedInspectorIds.value.join(',')
  
  if (isEdit.value) {
    await updateInspectionPlan({ id: route.query.id, ...form.value })
    ElMessage.success('更新成功')
  } else {
    await addInspectionPlan(form.value)
    ElMessage.success('新增成功')
  }
  router.back()
}

const loadEquipment = async () => {
  const res = await pageEquipment({ pageNum: 1, pageSize: 1000 })
  equipmentList.value = res.data.records
}

const loadInspectors = async () => {
  // TODO: 从用户列表中筛选维护人员角色的用户
  // 这里简化处理，实际应该调用用户列表接口并筛选
  inspectorList.value = []
}

onMounted(async () => {
  await loadEquipment()
  await loadInspectors()
  
  if (route.query.id) {
    isEdit.value = true
    const res = await getInspectionPlan(route.query.id)
    form.value = res.data
    selectedEquipmentIds.value = res.data.equipmentIds ? res.data.equipmentIds.split(',').map(Number) : []
    selectedInspectorIds.value = res.data.inspectorIds ? res.data.inspectorIds.split(',').map(Number) : []
  }
})
</script>

<style scoped>
.inspection-plan-add { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
```

- [ ] **Step 5: 创建巡检记录列表页面**

```vue
<template>
  <div class="inspection-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>巡检记录管理</span>
          <el-button type="primary" @click="handleAdd">新增记录</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="设备">
          <el-select v-model="searchForm.equipmentId" placeholder="请选择设备" clearable>
            <el-option v-for="item in equipmentList" :key="item.id" :label="item.equipmentName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检结果">
          <el-select v-model="searchForm.result" placeholder="请选择结果" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="一般异常" :value="1" />
            <el-option label="严重异常" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentId" label="设备" width="150">
          <template #default="{ row }">
            {{ getEquipmentName(row.equipmentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="inspectorId" label="巡检人员" width="120" />
        <el-table-column prop="inspectTime" label="巡检时间" />
        <el-table-column prop="result" label="巡检结果" width="120">
          <template #default="{ row }">
            <el-tag :type="getResultType(row.result)">{{ getResultText(row.result) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="faultDesc" label="异常描述" show-overflow-tooltip />
        <el-table-column prop="budget" label="预估费用" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
        :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageEquipment } from '../../../../api/equipment'
import { pageInspectionRecord, deleteInspectionRecord } from '../../../../api/inspectionRecord'

const router = useRouter()
const tableData = ref([])
const equipmentList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({ equipmentId: null, result: null })

const getEquipmentName = (id) => {
  const equipment = equipmentList.value.find(item => item.id === id)
  return equipment ? equipment.equipmentName : ''
}

const getResultType = (r) => ({ 0: 'success', 1: 'warning', 2: 'danger' }[r] || 'info')
const getResultText = (r) => ({ 0: '正常', 1: '一般异常', 2: '严重异常' }[r] || '未知')

const loadEquipment = async () => {
  const res = await pageEquipment({ pageNum: 1, pageSize: 1000 })
  equipmentList.value = res.data.records
}

const loadData = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    equipmentId: searchForm.value.equipmentId,
    result: searchForm.value.result
  }
  const res = await pageInspectionRecord(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => { currentPage.value = 1; loadData() }
const handleReset = () => { searchForm.value = { equipmentId: null, result: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; loadData() }
const handleCurrentChange = () => loadData()

const handleAdd = () => router.push('/inspection/record/add')
const handleDetail = (row) => router.push(`/inspection/record/add?id=${row.id}`)
const handleEdit = (row) => router.push(`/inspection/record/add?id=${row.id}`)

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该记录？', '提示')
  await deleteInspectionRecord(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadEquipment()
  loadData()
})
</script>

<style scoped>
.inspection-record { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
```

- [ ] **Step 6: 创建巡检记录新增页面**

```vue
<template>
  <div class="inspection-record-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑记录' : '新增记录' }}</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="关联计划">
          <el-select v-model="form.planId" placeholder="请选择计划（可选）" clearable>
            <el-option v-for="item in planList" :key="item.id" :label="item.planName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择设备" prop="equipmentId">
          <el-select v-model="form.equipmentId" placeholder="请选择设备">
            <el-option v-for="item in equipmentList" :key="item.id" :label="item.equipmentName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检结果" prop="result">
          <el-radio-group v-model="form.result">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">一般异常</el-radio>
            <el-radio :value="2">严重异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="异常描述">
          <el-input v-model="form.faultDesc" type="textarea" rows="3" placeholder="请输入异常描述" />
        </el-form-item>
        <el-form-item label="维修建议">
          <el-input v-model="form.repairSuggestion" type="textarea" rows="3" placeholder="请输入维修建议" />
        </el-form-item>
        <el-form-item label="预估费用">
          <el-input-number v-model="form.budget" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="预估工时">
          <el-input v-model="form.duration" placeholder="如：2小时、1天" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { pageEquipment } from '../../../../api/equipment'
import { pageInspectionPlan } from '../../../../api/inspectionPlan'
import {
  addInspectionRecord,
  updateInspectionRecord,
  getInspectionRecord
} from '../../../../api/inspectionRecord'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const isEdit = ref(false)

const equipmentList = ref([])
const planList = ref([])

const form = ref({
  planId: null,
  equipmentId: null,
  result: null,
  faultDesc: '',
  repairSuggestion: '',
  budget: null,
  duration: ''
})

const rules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  result: [{ required: true, message: '请选择巡检结果', trigger: 'change' }]
}

const handleCancel = () => router.back()

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateInspectionRecord({ id: route.query.id, ...form.value })
    ElMessage.success('更新成功')
  } else {
    await addInspectionRecord(form.value)
    ElMessage.success('新增成功')
  }
  router.back()
}

const loadEquipment = async () => {
  const res = await pageEquipment({ pageNum: 1, pageSize: 1000 })
  equipmentList.value = res.data.records
}

const loadPlans = async () => {
  const res = await pageInspectionPlan({ pageNum: 1, pageSize: 1000 })
  planList.value = res.data.records
}

onMounted(async () => {
  await loadEquipment()
  await loadPlans()
  
  if (route.query.id) {
    isEdit.value = true
    const res = await getInspectionRecord(route.query.id)
    form.value = res.data
  }
})
</script>

<style scoped>
.inspection-record-add { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
```

- [ ] **Step 7: 提交**

```bash
git add frontend/src/views/equipment/category/index.vue
git add frontend/src/views/equipment/device/index.vue
git add frontend/src/views/equipment/inspection/plan/index.vue
git add frontend/src/views/equipment/inspection/plan/add.vue
git add frontend/src/views/equipment/inspection/record/index.vue
git add frontend/src/views/equipment/inspection/record/add.vue
git commit -m "feat: 创建设备巡检模块前端页面"
```

---

## Task 8: 更新前端路由

**Files:**
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: 添加路由配置**

在 `frontend/src/router/index.js` 中的 children 数组中添加以下路由：

```javascript
{
  path: 'equipment/category',
  name: 'EquipmentCategory',
  component: () => import('../views/equipment/category/index.vue'),
  meta: { title: '设备分类', parent: '设备巡检' }
},
{
  path: 'equipment/device',
  name: 'Equipment',
  component: () => import('../views/equipment/device/index.vue'),
  meta: { title: '设备管理', parent: '设备巡检' }
},
{
  path: 'inspection/plan',
  name: 'InspectionPlan',
  component: () => import('../views/equipment/inspection/plan/index.vue'),
  meta: { title: '巡检计划', parent: '设备巡检' }
},
{
  path: 'inspection/plan/add',
  name: 'InspectionPlanAdd',
  component: () => import('../views/equipment/inspection/plan/add.vue'),
  meta: { title: '新增计划', parent: '巡检计划' }
},
{
  path: 'inspection/record',
  name: 'InspectionRecord',
  component: () => import('../views/equipment/inspection/record/index.vue'),
  meta: { title: '巡检记录', parent: '设备巡检' }
},
{
  path: 'inspection/record/add',
  name: 'InspectionRecordAdd',
  component: () => import('../views/equipment/inspection/record/add.vue'),
  meta: { title: '新增记录', parent: '巡检记录' }
}
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/router/index.js
git commit -m "feat: 添加设备巡检模块路由配置"
```

---

## Task 9: 测试验证

**Files:**
- None (验证步骤)

- [ ] **Step 1: 后端编译验证**

```bash
mvn compile -DskipTests
```

- [ ] **Step 2: 前端构建验证**

```bash
cd frontend
npm run build
```

- [ ] **Step 3: 最终提交**

```bash
git add -A
git commit -m "feat: 完成设备巡检模块开发"
```

---

**计划完成。** 请选择执行方式：

**1. Subagent-Driven（推荐）** - 每个任务分派独立子代理，任务间进行审查

**2. Inline Execution** - 在当前会话中执行任务，批量执行带检查点

**选择哪种方式？**
