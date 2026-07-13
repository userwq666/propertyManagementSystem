# 公告管理模块实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现公告管理模块，支持管理员发布公告，业主查看公告列表和详情

**Architecture:** 采用与报修模块一致的架构：单表设计，状态字段控制流程，前后端分离

**Tech Stack:** SpringBoot 4.x + MyBatis-Plus + MySQL + Vue3 + Element Plus

---

## 文件结构

### 后端文件
- Create: `sql/announcement.sql` — 数据库表结构
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/Announcement.java` — 实体类
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/AnnouncementMapper.java` — Mapper接口
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/AnnouncementRequest.java` — 请求DTO
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/AnnouncementService.java` — Service接口
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/AnnouncementServiceImpl.java` — Service实现
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/AnnouncementController.java` — Controller

### 前端文件
- Create: `frontend/src/api/announcement.js` — API接口
- Create: `frontend/src/views/announcement/index.vue` — 列表页面
- Create: `frontend/src/views/announcement/add.vue` — 新增页面
- Create: `frontend/src/views/announcement/detail.vue` — 详情页面
- Modify: `frontend/src/router/index.js` — 添加路由配置

---

## Task 1: 创建数据库表结构

**Files:**
- Create: `sql/announcement.sql`

- [ ] **Step 1: 创建数据库表**

```sql
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    type VARCHAR(20) NOT NULL COMMENT '类型：通知/活动/紧急',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0草稿 1预发布 2已发布 3已过期',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
    publish_time DATETIME NULL COMMENT '定时发布时间',
    expire_time DATETIME NULL COMMENT '过期时间',
    create_user VARCHAR(50) NOT NULL COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除'
) COMMENT '公告表';
```

- [ ] **Step 2: 验证表结构**

```bash
mysql -u root -p123456 property_management_system < sql/announcement.sql
```

- [ ] **Step 3: 提交**

```bash
git add sql/announcement.sql
git commit -m "feat: 创建公告管理模块数据库表结构"
```

---

## Task 2: 创建后端实体类和Mapper

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/Announcement.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/AnnouncementMapper.java`

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
@TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String type;
    private Integer status;
    private Integer isTop;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private String createUser;
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
import com.lsy.propertymanagementsystem.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/Announcement.java
git add src/main/java/com/lsy/propertymanagementsystem/mapper/AnnouncementMapper.java
git commit -m "feat: 创建公告管理模块实体类和Mapper"
```

---

## Task 3: 创建后端DTO和Service

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/AnnouncementRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/AnnouncementService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/AnnouncementServiceImpl.java`

- [ ] **Step 1: 创建请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementRequest {
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "类型不能为空")
    private String type;

    private Integer status;
    private Integer isTop;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
}
```

- [ ] **Step 2: 创建Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.dto.request.AnnouncementRequest;
import com.lsy.propertymanagementsystem.entity.Announcement;

public interface AnnouncementService {
    void add(AnnouncementRequest request);
    void update(AnnouncementRequest request);
    void delete(Long id);
    Announcement getById(Long id);
    Page<Announcement> page(int pageNum, int pageSize, String type, Integer status);
    void updateStatus(Long id, Integer status);
    void updateTop(Long id, Integer isTop);
}
```

- [ ] **Step 3: 创建Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.AnnouncementRequest;
import com.lsy.propertymanagementsystem.entity.Announcement;
import com.lsy.propertymanagementsystem.mapper.AnnouncementMapper;
import com.lsy.propertymanagementsystem.service.AnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    @Transactional
    public void add(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(request, announcement);
        announcement.setStatus(0); // 默认状态：草稿
        announcement.setIsTop(0); // 默认不置顶
        announcementMapper.insert(announcement);
    }

    @Override
    @Transactional
    public void update(AnnouncementRequest request) {
        Announcement announcement = announcementMapper.selectById(request.getId());
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        BeanUtils.copyProperties(request, announcement);
        announcementMapper.updateById(announcement);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        announcementMapper.deleteById(id);
    }

    @Override
    public Announcement getById(Long id) {
        return announcementMapper.selectById(id);
    }

    @Override
    public Page<Announcement> page(int pageNum, int pageSize, String type, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Announcement::getType, type);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getIsTop).orderByDesc(Announcement::getCreateTime);
        return announcementMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setStatus(status);
        announcementMapper.updateById(announcement);
    }

    @Override
    @Transactional
    public void updateTop(Long id, Integer isTop) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setIsTop(isTop);
        announcementMapper.updateById(announcement);
    }
}
```

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/dto/request/AnnouncementRequest.java
git add src/main/java/com/lsy/propertymanagementsystem/service/AnnouncementService.java
git add src/main/java/com/lsy/propertymanagementsystem/service/impl/AnnouncementServiceImpl.java
git commit -m "feat: 创建公告管理模块DTO和Service"
```

---

## Task 4: 创建后端Controller

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/AnnouncementController.java`

