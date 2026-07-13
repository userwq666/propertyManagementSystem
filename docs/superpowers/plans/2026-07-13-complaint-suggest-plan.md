# 投诉建议模块实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现投诉建议模块，支持业主提交投诉建议，物业人员受理处理，业主评价

**Architecture:** 采用与报修模块一致的架构：单表设计，状态字段控制流程，前后端分离

**Tech Stack:** SpringBoot 4.x + MyBatis-Plus + MySQL + Vue3 + Element Plus

---

## 文件结构

### 后端文件
- Create: `sql/complaint.sql` — 数据库表结构
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/ComplaintSuggest.java` — 实体类
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/ComplaintSuggestMapper.java` — Mapper接口
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/ComplaintSuggestRequest.java` — 请求DTO
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/ComplaintSuggestService.java` — Service接口
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/ComplaintSuggestServiceImpl.java` — Service实现
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/ComplaintSuggestController.java` — Controller

### 前端文件
- Create: `frontend/src/api/complaintSuggest.js` — API接口
- Create: `frontend/src/views/complaint/suggest/index.vue` — 列表页面
- Create: `frontend/src/views/complaint/suggest/add.vue` — 新增页面
- Create: `frontend/src/views/complaint/suggest/detail.vue` — 详情页面
- Modify: `frontend/src/router/index.js` — 添加路由配置

---

## Task 1: 创建数据库表结构

**Files:**
- Create: `sql/complaint.sql`

- [ ] **Step 1: 创建数据库表**

```sql
CREATE TABLE complaint_suggest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL COMMENT '业主ID',
    house_id BIGINT NOT NULL COMMENT '房屋ID',
    type VARCHAR(20) NOT NULL COMMENT '类型：投诉/建议/其他',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容描述',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待受理 1已受理 2处理中 3已完成 4已评价 5已驳回',
    handle_user VARCHAR(50) NULL COMMENT '处理人',
    handle_result VARCHAR(500) NULL COMMENT '处理结果',
    finish_time DATETIME NULL COMMENT '完成时间',
    rating TINYINT NULL COMMENT '评分：1-5',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除'
) COMMENT '投诉建议表';
```

- [ ] **Step 2: 验证表结构**

```bash
mysql -u root -p123456 property_management_system < sql/complaint.sql
```

- [ ] **Step 3: 提交**

```bash
git add sql/complaint.sql
git commit -m "feat: 创建投诉建议模块数据库表结构"
```

---

## Task 2: 创建后端实体类和Mapper

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/ComplaintSuggest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/ComplaintSuggestMapper.java`

- [ ] **Step 1: 创建实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("complaint_suggest")
public class ComplaintSuggest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private Long houseId;
    private String type;
    private String title;
    private String content;
    private Integer status;
    private String handleUser;
    private String handleResult;
    private LocalDateTime finishTime;
    private Integer rating;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 2: 创建Mapper接口**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.ComplaintSuggest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ComplaintSuggestMapper extends BaseMapper<ComplaintSuggest> {
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/ComplaintSuggest.java
git add src/main/java/com/lsy/propertymanagementsystem/mapper/ComplaintSuggestMapper.java
git commit -m "feat: 创建投诉建议模块实体类和Mapper"
```

---

## Task 3: 创建后端DTO和Service

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/ComplaintSuggestRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/ComplaintSuggestService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/ComplaintSuggestServiceImpl.java`

- [ ] **Step 1: 创建请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintSuggestRequest {
    private Long id;

    @NotNull(message = "业主ID不能为空")
    private Long ownerId;

    @NotNull(message = "房屋ID不能为空")
    private Long houseId;

    @NotBlank(message = "类型不能为空")
    private String type;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Integer status;
    private String handleUser;
    private String handleResult;
    private Integer rating;
}
```

- [ ] **Step 2: 创建Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.ComplaintSuggestRequest;
import com.lsy.propertymanagementsystem.entity.ComplaintSuggest;

