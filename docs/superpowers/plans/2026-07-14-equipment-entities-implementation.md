# 设备巡检模块实体类实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 创建物业管理系统设备巡检模块的4个实体类

**Architecture:** 使用SpringBoot 4.x + MyBatis-Plus + MySQL架构，创建对应的Java实体类，与数据库表结构映射。

**Tech Stack:** SpringBoot 4.x, MyBatis-Plus, Lombok, MySQL

---

## 文件结构

在开始定义任务之前，列出将创建或修改的文件及其职责：

### 创建的文件

1. **EquipmentCategory.java** - 设备分类实体类
   - 路径: `src/main/java/com/lsy/propertymanagementsystem/entity/EquipmentCategory.java`
   - 职责: 映射`equipment_category`表

2. **Equipment.java** - 设备实体类
   - 路径: `src/main/java/com/lsy/propertymanagementsystem/entity/Equipment.java`
   - 职责: 映射`equipment`表

3. **InspectionPlan.java** - 巡检计划实体类
   - 路径: `src/main/java/com/lsy/propertymanagementsystem/entity/InspectionPlan.java`
   - 职责: 映射`inspection_plan`表

4. **InspectionRecord.java** - 巡检记录实体类
   - 路径: `src/main/java/com/lsy/propertymanagementsystem/entity/InspectionRecord.java`
   - 职责: 映射`inspection_record`表

### 现有文件（参考）

1. **Announcement.java** - 现有实体类示例
   - 路径: `src/main/java/com/lsy/propertymanagementsystem/entity/Announcement.java`
   - 用途: 参考代码风格和注解使用

## 任务分解

### Task 1: 创建设备分类实体类

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/EquipmentCategory.java`

- [ ] **Step 1: 创建EquipmentCategory.java文件**

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

- [ ] **Step 2: 验证文件创建**

Run: `ls -la src/main/java/com/lsy/propertymanagementsystem/entity/EquipmentCategory.java`
Expected: 文件存在且大小正确

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/EquipmentCategory.java
git commit -m "feat: 创建设备分类实体类"
```

### Task 2: 创建设备实体类

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/Equipment.java`

- [ ] **Step 1: 创建Equipment.java文件**

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

- [ ] **Step 2: 验证文件创建**

Run: `ls -la src/main/java/com/lsy/propertymanagementsystem/entity/Equipment.java`
Expected: 文件存在且大小正确

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/Equipment.java
git commit -m "feat: 创建设备实体类"
```

### Task 3: 创建巡检计划实体类

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/InspectionPlan.java`

- [ ] **Step 1: 创建InspectionPlan.java文件**

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

- [ ] **Step 2: 验证文件创建**

Run: `ls -la src/main/java/com/lsy/propertymanagementsystem/entity/InspectionPlan.java`
Expected: 文件存在且大小正确

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/InspectionPlan.java
git commit -m "feat: 创建巡检计划实体类"
```

### Task 4: 创建巡检记录实体类

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/InspectionRecord.java`

- [ ] **Step 1: 创建InspectionRecord.java文件**

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

- [ ] **Step 2: 验证文件创建**

Run: `ls -la src/main/java/com/lsy/propertymanagementsystem/entity/InspectionRecord.java`
Expected: 文件存在且大小正确

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/InspectionRecord.java
git commit -m "feat: 创建巡检记录实体类"
```

### Task 5: 最终验证

**Files:**
- 验证所有创建的文件

- [ ] **Step 1: 列出所有实体类文件**

Run: `ls -la src/main/java/com/lsy/propertymanagementsystem/entity/`
Expected: 包含所有4个新创建的实体类文件

- [ ] **Step 2: 检查编译**

Run: `mvn compile -f pom.xml`
Expected: 编译成功，无错误

- [ ] **Step 3: 最终提交**

```bash
git add -A
git commit -m "feat: 创建设备巡检模块实体类"
```

## 自我审查

**1. 规范覆盖：** 每个实体类都有对应的任务，覆盖所有要求。

**2. 占位符扫描：** 没有TBD、TODO或占位符，所有步骤都有完整代码。

**3. 类型一致性：** 所有实体类使用一致的字段类型和命名约定。

## 执行交接

计划已完成并保存到 `docs\superpowers\plans\2026-07-14-equipment-entities-implementation.md`。

**两种执行选项：**

**1. 子代理驱动（推荐）** - 我为每个任务分派一个新的子代理，任务间进行审查，快速迭代

**2. 内联执行** - 使用executing-plans在当前会话中执行任务，批量执行并设置检查点

**选择哪种方法？**