- [ ] **Step 1: 创建Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.common.Result;
import com.lsy.propertymanagementsystem.dto.request.AnnouncementRequest;
import com.lsy.propertymanagementsystem.entity.Announcement;
import com.lsy.propertymanagementsystem.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping
    public Result add(@Valid @RequestBody AnnouncementRequest request) {
        announcementService.add(request);
        return Result.success();
    }

    @PutMapping
    public Result update(@Valid @RequestBody AnnouncementRequest request) {
        announcementService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) Integer status) {
        Page<Announcement> page = announcementService.page(pageNum, pageSize, type, status);
        return Result.success(page);
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        announcementService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/top")
    public Result updateTop(@RequestParam Long id, @RequestParam Integer isTop) {
        announcementService.updateTop(id, isTop);
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
git add src/main/java/com/lsy/propertymanagementsystem/controller/AnnouncementController.java
git commit -m "feat: 创建公告管理模块Controller"
```

---

## Task 5: 创建前端API

**Files:**
- Create: `frontend/src/api/announcement.js`

- [ ] **Step 1: 创建API文件**

```javascript
import request from '@/utils/request'

export function addAnnouncement(data) {
  return request({ url: '/api/announcement', method: 'post', data })
}

export function updateAnnouncement(data) {
  return request({ url: '/api/announcement', method: 'put', data })
}

export function deleteAnnouncement(id) {
  return request({ url: `/api/announcement/${id}`, method: 'delete' })
}

export function getAnnouncement(id) {
  return request({ url: `/api/announcement/${id}`, method: 'get' })
}

export function pageAnnouncement(params) {
  return request({ url: '/api/announcement/page', method: 'get', params })
}

export function updateAnnouncementStatus(params) {
  return request({ url: '/api/announcement/status', method: 'put', params })
}

export function updateAnnouncementTop(params) {
  return request({ url: '/api/announcement/top', method: 'put', params })
}
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/api/announcement.js
git commit -m "feat: 创建公告管理模块前端API"
```

---

## Task 6: 创建前端列表页面

**Files:**
- Create: `frontend/src/views/announcement/index.vue`

- [ ] **Step 1: 创建列表页面**

```vue
<template>
  <div class="announcement">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="handleAdd">新增公告</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="通知" value="通知" />
            <el-option label="活动" value="活动" />
            <el-option label="紧急" value="紧急" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="预发布" :value="1" />
            <el-option label="已发布" :value="2" />
            <el-option label="已过期" :value="3" />
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
        <el-table-column prop="isTop" label="置顶" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isTop === 1" type="danger">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 2" size="small" type="warning" @click="handleOffline(row)">下架</el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageAnnouncement, deleteAnnouncement, updateAnnouncementStatus } from '@/api/announcement'

const router = useRouter()
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({ type: null, status: null })

const getStatusType = (s) => ({ 0:'info', 1:'warning', 2:'success', 3:'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0:'草稿', 1:'预发布', 2:'已发布', 3:'已过期' }[s] || '未知')

const fetchData = async () => {
  const params = { pageNum: currentPage.value, pageSize: pageSize.value }
  if (searchForm.value.type) params.type = searchForm.value.type
  if (searchForm.value.status !== null) params.status = searchForm.value.status
  const res = await pageAnnouncement(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleAdd = () => router.push('/announcement/add')
const handleDetail = (row) => router.push(`/announcement/${row.id}`)
const handleEdit = (row) => router.push(`/announcement/add?id=${row.id}`)
const handleSearch = () => { currentPage.value = 1; fetchData() }
const handleReset = () => { searchForm.value = { type: null, status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => fetchData()

const handlePublish = async (row) => {
  await ElMessageBox.confirm('确认发布该公告？', '提示')
  await updateAnnouncementStatus({ id: row.id, status: 2 })
  ElMessage.success('发布成功')
  fetchData()
}

const handleOffline = async (row) => {
  await ElMessageBox.confirm('确认下架该公告？', '提示')
  await updateAnnouncementStatus({ id: row.id, status: 3 })
  ElMessage.success('下架成功')
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该公告？', '提示')
  await deleteAnnouncement(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/announcement/index.vue
git commit -m "feat: 创建公告管理模块列表页面"
```

---

## Task 7: 创建前端新增页面

**Files:**
- Create: `frontend/src/views/announcement/add.vue`

- [ ] **Step 1: 创建新增页面**

```vue
<template>
  <div class="announcement-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑公告' : '新增公告' }}</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="通知" value="通知" />
            <el-option label="活动" value="活动" />
            <el-option label="紧急" value="紧急" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" rows="6" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.publishTime" type="datetime" placeholder="选择发布时间" />
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker v-model="form.expireTime" type="datetime" placeholder="选择过期时间" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
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
import { addAnnouncement, updateAnnouncement, getAnnouncement } from '@/api/announcement'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const isEdit = ref(false)

const form = ref({
  type: '',
  title: '',
  content: '',
  publishTime: null,
  expireTime: null,
  isTop: 0
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const handleCancel = () => router.back()

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateAnnouncement({ id: route.query.id, ...form.value })
    ElMessage.success('更新成功')
  } else {
    await addAnnouncement(form.value)
    ElMessage.success('提交成功')
  }
  router.back()
}

onMounted(async () => {
  if (route.query.id) {
    isEdit.value = true
    const res = await getAnnouncement(route.query.id)
    form.value = res.data
  }
})
</script>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/announcement/add.vue
git commit -m "feat: 创建公告管理模块新增页面"
```

---

## Task 8: 创建前端详情页面

**Files:**
- Create: `frontend/src/views/announcement/detail.vue`

- [ ] **Step 1: 创建详情页面**

```vue
<template>
  <div class="announcement-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告详情</span>
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
        <el-descriptions-item label="置顶">
          <el-tag v-if="detail.isTop === 1" type="danger">置顶</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detail.createUser }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ detail.publishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ detail.expireTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getAnnouncement } from '@/api/announcement'

const router = useRouter()
const route = useRoute()
const detail = ref({})

const getStatusType = (s) => ({ 0:'info', 1:'warning', 2:'success', 3:'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0:'草稿', 1:'预发布', 2:'已发布', 3:'已过期' }[s] || '未知')

const fetchData = async () => {
  const res = await getAnnouncement(route.params.id)
  detail.value = res.data
}

const handleBack = () => router.back()

onMounted(() => fetchData())
</script>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/announcement/detail.vue
git commit -m "feat: 创建公告管理模块详情页面"
```

---

## Task 9: 更新前端路由

**Files:**
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: 添加路由配置**

在 `frontend/src/router/index.js` 中添加以下路由：

```javascript
{
  path: 'announcement',
  name: 'Announcement',
  component: () => import('@/views/announcement/index.vue'),
  meta: { title: '公告管理' }
},
{
  path: 'announcement/add',
  name: 'AnnouncementAdd',
  component: () => import('@/views/announcement/add.vue'),
  meta: { title: '新增公告' }
},
{
  path: 'announcement/:id',
  name: 'AnnouncementDetail',
  component: () => import('@/views/announcement/detail.vue'),
  meta: { title: '公告详情' }
}
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/router/index.js
git commit -m "feat: 添加公告管理模块路由配置"
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
git commit -m "feat: 完成公告管理模块开发"
```
