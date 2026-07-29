# 物业管理系统 · 路由权限审查报告

> 基于当前前端路由配置、后端模块、视图和 API 的全面梳理
> 生成日期：2026-07-16

---

## 一、角色定义

| 角色标识 | 角色名称 | 对应人群 |
|---|---|---|
| `admin` | 系统管理员 | 物业 IT 管理员 |
| `property` | 物业人员 | 客服、管家、维修调度 |
| `finance` | 财务人员 | 物业财务 |
| `owner` | 业主/住户 | 小区业主、租户 |

---

## 二、模块总览

| 模块 | 后端 Controller | 前端路由 | 前端视图 | API | 状态 |
|---|---|---|---|---|---|
| 系统管理 | ✅ | ✅ | ✅ | ✅ | 正常 |
| 房屋/社区管理 | ✅ | ✅ | ✅ | ✅ | 正常 |
| 费用管理 | ✅ | ✅ | ⚠️ | ✅ | 缺路由 |
| 报修管理 | ✅ | ✅ | ✅ | ✅ | 缺角色 |
| 投诉建议 | ✅ | ⚠️ | ✅ | ✅ | 缺路由 |
| 公告通知 | ✅ | ✅ | ✅ | ✅ | 正常 |
| 消息通知 | ✅ | ⚠️ | ✅ | ✅ | 缺路由 |
| 车位管理 | ✅ | ✅ | ✅ | ✅ | 正常 |
| 统计分析 | ✅ | ✅ | ✅ | ✅ | 正常 |
| 设备管理 | ✅ | ✅ | ✅ | ✅ | 正常 |
| **巡检管理** | ✅ | **❌** | ✅ | ✅ | **完全缺失** |
| 个人中心 | — | ✅ | ✅ | — | 正常 |

---

## 三、问题清单

### 问题 1：报修管理缺少 `owner` 角色

**涉及文件：** `frontend/src/router/routes.js` — `/repair` 路由段

**当前配置：**

```js
// 父模块
{ path: '/repair', roles: ['admin', 'property'] }

// 子页面
order    → roles: ['admin', 'property']   // 报修工单
dispatch → roles: ['admin', 'property']   // 派单管理
worker   → roles: ['admin', 'property']   // 维修人员
evaluate → roles: ['admin', 'property']   // 评价管理
```

**影响：** 业主无法提交报修、查看自己的报修记录、评价维修服务

**建议调整：**

| 页面 | 建议角色 | 说明 |
|---|---|---|
| 报修工单 (order) | `admin`, `property`, **`owner`** | 业主可提交和查看自己的报修，物业看全部 |
| 评价管理 (evaluate) | `admin`, `property`, **`owner`** | 业主可评价自己的已完成工单 |
| 派单管理 (dispatch) | `admin`, `property` | 保持，仅物业内部操作 |
| 维修人员 (worker) | `admin`, `property` | 保持，仅物业内部管理 |

---

### 问题 2：房屋管理业主只读查看缺失

**涉及文件：** `frontend/src/router/routes.js` — `/property` 路由段

**当前：** 所有页面只有 `admin`、`property` 两个角色

**影响：** 业主登录后看不到自己名下的房产信息（房屋、房间）

**建议调整：** 在 `/property` 下新增一个业主专属只读页面（如 "我的房产"），或在现有页面上通过后端做数据隔离（业主只能看到自己的数据），将部分页面开放 `owner` 角色。

---

### 问题 3：费用管理业主完全不可见

**涉及文件：** `frontend/src/router/routes.js` — `/fee` 路由段

**当前配置：**

```js
// 父模块
{ path: '/fee', roles: ['admin', 'property', 'finance'] }

// 子页面
bill      → roles: ['admin', 'property', 'finance']
payment   → roles: ['admin', 'property', 'finance']
refund    → roles: ['admin', 'finance']
item      → roles: ['admin', 'finance']
standard  → roles: ['admin', 'finance']
arrears   → roles: ['admin', 'property', 'finance']
```

**影响：** 业主无法查看自己的账单、缴费、欠费情况

**建议调整：**

| 页面 | 建议角色 | 说明 |
|---|---|---|
| 账单管理 (bill) | `admin`, `property`, `finance`, **`owner`** | 业主查看自己的待缴/已缴账单 |
| 缴费记录 (payment) | `admin`, `property`, `finance`, **`owner`** | 业主查看自己的缴费历史 |
| 欠费管理 (arrears) | `admin`, `property`, `finance`, **`owner`** | 业主查看自己的欠费明细 |
| 退费管理 (refund) | `admin`, `finance` | 保持 |
| 收费项目 (item) | `admin`, `finance` | 保持 |
| 收费标准 (standard) | `admin`, `finance` | 保持 |