public interface ComplaintSuggestService {
    void add(ComplaintSuggestRequest request);
    void update(ComplaintSuggestRequest request);
    void delete(Long id);
    ComplaintSuggest getById(Long id);
    Page<ComplaintSuggest> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer status, String type);
    void updateStatus(Long id, Integer status, String handleUser, String handleResult);
    void updateRating(Long id, Integer rating);
}
```

- [ ] **Step 3: 创建Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.ComplaintSuggestRequest;
import com.lsy.propertymanagementsystem.entity.ComplaintSuggest;
import com.lsy.propertymanagementsystem.mapper.ComplaintSuggestMapper;
import com.lsy.propertymanagementsystem.service.ComplaintSuggestService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComplaintSuggestServiceImpl implements ComplaintSuggestService {

    @Autowired
    private ComplaintSuggestMapper complaintSuggestMapper;

    @Override
    @Transactional
    public void add(ComplaintSuggestRequest request) {
        ComplaintSuggest complaintSuggest = new ComplaintSuggest();
        BeanUtils.copyProperties(request, complaintSuggest);
        complaintSuggest.setStatus(0); // 默认状态：待受理
        complaintSuggestMapper.insert(complaintSuggest);
    }

    @Override
    @Transactional
    public void update(ComplaintSuggestRequest request) {
        ComplaintSuggest complaintSuggest = complaintSuggestMapper.selectById(request.getId());
        if (complaintSuggest == null) {
            throw new BusinessException("投诉建议不存在");
        }
        BeanUtils.copyProperties(request, complaintSuggest);
        complaintSuggestMapper.updateById(complaintSuggest);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        complaintSuggestMapper.deleteById(id);
    }

    @Override
    public ComplaintSuggest getById(Long id) {
        return complaintSuggestMapper.selectById(id);
    }

    @Override
    public Page<ComplaintSuggest> page(int pageNum, int pageSize, Long ownerId, Long houseId, Integer status, String type) {
        LambdaQueryWrapper<ComplaintSuggest> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(ComplaintSuggest::getOwnerId, ownerId);
        }
        if (houseId != null) {
            wrapper.eq(ComplaintSuggest::getHouseId, houseId);
        }
        if (status != null) {
            wrapper.eq(ComplaintSuggest::getStatus, status);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(ComplaintSuggest::getType, type);
        }
        wrapper.orderByDesc(ComplaintSuggest::getCreateTime);
        return complaintSuggestMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status, String handleUser, String handleResult) {
        ComplaintSuggest complaintSuggest = complaintSuggestMapper.selectById(id);
        if (complaintSuggest == null) {
            throw new BusinessException("投诉建议不存在");
        }
        complaintSuggest.setStatus(status);
        if (handleUser != null) complaintSuggest.setHandleUser(handleUser);
        if (handleResult != null) complaintSuggest.setHandleResult(handleResult);
        if (status == 3) complaintSuggest.setFinishTime(LocalDateTime.now()); // 完成时自动记录时间
        complaintSuggestMapper.updateById(complaintSuggest);
    }

    @Override
    @Transactional
    public void updateRating(Long id, Integer rating) {
        ComplaintSuggest complaintSuggest = complaintSuggestMapper.selectById(id);
        if (complaintSuggest == null) {
            throw new BusinessException("投诉建议不存在");
        }
        if (complaintSuggest.getStatus() != 3) {
            throw new BusinessException("只能评价已完成的投诉建议");
        }
        complaintSuggest.setRating(rating);
        complaintSuggest.setStatus(4); // 更新状态为已评价
        complaintSuggestMapper.updateById(complaintSuggest);
    }
}
```

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/dto/request/ComplaintSuggestRequest.java
git add src/main/java/com/lsy/propertymanagementsystem/service/ComplaintSuggestService.java
git add src/main/java/com/lsy/propertymanagementsystem/service/impl/ComplaintSuggestServiceImpl.java
git commit -m "feat: 创建投诉建议模块DTO和Service"
```

---

## Task 4: 创建后端Controller

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/ComplaintSuggestController.java`

- [ ] **Step 1: 创建Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.Result;
import com.lsy.propertymanagementsystem.dto.request.ComplaintSuggestRequest;
import com.lsy.propertymanagementsystem.entity.ComplaintSuggest;
import com.lsy.propertymanagementsystem.service.ComplaintSuggestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/complaint/suggest")
public class ComplaintSuggestController {

    @Autowired
    private ComplaintSuggestService complaintSuggestService;

    @PostMapping
    public Result add(@Valid @RequestBody ComplaintSuggestRequest request) {
        complaintSuggestService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody ComplaintSuggestRequest request) {
        complaintSuggestService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        complaintSuggestService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ComplaintSuggest complaintSuggest = complaintSuggestService.getById(id);
        return Result.success(complaintSuggest);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) Long ownerId,
                       @RequestParam(required = false) Long houseId,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(required = false) String type) {
        Page<ComplaintSuggest> page = complaintSuggestService.page(pageNum, pageSize, ownerId, houseId, status, type);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id,
                               @RequestParam Integer status,
                               @RequestParam(required = false) String handleUser,
                               @RequestParam(required = false) String handleResult) {
        complaintSuggestService.updateStatus(id, status, handleUser, handleResult);
        return Result.success();
    }

    @PutMapping("/rating")
    public Result updateRating(@RequestParam Long id, @RequestParam Integer rating) {
        complaintSuggestService.updateRating(id, rating);
        return Result.success();
    }
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile -DskipTests
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/controller/ComplaintSuggestController.java
git commit -m "feat: 创建投诉建议模块Controller"
```

---

## Task 5: 创建前端API

**Files:**
- Create: `frontend/src/api/complaintSuggest.js`

- [ ] **Step 1: 创建API文件**

```javascript
import request from '@/utils/request'

