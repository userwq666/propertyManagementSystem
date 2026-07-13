# 收费管理模块实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现物业管理系统的收费管理模块，包括收费项目配置、账单管理、缴费记录、欠费统计功能

**Architecture:** 采用SpringBoot+MyBatisPlus+MySQL后端架构，Vue3+Element Plus前端架构，前后端分离设计。使用jakarta.validation进行参数校验（兼容Spring Boot 4.x）。

**Tech Stack:** SpringBoot 4.0.8-SNAPSHOT、MyBatis-Plus、MySQL 8.0、JWT、Jakarta Validation、Vue3、Vite、Element Plus

---

## 文件结构规划

### 后端文件结构
```
src/main/java/com/lsy/propertymanagementsystem/
├── entity/
│   ├── FeeItem.java              # 收费项目实体
│   └── FeeRecord.java            # 缴费账单记录实体
├── mapper/
│   ├── FeeItemMapper.java        # 收费项目Mapper
│   └── FeeRecordMapper.java      # 缴费账单记录Mapper
├── dto/
│   └── request/
│       ├── FeeItemRequest.java   # 收费项目请求
│       └── FeeRecordRequest.java # 缴费账单记录请求
├── service/
│   ├── FeeItemService.java       # 收费项目Service接口
│   ├── impl/
│   │   └── FeeItemServiceImpl.java # 收费项目Service实现
│   ├── FeeRecordService.java     # 缴费账单记录Service接口
│   └── impl/
│       └── FeeRecordServiceImpl.java # 缴费账单记录Service实现
└── controller/
    ├── FeeItemController.java    # 收费项目Controller
    └── FeeRecordController.java  # 缴费账单记录Controller
```

### 前端文件结构
```
frontend/
├── src/
│   ├── api/
│   │   ├── feeItem.js            # 收费项目API
│   │   └── feeRecord.js          # 缴费账单记录API
│   └── views/
│       └── fee/
│           ├── item/
│           │   └── index.vue     # 收费项目管理
│           ├── record/
│           │   └── index.vue     # 账单管理
│           ├── payment/
│           │   └── index.vue     # 缴费记录
│           └── arrears/
│               └── index.vue     # 欠费统计
├── src/router/index.js           # 路由配置（需更新）
```

---

## Task 1: 创建数据库表结构

**Files:**
- Create: `sql/fee.sql`

- [ ] **Step 1: 创建收费管理数据库脚本**

```sql
-- 收费管理模块表结构

-- 创建收费项目表
CREATE TABLE fee_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(100) NOT NULL COMMENT '收费项目名称',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    cycle_type TINYINT NOT NULL COMMENT '收费周期：1月 2季 3年',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0禁用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '收费项目表';

-- 创建缴费账单记录表
CREATE TABLE fee_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL COMMENT '业主id',
    house_id BIGINT NOT NULL COMMENT '房屋id',
    item_id BIGINT NOT NULL COMMENT '收费项目id',
    total_money DECIMAL(10,2) NOT NULL COMMENT '应付总金额',
    bill_cycle VARCHAR(20) NOT NULL COMMENT '账单所属周期（如：2024-01）',
    pay_status TINYINT NOT NULL DEFAULT 0 COMMENT '缴费状态：0未缴费 1已缴费 2欠费',
    pay_time DATETIME COMMENT '实际缴费时间',
    pay_way VARCHAR(50) COMMENT '支付方式',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '缴费账单记录表';
```

- [ ] **Step 2: 提交数据库脚本**

```bash
git add sql/fee.sql
git commit -m "feat: 创建收费管理模块数据库表结构"
```

---

## Task 2: 创建后端实体类和Mapper

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/FeeItem.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/FeeRecord.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/FeeItemMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/FeeRecordMapper.java`

- [ ] **Step 1: 创建FeeItem实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_item")
public class FeeItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String itemName;
    private BigDecimal price;
    private Integer cycleType;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 2: 创建FeeRecord实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_record")
public class FeeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private Long houseId;
    private Long itemId;
    private BigDecimal totalMoney;
    private String billCycle;
    private Integer payStatus;
    private LocalDateTime payTime;
    private String payWay;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 3: 创建FeeItemMapper接口**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.FeeItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeeItemMapper extends BaseMapper<FeeItem> {
}
```