**注意：** 前端权限控制只能决定"能看见这个页面"，数据层面的隔离（业主只能看自己的）需要后端配合做数据权限过滤。

---

### 问题 4：投诉建议缺少 `suggestion` 路由

**涉及文件：** `frontend/src/router/routes.js`

**现状：** 后端有 `ComplaintSuggestController`，前端有 `src/views/complaint/suggestion/index.vue` 和 `src/api/complaint/suggestion.js`，但 **路由中只配置了 `list`、`handle`、`feedback` 三个子页面**，suggestion 页面没有路由。

**建议调整：** 在 `/complaint` 下新增路由指向 `suggestion/index.vue`

| 页面 | 建议角色 | 说明 |
|---|---|---|
| 投诉列表 (list) | `admin`, `property` | 物业查看全部投诉 |
| **投诉建议 (suggestion)** | `admin`, `property`, **`owner`** | ⬅ 新增路由，业主提交投诉建议 |
| 处理记录 (handle) | `admin`, `property` | 物业处理流程 |
| 满意度反馈 (feedback) | `admin`, `property`, `owner` | 业主评价处理结果 |

---

### 问题 5：`fee/notice` 和 `fee/record` 路由缺失

**涉及文件：** `frontend/src/router/routes.js` — `/fee` 路由段

**现状：** 前端已有视图 `src/views/fee/notice/index.vue`、`src/views/fee/record/index.vue` 和对应 API 文件 `src/api/fee/notice.js`、`src/api/fee/record.js`，但路由中只配置了 `bill`、`payment`、`refund`、`item`、`standard`、`arrears`。

**建议调整：** 在 `/fee` 下补全这两个路由

| 页面 | 建议角色 | 说明 |
|---|---|---|
| 费用通知 (notice) | `admin`, `property`, `finance` | 发送费用催缴通知 |
| 缴费记录 (record) | `admin`, `property`, `finance`, **`owner`** | 缴费流水记录 |

---

### 问题 6：`notice/announcement` 路由缺失

**涉及文件：** `frontend/src/router/routes.js` — `/notice` 路由段

**现状：** 前端已有视图 `src/views/notice/announcement/index.vue`，但路由中只配置了 `message` 和 `template`。

**建议调整：** 在 `/notice` 下补全路由

| 页面 | 建议角色 | 说明 |
|---|---|---|
| 通知公告 (announcement) | `admin`, `property`, **`owner`** | 在这里展示物业发布的公告 |

---

### 问题 7：巡检管理模块路由完全缺失 ❌ 优先级最高

**涉及文件：** `frontend/src/router/routes.js`

**现状：**

| 层面 | 状态 |
|---|---|
| 后端 Java 模块 | ✅ `InspectionPlanController`、`InspectionRecordController` |
| 前端视图 | ✅ `src/views/inspection/plan/index.vue`、`src/views/inspection/record/index.vue` |
| 前端 API | ✅ `src/api/inspection/plan.js`、`src/api/inspection/record.js` |
| **前端路由** | **❌ 完全不存在** |

**影响：** 巡检模块有完整的后端和前端代码，但无法通过菜单访问，属于未完成的前后端对接。

**建议：** 在 `routes.js` 的 `asyncRoutes` 中新增 `/inspection` 路由段：

```js
{
  path: '/inspection',
  component: () => import('@/layout/index.vue'),
  redirect: '/inspection/plan',
  name: 'Inspection',
  meta: { title: '巡检管理', icon: 'Monitor', roles: ['admin', 'property'] },
  children: [
    {
      path: 'plan',
      component: () => import('@/views/inspection/plan/index.vue'),
      name: 'InspectionPlan',
      meta: { title: '巡检计划', icon: 'Timer', roles: ['admin', 'property'] }
    },
    {
      path: 'record',
      component: () => import('@/views/inspection/record/index.vue'),
      name: 'InspectionRecord',
      meta: { title: '巡检记录', icon: 'DocumentCopy', roles: ['admin', 'property'] }
    }
  ]
}
```

---

### 问题 8：登录后跳转未按角色区分

**涉及文件：** `frontend/src/router/index.js`

**当前逻辑：**

```js
if (getToken()) {
  if (to.path === '/login') {
    next({ path: '/dashboard' })   // ← 所有人都跳仪表盘
  }
}
```

**建议调整：** 按角色跳转不同首页