export function addComplaintSuggest(data) {
  return request({ url: '/api/complaint/suggest', method: 'post', data })
}

export function updateComplaintSuggest(data) {
  return request({ url: '/api/complaint/suggest', method: 'put', data })
}

export function deleteComplaintSuggest(id) {
  return request({ url: `/api/complaint/suggest/${id}`, method: 'delete' })
}

export function getComplaintSuggest(id) {
  return request({ url: `/api/complaint/suggest/${id}`, method: 'get' })
}

export function pageComplaintSuggest(params) {
  return request({ url: '/api/complaint/suggest/page', method: 'get', params })
}

export function updateComplaintSuggestStatus(params) {
  return request({ url: '/api/complaint/suggest/status', method: 'put', params })
}

export function updateComplaintSuggestRating(params) {
  return request({ url: '/api/complaint/suggest/rating', method: 'put', params })
}
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/api/complaintSuggest.js
git commit -m "feat: 创建投诉建议模块前端API"
```

---

## Task 6: 创建前端列表页面

**Files:**
- Create: `frontend/src/views/complaint/suggest/index.vue`

- [ ] **Step 1: 创建列表页面**

```vue
<template>
  <div class="complaint-suggest">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投诉建议管理</span>
          <el-button type="primary" @click="handleAdd">新增投诉建议</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="投诉" value="投诉" />
            <el-option label="建议" value="建议" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待受理" :value="0" />
            <el-option label="已受理" :value="1" />
            <el-option label="处理中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已评价" :value="4" />
            <el-option label="已驳回" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handleUser" label="处理人" />
        <el-table-column prop="rating" label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-if="row.rating" v-model="row.rating" disabled />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" size="small" type="warning" @click="handleAccept(row)">受理</el-button>
            <el-button v-if="row.status === 1 || row.status === 2" size="small" type="success" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="handleReject(row)">驳回</el-button>
            <el-button v-if="row.status === 3 && !row.rating" size="small" type="primary" @click="handleRate(row)">评价</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <!-- 完成弹窗 -->
    <el-dialog v-model="completeDialogVisible" title="完成处理" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-input v-model="completeForm.handleResult" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCompleteSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="rateDialogVisible" title="评价" width="400px">
      <el-form :model="rateForm" label-width="100px">
        <el-form-item label="评分">
          <el-rate v-model="rateForm.rating" show-text :texts="['很差','较差','一般','较好','很好']" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRateSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageComplaintSuggest, deleteComplaintSuggest,
  updateComplaintSuggestStatus, updateComplaintSuggestRating
} from '@/api/complaintSuggest'

const router = useRouter()
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const completeDialogVisible = ref(false)
const rateDialogVisible = ref(false)
const currentRow = ref(null)

const searchForm = ref({ type: null, status: null })
const completeForm = ref({ handleResult: '' })
const rateForm = ref({ rating: 5 })