- [ ] **Step 4: 创建FeeRecordMapper接口**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.FeeRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeeRecordMapper extends BaseMapper<FeeRecord> {
}
```

- [ ] **Step 5: 编译验证**

```bash
mvn compile -DskipTests
```

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/ src/main/java/com/lsy/propertymanagementsystem/mapper/
git commit -m "feat: 创建收费管理模块实体类和Mapper"
```

---

## Task 3: 创建后端DTO和Service

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/FeeItemRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/FeeRecordRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/FeeItemService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/FeeItemServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/FeeRecordService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/FeeRecordServiceImpl.java`

- [ ] **Step 1: 创建FeeItemRequest DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeeItemRequest {
    private Long id;

    @NotBlank(message = "收费项目名称不能为空")
    private String itemName;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal price;

    @NotNull(message = "收费周期不能为空")
    private Integer cycleType;

    private Integer status;
    private String remark;
}
```

- [ ] **Step 2: 创建FeeRecordRequest DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FeeRecordRequest {
    private Long id;

    @NotNull(message = "业主id不能为空")
    private Long ownerId;

    @NotNull(message = "房屋id不能为空")
    private Long houseId;

    @NotNull(message = "收费项目id不能为空")
    private Long itemId;

    @NotNull(message = "应付总金额不能为空")
    @DecimalMin(value = "0.01", message = "应付总金额必须大于0")
    private BigDecimal totalMoney;

    @NotBlank(message = "账单所属周期不能为空")
    private String billCycle;

    private Integer payStatus;
    private LocalDateTime payTime;
    private String payWay;
}
```

- [ ] **Step 3: 创建FeeItemService接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.FeeItemRequest;
import com.lsy.propertymanagementsystem.entity.FeeItem;
import java.util.List;

public interface FeeItemService {
    void add(FeeItemRequest request);
    void update(FeeItemRequest request);
    void delete(Long id);
    FeeItem getById(Long id);
    List<FeeItem> list();
    Page<FeeItem> page(int pageNum, int pageSize);
    void updateStatus(Long id, Integer status);
}
```

- [ ] **Step 4: 创建FeeItemServiceImpl实现类**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.FeeItemRequest;
import com.lsy.propertymanagementsystem.entity.FeeItem;
import com.lsy.propertymanagementsystem.mapper.FeeItemMapper;
import com.lsy.propertymanagementsystem.service.FeeItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeeItemServiceImpl implements FeeItemService {

    @Autowired
    private FeeItemMapper feeItemMapper;

    @Override
    public void add(FeeItemRequest request) {
        FeeItem feeItem = new FeeItem();
        BeanUtils.copyProperties(request, feeItem);
        feeItemMapper.insert(feeItem);
    }

    @Override
    public void update(FeeItemRequest request) {
        FeeItem feeItem = feeItemMapper.selectById(request.getId());
        if (feeItem == null) {
            throw new BusinessException("收费项目不存在");
        }
        BeanUtils.copyProperties(request, feeItem);
        feeItemMapper.updateById(feeItem);
    }

    @Override
    public void delete(Long id) {
        feeItemMapper.deleteById(id);
    }

    @Override
    public FeeItem getById(Long id) {
        return feeItemMapper.selectById(id);
    }

    @Override
    public List<FeeItem> list() {
        return feeItemMapper.selectList(null);
    }

    @Override
    public Page<FeeItem> page(int pageNum, int pageSize) {
        return feeItemMapper.selectPage(new Page<>(pageNum, pageSize), null);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        FeeItem feeItem = feeItemMapper.selectById(id);
        if (feeItem == null) {
            throw new BusinessException("收费项目不存在");
        }
        feeItem.setStatus(status);
        feeItemMapper.updateById(feeItem);
    }
}
```

- [ ] **Step 5: 创建FeeRecordService接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.FeeRecordRequest;
import com.lsy.propertymanagementsystem.entity.FeeRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FeeRecordService {
    void generateBills(List<FeeRecordRequest> requests);
    FeeRecord getById(Long id);
    Page<FeeRecord> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer payStatus);
    void confirmPay(Long id, String payWay);
    Map<String, Object> getStatistics(Long ownerId, Long houseId);
}
```