| 角色 | 目标首页 | 理由 |
|---|---|---|
| `admin` | `/dashboard` | 数据概览，全局视野 |
| `property` | `/dashboard` | 数据概览，便于日常运营 |
| `finance` | `/fee/bill` | 直接进入账单管理，财务核心工作 |
| `owner` | `/fee/arrears` 或 `/notice/message` | 查看自己欠费或消息通知 |

---

## 四、调整后完整权限矩阵

| 模块 | 页面 | admin | property | finance | owner |
|---|---|---|---|---|---|
| **首页** | 数据概览 | ✅ | ✅ | ✅ | ✅ |
| **系统管理** | 用户管理 | ✅ | — | — | — |
| | 角色管理 | ✅ | — | — | — |
| | 菜单管理 | ✅ | — | — | — |
| **房屋管理** | 楼栋管理 | ✅ | ✅ | — | — |
| | 房屋信息 | ✅ | ✅ | — | **✅ (只读)** |
| | 房间管理 | ✅ | ✅ | — | — |
| | 业主管理 | ✅ | ✅ | — | — |
| | 租户管理 | ✅ | ✅ | — | — |
| | 车位管理 | ✅ | ✅ | — | — |
| **费用管理** | 账单管理 | ✅ | ✅ | ✅ | **✅ (只读)** |
| | 缴费记录 | ✅ | ✅ | ✅ | **✅ (只读)** |
| | 退费管理 | ✅ | — | ✅ | — |
| | 收费项目 | ✅ | — | ✅ | — |
| | 收费标准 | ✅ | — | ✅ | — |
| | 欠费管理 | ✅ | ✅ | ✅ | **✅ (只读)** |
| | 费用通知 | ✅ | ✅ | ✅ | — |
| **报修管理** | 报修工单 | ✅ | ✅ | — | **✅ (限自己)** |
| | 派单管理 | ✅ | ✅ | — | — |
| | 维修人员 | ✅ | ✅ | — | — |
| | 评价管理 | ✅ | ✅ | — | **✅ (限自己)** |
| **投诉建议** | 投诉列表 | ✅ | ✅ | — | — |
| | 投诉建议 | ✅ | ✅ | — | **✅** |
| | 处理记录 | ✅ | ✅ | — | — |
| | 满意度反馈 | ✅ | ✅ | — | ✅ |
| **公告通知** | 公告管理 | ✅ | ✅ | — | — |
| | **公告查看** | ✅ | ✅ | ✅ | **✅ (新增只读视图)** |
| **消息通知** | 消息中心 | ✅ | ✅ | ✅ | ✅ |
| | 模板管理 | ✅ | ✅ | — | — |
| | 通知公告 | ✅ | ✅ | ✅ | ✅ |
| **车位管理** | 车位信息 | ✅ | ✅ | — | **✅ (只读)** |
| | 租赁管理 | ✅ | ✅ | — | — |
| | 车辆登记 | ✅ | ✅ | — | ✅ |
| | 进出记录 | ✅ | ✅ | — | **✅ (只读)** |
| **设备管理** | 设备分类 | ✅ | ✅ | — | — |
| | 设备列表 | ✅ | ✅ | — | — |
| | 维保记录 | ✅ | ✅ | — | — |
| **巡检管理** | 巡检计划 | ✅ | ✅ | — | — |
| | 巡检记录 | ✅ | ✅ | — | — |
| **统计分析** | 统计仪表盘 | ✅ | ✅ | — | — |
| **个人中心** | 个人信息 | ✅ | ✅ | ✅ | ✅ |
| | 修改密码 | ✅ | ✅ | ✅ | ✅ |

---

## 五、修复优先级建议

| 优先级 | 问题 | 工作量 | 影响面 |
|---|---|---|---|
| 🔴 P0 | 巡检管理缺少路由 | 小（1 路由段） | 模块无法访问 |
| 🔴 P0 | 报修缺少 owner 角色 | 小（改 roles） | 核心功能缺失 |
| 🔴 P0 | 费用管理 owner 不可见 | 中（roles + 后端数据隔离） | 业主无法缴费 |
| 🟡 P1 | complaint/suggestion 缺路由 | 小（1 条路由） | 功能不完整 |
| 🟡 P1 | fee/notice、fee/record 缺路由 | 小（2 条路由） | 功能不完整 |
| 🟡 P1 | notice/announcement 缺路由 | 小（1 条路由） | 功能不完整 |
| 🟢 P2 | 房屋管理业主只读查看 | 中（需新建或改造页面） | 体验优化 |
| 🟢 P2 | 登录后按角色跳转不同首页 | 小（改 router 守卫） | 体验优化 |