const getStatusType = (s) => ({ 0:'warning', 1:'primary', 2:'', 3:'success', 4:'success', 5:'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0:'待受理', 1:'已受理', 2:'处理中', 3:'已完成', 4:'已评价', 5:'已驳回' }[s] || '未知')

const fetchData = async () => {
  const params = { pageNum: currentPage.value, pageSize: pageSize.value }
  if (searchForm.value.type) params.type = searchForm.value.type
  if (searchForm.value.status !== null) params.status = searchForm.value.status
  const res = await pageComplaintSuggest(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleAdd = () => router.push('/complaint/suggest/add')
const handleDetail = (row) => router.push(`/complaint/suggest/${row.id}`)
const handleSearch = () => { currentPage.value = 1; fetchData() }
const handleReset = () => { searchForm.value = { type: null, status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => fetchData()

const handleAccept = async (row) => {
  await ElMessageBox.confirm('确认受理该投诉建议？', '提示')
  await updateComplaintSuggestStatus({ id: row.id, status: 1 })
  ElMessage.success('受理成功')
  fetchData()
}

const handleComplete = (row) => {
  currentRow.value = row
  completeForm.value.handleResult = ''
  completeDialogVisible.value = true
}

const handleCompleteSubmit = async () => {
  await updateComplaintSuggestStatus({
    id: currentRow.value.id,
    status: 3,
    handleResult: completeForm.value.handleResult
  })
  ElMessage.success('完成成功')
  completeDialogVisible.value = false
  fetchData()
}

const handleReject = async (row) => {
  await ElMessageBox.confirm('确认驳回该投诉建议？', '提示')
  await updateComplaintSuggestStatus({ id: row.id, status: 5 })
  ElMessage.success('驳回成功')
  fetchData()
}

const handleRate = (row) => {
  currentRow.value = row
  rateForm.value.rating = 5
  rateDialogVisible.value = true
}

const handleRateSubmit = async () => {
  await updateComplaintSuggestRating({ id: currentRow.value.id, rating: rateForm.value.rating })
  ElMessage.success('评价成功')
  rateDialogVisible.value = false
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该投诉建议？', '提示')
  await deleteComplaintSuggest(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/complaint/suggest/index.vue
git commit -m "feat: 创建投诉建议模块列表页面"
```

---

## Task 7: 创建前端新增页面

**Files:**
- Create: `frontend/src/views/complaint/suggest/add.vue`

- [ ] **Step 1: 创建新增页面**

```vue
<template>
  <div class="complaint-suggest-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>新增投诉建议</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="业主ID" prop="ownerId">
          <el-input v-model="form.ownerId" placeholder="请输入业主ID" />
        </el-form-item>
        <el-form-item label="房屋ID" prop="houseId">
          <el-input v-model="form.houseId" placeholder="请输入房屋ID" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="投诉" value="投诉" />
            <el-option label="建议" value="建议" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" rows="6" placeholder="请输入内容" />
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addComplaintSuggest } from '@/api/complaintSuggest'

const router = useRouter()
const formRef = ref(null)

const form = ref({
  ownerId: '',
  houseId: '',
  type: '',
  title: '',
  content: ''
})

const rules = {
  ownerId: [{ required: true, message: '请输入业主ID', trigger: 'blur' }],
  houseId: [{ required: true, message: '请输入房屋ID', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const handleCancel = () => router.back()

const handleSubmit = async () => {
  await formRef.value.validate()
  await addComplaintSuggest(form.value)
  ElMessage.success('提交成功')
  router.back()
}
</script>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/complaint/suggest/add.vue
git commit -m "feat: 创建投诉建议模块新增页面"
```

---

## Task 8: 创建前端详情页面

**Files:**
- Create: `frontend/src/views/complaint/suggest/detail.vue`

- [ ] **Step 1: 创建详情页面**

```vue
<template>
  <div class="complaint-suggest-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投诉建议详情</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail.type }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detail.status)">{{ getStatusText(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="业主ID">{{ detail.ownerId }}</el-descriptions-item>
        <el-descriptions-item label="房屋ID">{{ detail.houseId }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detail.handleUser || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detail.finishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ detail.handleResult || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评分" :span="2">
          <el-rate v-if="detail.rating" v-model="detail.rating" disabled />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getComplaintSuggest } from '@/api/complaintSuggest'

const router = useRouter()
const route = useRoute()
const detail = ref({})

const getStatusType = (s) => ({ 0:'warning', 1:'primary', 2:'', 3:'success', 4:'success', 5:'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0:'待受理', 1:'已受理', 2:'处理中', 3:'已完成', 4:'已评价', 5:'已驳回' }[s] || '未知')

const fetchData = async () => {
  const res = await getComplaintSuggest(route.params.id)
  detail.value = res.data
}

const handleBack = () => router.back()

onMounted(() => fetchData())
</script>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/complaint/suggest/detail.vue
git commit -m "feat: 创建投诉建议模块详情页面"
```

---

## Task 9: 更新前端路由

**Files:**
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: 添加路由配置**

在 `frontend/src/router/index.js` 中添加以下路由：

```javascript
{
  path: '/complaint',
  component: Layout,
  name: 'Complaint',
  meta: { title: '投诉建议', icon: 'ChatDotRound' },
  children: [
    {
      path: 'suggest',
      name: 'ComplaintSuggest',
      component: () => import('@/views/complaint/suggest/index.vue'),
      meta: { title: '投诉建议列表' }
    },
    {
      path: 'suggest/add',
      name: 'ComplaintSuggestAdd',
      component: () => import('@/views/complaint/suggest/add.vue'),
      meta: { title: '新增投诉建议' }
    },
    {
      path: 'suggest/:id',
      name: 'ComplaintSuggestDetail',
      component: () => import('@/views/complaint/suggest/detail.vue'),
      meta: { title: '投诉建议详情' }
    }
  ]
}
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/router/index.js
git commit -m "feat: 添加投诉建议模块路由配置"
```

---

## Task 10: 测试验证

**Files:**
- None (验证步骤)

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

- [ ] **Step 3: 最终提交**

```bash
git add -A
git commit -m "feat: 完成投诉建议模块开发"
```