- [ ] **Step 6: 创建FeeRecordServiceImpl实现类**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.FeeRecordRequest;
import com.lsy.propertymanagementsystem.entity.FeeRecord;
import com.lsy.propertymanagementsystem.mapper.FeeRecordMapper;
import com.lsy.propertymanagementsystem.service.FeeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeRecordServiceImpl implements FeeRecordService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Override
    public void generateBills(List<FeeRecordRequest> requests) {
        for (FeeRecordRequest request : requests) {
            FeeRecord feeRecord = new FeeRecord();
            BeanUtils.copyProperties(request, feeRecord);
            feeRecord.setPayStatus(0);
            feeRecordMapper.insert(feeRecord);
        }
    }

    @Override
    public FeeRecord getById(Long id) {
        return feeRecordMapper.selectById(id);
    }

    @Override
    public Page<FeeRecord> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer payStatus) {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(FeeRecord::getOwnerId, ownerId);
        }
        if (houseId != null) {
            wrapper.eq(FeeRecord::getHouseId, houseId);
        }
        if (payStatus != null) {
            wrapper.eq(FeeRecord::getPayStatus, payStatus);
        }
        wrapper.orderByDesc(FeeRecord::getCreateTime);
        return feeRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void confirmPay(Long id, String payWay) {
        FeeRecord feeRecord = feeRecordMapper.selectById(id);
        if (feeRecord == null) {
            throw new BusinessException("账单不存在");
        }
        if (feeRecord.getPayStatus() == 1) {
            throw new BusinessException("账单已缴费");
        }
        feeRecord.setPayStatus(1);
        feeRecord.setPayTime(LocalDateTime.now());
        feeRecord.setPayWay(payWay);
        feeRecordMapper.updateById(feeRecord);
    }

    @Override
    public Map<String, Object> getStatistics(Long ownerId, Long houseId) {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeRecord::getPayStatus, 2);
        if (ownerId != null) {
            wrapper.eq(FeeRecord::getOwnerId, ownerId);
        }
        if (houseId != null) {
            wrapper.eq(FeeRecord::getHouseId, houseId);
        }
        List<FeeRecord> arrearsList = feeRecordMapper.selectList(wrapper);
        
        BigDecimal totalArrears = arrearsList.stream()
                .map(FeeRecord::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> result = new HashMap<>();
        result.put("arrearsList", arrearsList);
        result.put("totalArrears", totalArrears);
        result.put("count", arrearsList.size());
        return result;
    }
}
```

- [ ] **Step 7: 编译验证**

```bash
mvn compile -DskipTests
```

- [ ] **Step 8: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/dto/ src/main/java/com/lsy/propertymanagementsystem/service/
git commit -m "feat: 创建收费管理模块DTO和Service"
```

---

## Task 4: 创建后端Controller

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/FeeItemController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/FeeRecordController.java`

- [ ] **Step 1: 创建FeeItemController**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.Result;
import com.lsy.propertymanagementsystem.dto.request.FeeItemRequest;
import com.lsy.propertymanagementsystem.entity.FeeItem;
import com.lsy.propertymanagementsystem.service.FeeItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fee/item")
public class FeeItemController {

    @Autowired
    private FeeItemService feeItemService;

    @PostMapping
    public Result add(@Valid @RequestBody FeeItemRequest request) {
        feeItemService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody FeeItemRequest request) {
        feeItemService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        feeItemService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        FeeItem feeItem = feeItemService.getById(id);
        return Result.success(feeItem);
    }

    @GetMapping("/list")
    public Result list() {
        List<FeeItem> list = feeItemService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize) {
        Page<FeeItem> page = feeItemService.page(pageNum, pageSize);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        feeItemService.updateStatus(id, status);
        return Result.success();
    }
}
```

- [ ] **Step 2: 创建FeeRecordController**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.Result;
import com.lsy.propertymanagementsystem.dto.request.FeeRecordRequest;
import com.lsy.propertymanagementsystem.entity.FeeRecord;
import com.lsy.propertymanagementsystem.service.FeeRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee/record")
public class FeeRecordController {

    @Autowired
    private FeeRecordService feeRecordService;

    @PostMapping("/generate")
    public Result generateBills(@Valid @RequestBody List<FeeRecordRequest> requests) {
        feeRecordService.generateBills(requests);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        FeeRecord feeRecord = feeRecordService.getById(id);
        return Result.success(feeRecord);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) Long houseId,
                       @RequestParam(required = false) Integer payStatus) {
        Page<FeeRecord> page = feeRecordService.page(pageNum, pageSize, ownerId, houseId, payStatus);
        return Result.success(page);
    }

    @PutMapping("/pay")
    public Result confirmPay(@RequestParam Long id, @RequestParam String payWay) {
        feeRecordService.confirmPay(id, payWay);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam(required = false) Long ownerId,
                                @RequestParam(required = false) Long houseId) {
        Map<String, Object> statistics = feeRecordService.getStatistics(ownerId, houseId);
        return Result.success(statistics);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
mvn compile -DskipTests
```

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/controller/FeeItemController.java src/main/java/com/lsy/propertymanagementsystem/controller/FeeRecordController.java
git commit -m "feat: 创建收费管理模块Controller"
```

---

## Task 5: 创建前端页面

**Files:**
- Create: `frontend/src/api/feeItem.js`
- Create: `frontend/src/api/feeRecord.js`
- Create: `frontend/src/views/fee/item/index.vue`
- Create: `frontend/src/views/fee/record/index.vue`
- Create: `frontend/src/views/fee/payment/index.vue`
- Create: `frontend/src/views/fee/arrears/index.vue`
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: 创建feeItem.js API文件**

```javascript
import request from '@/utils/request'

export function addFeeItem(data) {
  return request({
    url: '/api/fee/item',
    method: 'post',
    data
  })
}

export function updateFeeItem(data) {
  return request({
    url: '/api/fee/item',
    method: 'put',
    data
  })
}

export function deleteFeeItem(id) {
  return request({
    url: `/api/fee/item/${id}`,
    method: 'delete'
  })
}

export function getFeeItem(id) {
  return request({
    url: `/api/fee/item/${id}`,
    method: 'get'
  })
}

export function listFeeItem() {
  return request({
    url: '/api/fee/item/list',
    method: 'get'
  })
}

export function pageFeeItem(params) {
  return request({
    url: '/api/fee/item/page',
    method: 'get',
    params
  })
}

export function updateFeeItemStatus(id, status) {
  return request({
    url: '/api/fee/item/status',
    method: 'put',
    params: { id, status }
  })
}
```

- [ ] **Step 2: 创建feeRecord.js API文件**

```javascript
import request from '@/utils/request'

export function generateBills(data) {
  return request({
    url: '/api/fee/record/generate',
    method: 'post',
    data
  })
}

export function getFeeRecord(id) {
  return request({
    url: `/api/fee/record/${id}`,
    method: 'get'
  })
}

export function pageFeeRecord(params) {
  return request({
    url: '/api/fee/record/page',
    method: 'get',
    params
  })
}

export function confirmPay(id, payWay) {
  return request({
    url: '/api/fee/record/pay',
    method: 'put',
    params: { id, payWay }
  })
}

export function getStatistics(params) {
  return request({
    url: '/api/fee/record/statistics',
    method: 'get',
    params
  })
}
```

- [ ] **Step 3: 创建收费项目管理页面**

```vue
<template>
  <div class="fee-item">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>收费项目管理</span>
          <el-button type="primary" @click="handleAdd">新增收费项目</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="itemName" label="项目名称" />
        <el-table-column prop="price" label="单价" />
        <el-table-column prop="cycleType" label="收费周期">
          <template #default="{ row }">
            {{ row.cycleType === 1 ? '月' : row.cycleType === 2 ? '季' : '年' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="form.itemName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="收费周期" prop="cycleType">
          <el-select v-model="form.cycleType" placeholder="请选择收费周期">
            <el-option label="月" :value="1" />
            <el-option label="季" :value="2" />
            <el-option label="年" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { pageFeeItem, addFeeItem, updateFeeItem, deleteFeeItem, updateFeeItemStatus } from '@/api/feeItem'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = ref({
  itemName: '',
  price: 0,
  cycleType: 1,
  remark: ''
})

const rules = {
  itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  cycleType: [{ required: true, message: '请选择收费周期', trigger: 'change' }]
}

const fetchData = async () => {
  const res = await pageFeeItem({ pageNum: currentPage.value, pageSize: pageSize.value })
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleAdd = () => {
  dialogTitle.value = '新增收费项目'
  form.value = { itemName: '', price: 0, cycleType: 1, remark: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑收费项目'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该收费项目吗？', '提示', { type: 'warning' })
  await deleteFeeItem(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

const handleStatusChange = async (row) => {
  await updateFeeItemStatus(row.id, row.status)
  ElMessage.success('状态更新成功')
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.value.id) {
    await updateFeeItem(form.value)
  } else {
    await addFeeItem(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  fetchData()
}

const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => { fetchData() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.fee-item { padding: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
```

- [ ] **Step 4: 创建账单管理页面**

```vue
<template>
  <div class="fee-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>账单管理</span>
          <el-button type="primary" @click="handleGenerate">批量生成账单</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="账单ID" width="80" />
        <el-table-column prop="ownerId" label="业主ID" />
        <el-table-column prop="houseId" label="房屋ID" />
        <el-table-column prop="totalMoney" label="应付金额" />
        <el-table-column prop="billCycle" label="账单周期" />
        <el-table-column prop="payStatus" label="缴费状态">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 0 ? 'warning' : row.payStatus === 1 ? 'success' : 'danger'">
              {{ row.payStatus === 0 ? '未缴费' : row.payStatus === 1 ? '已缴费' : '欠费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-if="row.payStatus === 0" size="small" type="success" @click="handlePay(row)">确认缴费</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
    
    <el-dialog v-model="generateDialogVisible" title="批量生成账单" width="600px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="收费项目">
          <el-select v-model="generateForm.itemId" placeholder="请选择收费项目">
            <el-option v-for="item in feeItems" :key="item.id" :label="item.itemName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主ID">
          <el-input v-model="generateForm.ownerId" placeholder="请输入业主ID" />
        </el-form-item>
        <el-form-item label="房屋ID">
          <el-input v-model="generateForm.houseId" placeholder="请输入房屋ID" />
        </el-form-item>
        <el-form-item label="账单周期">
          <el-input v-model="generateForm.billCycle" placeholder="请输入账单周期（如：2024-01）" />
        </el-form-item>
        <el-form-item label="应付金额">
          <el-input-number v-model="generateForm.totalMoney" :min="0.01" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGenerateSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pageFeeRecord, generateBills, confirmPay } from '@/api/feeRecord'
import { listFeeItem } from '@/api/feeItem'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const generateDialogVisible = ref(false)
const feeItems = ref([])

const generateForm = ref({
  itemId: null,
  ownerId: '',
  houseId: '',
  billCycle: '',
  totalMoney: 0
})

const fetchData = async () => {
  const res = await pageFeeRecord({ pageNum: currentPage.value, pageSize: pageSize.value })
  tableData.value = res.data.records
  total.value = res.data.total
}

const fetchFeeItems = async () => {
  const res = await listFeeItem()
  feeItems.value = res.data
}

const handleGenerate = () => {
  generateForm.value = { itemId: null, ownerId: '', houseId: '', billCycle: '', totalMoney: 0 }
  generateDialogVisible.value = true
}

const handleGenerateSubmit = async () => {
  await generateBills([generateForm.value])
  ElMessage.success('账单生成成功')
  generateDialogVisible.value = false
  fetchData()
}

const handlePay = async (row) => {
  await confirmPay(row.id, '线下缴费')
  ElMessage.success('缴费成功')
  fetchData()
}

const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => { fetchData() }

onMounted(() => { fetchData(); fetchFeeItems() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.fee-record { padding: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
```

- [ ] **Step 5: 创建缴费记录页面**

```vue
<template>
  <div class="fee-payment">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>缴费记录</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="业主ID">
          <el-input v-model="searchForm.ownerId" placeholder="请输入业主ID" clearable />
        </el-form-item>
        <el-form-item label="房屋ID">
          <el-input v-model="searchForm.houseId" placeholder="请输入房屋ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="账单ID" width="80" />
        <el-table-column prop="ownerId" label="业主ID" />
        <el-table-column prop="houseId" label="房屋ID" />
        <el-table-column prop="totalMoney" label="应付金额" />
        <el-table-column prop="billCycle" label="账单周期" />
        <el-table-column prop="payStatus" label="缴费状态">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 0 ? 'warning' : row.payStatus === 1 ? 'success' : 'danger'">
              {{ row.payStatus === 0 ? '未缴费' : row.payStatus === 1 ? '已缴费' : '欠费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" />
        <el-table-column prop="payWay" label="支付方式" />
      </el-table>
      
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { pageFeeRecord } from '@/api/feeRecord'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  ownerId: '',
  houseId: ''
})

const fetchData = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    payStatus: 1
  }
  if (searchForm.value.ownerId) params.ownerId = searchForm.value.ownerId
  if (searchForm.value.houseId) params.houseId = searchForm.value.houseId
  const res = await pageFeeRecord(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => { currentPage.value = 1; fetchData() }
const handleReset = () => { searchForm.value = { ownerId: '', houseId: '' }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => { fetchData() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.fee-payment { padding: 20px; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
```

- [ ] **Step 6: 创建欠费统计页面**

```vue
<template>
  <div class="fee-arrears">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>欠费统计</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="业主ID">
          <el-input v-model="searchForm.ownerId" placeholder="请输入业主ID" clearable />
        </el-form-item>
        <el-form-item label="房屋ID">
          <el-input v-model="searchForm.houseId" placeholder="请输入房屋ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-row :gutter="20" class="statistics-row">
        <el-col :span="8">
          <el-statistic title="欠费户数" :value="statistics.count" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="欠费总金额" :value="statistics.totalArrears" :precision="2" />
        </el-col>
      </el-row>
      
      <el-table :data="statistics.arrearsList" border style="width: 100%">
        <el-table-column prop="id" label="账单ID" width="80" />
        <el-table-column prop="ownerId" label="业主ID" />
        <el-table-column prop="houseId" label="房屋ID" />
        <el-table-column prop="totalMoney" label="欠费金额" />
        <el-table-column prop="billCycle" label="账单周期" />
        <el-table-column prop="createTime" label="生成时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '@/api/feeRecord'

const statistics = ref({
  count: 0,
  totalArrears: 0,
  arrearsList: []
})

const searchForm = ref({
  ownerId: '',
  houseId: ''
})

const fetchData = async () => {
  const params = {}
  if (searchForm.value.ownerId) params.ownerId = searchForm.value.ownerId
  if (searchForm.value.houseId) params.houseId = searchForm.value.houseId
  const res = await getStatistics(params)
  statistics.value = res.data
}

const handleSearch = () => { fetchData() }
const handleReset = () => { searchForm.value = { ownerId: '', houseId: '' }; fetchData() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.fee-arrears { padding: 20px; }
.search-form { margin-bottom: 20px; }
.statistics-row { margin-bottom: 20px; }
</style>
```

- [ ] **Step 7: 更新路由配置**

在 `frontend/src/router/index.js` 的 layout children 中添加小区管理子路由：

```javascript
{
  path: 'fee',
  name: 'Fee',
  redirect: '/fee/item',
  meta: { title: '收费管理', icon: 'Money' },
  children: [
    {
      path: 'item',
      name: 'FeeItem',
      component: () => import('@/views/fee/item/index.vue'),
      meta: { title: '收费项目' }
    },
    {
      path: 'record',
      name: 'FeeRecord',
      component: () => import('@/views/fee/record/index.vue'),
      meta: { title: '账单管理' }
    },
    {
      path: 'payment',
      name: 'FeePayment',
      component: () => import('@/views/fee/payment/index.vue'),
      meta: { title: '缴费记录' }
    },
    {
      path: 'arrears',
      name: 'FeeArrears',
      component: () => import('@/views/fee/arrears/index.vue'),
      meta: { title: '欠费统计' }
    }
  ]
}
```

- [ ] **Step 8: 提交前端代码**

```bash
git add frontend/
git commit -m "feat: 创建收费管理模块前端页面"
```

---

## Task 6: 测试验证

- [ ] **Step 1: 后端编译验证**

```bash
mvn compile -DskipTests
```

- [ ] **Step 2: 前端构建验证**

```bash
cd frontend
npm install
npm run build
```

- [ ] **Step 3: 启动后端服务测试**

```bash
mvn spring-boot:run
```

- [ ] **Step 4: 启动前端服务测试**

```bash
cd frontend
npm run dev
```

- [ ] **Step 5: 最终提交**

```bash
git add -A
git commit -m "feat: 完成收费管理模块开发"
```
