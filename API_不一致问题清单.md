# 前后端接口不一致问题清单

> **生成时间**: 2026-07-16  
> **项目**: 物业管理系统  
> **检查范围**: 前端 `frontend/src/api/` 与后端 Controller 对比  
> **核心问题**: 前端 API 调用路径缺少 `/api` 前缀，且多模块接口路径、参数、功能存在大量不匹配

---

## 目录
1. [核心问题总览](#核心问题总览)
2. [系统管理模块](#系统管理模块)
3. [小区基础模块](#小区基础模块)
4. [收费管理模块](#收费管理模块)
5. [设备管理模块](#设备管理模块)
6. [报修管理模块](#报修管理模块)
7. [投诉建议模块](#投诉建议模块)
8. [巡检管理模块](#巡检管理模块)
9. [公告通知模块](#公告通知模块)
10. [统计分析模块](#统计分析模块)
11. [通用问题汇总](#通用问题汇总)
12. [修复建议](#修复建议)

---

## 核心问题总览

| 问题类型 | 影响模块数 | 严重程度 | 说明 |
|---------|-----------|---------|------|
| **缺少 `/api` 前缀** | 全模块 (100%) | 🔴 严重 | 前端所有接口调用均缺少 `/api` 前缀，后端 Controller 均以 `/api` 开头 |
| **路径命名不一致** | 9/10 模块 | 🔴 严重 | 复数/单数、下划线/驼峰、模块前缀差异 |
| **前端调用后端不存在的接口** | 8/10 模块 | 🟠 高 | 前端定义了后端未实现的接口（如导出、状态变更、树形结构等） |
| **后端实现前端未调用的接口** | 7/10 模块 | 🟡 中 | 后端有前端未使用的接口（如发布、置顶、生成等） |
| **参数格式不一致** | 多模块 | 🟡 中 | Query/Body/Path 参数传递方式不统一 |

---

## 系统管理模块

### 后端 Controller: `SysUserController` (`/api/system/user`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/system/user/page` | `/api/system/user/page` | ❌ 不匹配 | 缺少 `/api` 前缀 |
| `/system/user/{userId}` | `/api/system/user/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/system/user` (POST) | `/api/system/user` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/system/user` (PUT) | `/api/system/user` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/system/user/{userIds}` (DELETE) | `/api/system/user/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/system/user/password` (PUT) | `/api/system/user/password` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/system/user/status` (PUT) | `/api/system/user/status` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/system/role/list` | `/api/system/role/list` | ❌ 不匹配 | 缺少 `/api` |
| `/system/dept/tree` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |
| `/system/user/export` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |
| `/system/user/importData` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |
| `/system/user/importTemplate` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |

### 后端 Controller: `SysMenuController` (`/api/system/menu`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/system/menu/list` | `/api/system/menu/list` | ❌ 不匹配 | 缺少 `/api` |
| `/system/menu/tree` | `/api/system/menu/tree` | ❌ 不匹配 | 缺少 `/api` |
| `/system/menu/{menuId}` | `/api/system/menu/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/system/menu` (POST) | `/api/system/menu` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/system/menu` (PUT) | `/api/system/menu` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/system/menu/{menuIds}` (DELETE) | `/api/system/menu/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/system/menu/treeselect` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |
| `/system/menu/roleMenuTreeselect/{roleId}` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |
| `/system/menu/export` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |

### 后端 Controller: `SysRoleController` (`/api/system/role`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/system/role/list` | `/api/system/role/list` | ❌ 不匹配 | 缺少 `/api` |
| `/system/role/{roleId}` | `/api/system/role/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/system/role` (POST) | `/api/system/role` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/system/role` (PUT) | `/api/system/role` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/system/role/{roleIds}` (DELETE) | `/api/system/role/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/system/menu/roleMenuTreeselect/{roleId}` | `/api/system/role/{roleId}/menus` (GET) | ❌ 路径完全不同 | 前后端路径设计不一致 |
| `/system/menu/treeselect` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |
| `/system/dept/treeselect` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |
| `/system/role/export` | **后端无此接口** | ❌ 后端缺失 | 前端调用但后端未实现 |

**后端独有接口（前端未调用）**：
- `PUT /api/system/role/assignMenus` - 分配菜单权限
- `GET /api/system/role/{roleId}/menus` - 获取角色菜单ID列表

---

## 小区基础模块

### 后端 Controller: `CommunityBuildingController` (`/api/community/building`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/community/building/page` | `/api/community/building/page` | ❌ 不匹配 | 缺少 `/api` |
| `/community/building/{buildingId}` | `/api/community/building/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/community/building` (POST) | `/api/community/building` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/community/building` (PUT) | `/api/community/building` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/community/building/{buildingIds}` (DELETE) | `/api/community/building/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `PUT /community/building/{buildingId}/status` - 修改楼栋状态
- `GET /community/building/export` - 导出楼栋
- `GET /community/building/rooms` - 获取楼栋房间列表

### 后端 Controller: `CommunityHouseController` (`/api/community/house`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/community/house/page` | `/api/community/house/page` | ❌ 不匹配 | 缺少 `/api` |
| `/community/house/{houseId}` | `/api/community/house/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/community/house` (POST) | `/api/community/house` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/community/house` (PUT) | `/api/community/house` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/community/house/{houseIds}` (DELETE) | `/api/community/house/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `PUT /community/house/{houseId}/status` - 修改房屋状态
- `GET /community/house/export` - 导出房屋
- `GET /community/house/owners` - 获取房屋业主列表
- `POST /community/house/bindOwner` - 绑定业主
- `POST /community/house/unbindOwner` - 解绑业主
- `GET /community/building/tree` - 获取楼栋树
- `GET /community/house/tree` - 获取房屋树

### 后端 Controller: `CommunityOwnerController` (`/api/community/owner`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/community/owner/page` | `/api/community/owner/page` | ❌ 不匹配 | 缺少 `/api` |
| `/community/owner/{ownerId}` | `/api/community/owner/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/community/owner` (POST) | `/api/community/owner` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/community/owner` (PUT) | `/api/community/owner` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/community/owner/{ownerIds}` (DELETE) | `/api/community/owner/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `PUT /community/owner/{ownerId}/status/{status}` - 修改业主状态
- `GET /community/owner/export` - 导出业主
- `GET /community/owner/houses` - 获取业主房屋列表
- `POST /community/owner/bindHouse` - 绑定房屋
- `POST /community/owner/unbindHouse` - 解绑房屋
- `GET /community/house/tree` - 获取房屋树
- `POST /community/owner/uploadIdCard` - 上传身份证
- `GET /community/owner/{ownerId}/idCard` - 获取身份证信息
- `DELETE /community/owner/{ownerId}/idCard/{type}` - 删除身份证

### 后端 Controller: `CommunityParkingController` (`/api/community/parking`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/community/parking/page` | `/api/community/parking/page` | ❌ 不匹配 | 缺少 `/api` |
| `/community/parking/{parkingId}` | `/api/community/parking/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/community/parking` (POST) | `/api/community/parking` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/community/parking` (PUT) | `/api/community/parking` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/community/parking/{parkingIds}` (DELETE) | `/api/community/parking/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `PUT /community/parking/{parkingId}/status` - 修改车位状态
- `GET /community/parking/export` - 导出车位
- `POST /community/parking/bindOwner` - 绑定业主
- `POST /community/parking/unbindOwner` - 解绑业主
- `GET /community/parking/rent/page` - 车位租赁分页
- `GET /community/building/tree` - 获取楼栋树
- `GET /community/house/tree` - 获取房屋树

---

## 收费管理模块

### 后端 Controller: `FeeNoticeController` (`/api/fee/notice`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/fee/notice/page` | `/api/fee/notice/page` | ❌ 不匹配 | 缺少 `/api` |
| `/fee/notice/{noticeId}` | `/api/fee/notice/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/fee/notice` (POST) | `/api/fee/notice` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/fee/notice` (PUT) | `/api/fee/notice` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/fee/notice/{noticeIds}` (DELETE) | `/api/fee/notice/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `POST /fee/notice/send` - 发送通知
- `GET /fee/notice/{noticeId}/sendDetail` - 获取发送详情
- `PUT /fee/notice/markRead` - 标记已读/未读
- `GET /fee/notice/export` - 导出通知

**后端独有接口（前端未调用）**：
- `PUT /api/fee/notice/publish/{id}` - 发布通知

### 后端 Controller: `FeeItemController` (`/api/fee/item`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/fee/item/page` | `/api/fee/item/page` | ❌ 不匹配 | 缺少 `/api` |
| `/fee/item/{itemId}` | `/api/fee/item/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/fee/item` (POST) | `/api/fee/item` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/fee/item` (PUT) | `/api/fee/item` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/fee/item/{itemIds}` (DELETE) | `/api/fee/item/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `PUT /fee/item/status` - 修改收费项目状态
- `GET /fee/item/export` - 导出收费项目
- `GET /fee/standard/page` - 获取收费标准列表
- `GET /system/dict/data/type/{dictType}` - 获取字典数据

**后端独有接口（前端未调用）**：
- `PUT /api/fee/item/status` - 修改状态（参数方式不同：前端用 Body，后端用 Query）

### 后端 Controller: `FeeRecordController` (`/api/fee/record`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/fee/record/page` | `/api/fee/record/page` | ❌ 不匹配 | 缺少 `/api` |
| `/fee/record/{recordId}` | `/api/fee/record/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `POST /fee/record/refund` - 退费
- `GET /fee/record/export` - 导出缴费记录
- `GET /fee/record/statistics` - 缴费记录统计

**后端独有接口（前端未调用）**：
- `POST /api/fee/record/generate` - 生成账单
- `PUT /api/fee/record/pay` - 确认缴费
- `GET /api/fee/record/statistics` - 统计（参数不同）
- `PUT /api/fee/record/markOverdue` - 标记逾期

---

## 设备管理模块

### 后端 Controller: `EquipmentController` (`/api/equipment`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/equipment/equipment/tree` | `/api/equipment/page` 等 | ❌ 路径完全不同 | 前端调用 tree，后端无 tree 接口 |

**前端独有接口（后端缺失）**：
- `GET /equipment/equipment/tree` - 设备树（仅在巡检模块中调用）

**后端独有接口（前端未调用）**：
- `POST /api/equipment` - 新增设备
- `PUT /api/equipment` - 修改设备
- `DELETE /api/equipment/{id}` - 删除设备
- `GET /api/equipment/{id}` - 设备详情
- `GET /api/equipment/page` - 设备分页
- `PUT /api/equipment/status` - 修改设备状态

### 后端 Controller: `EquipmentCategoryController` (`/api/equipment/category`)

**后端独有接口（前端未调用）**：
- `POST /api/equipment/category` - 新增分类
- `PUT /api/equipment/category` - 修改分类
- `DELETE /api/equipment/category/{id}` - 删除分类
- `GET /api/equipment/category/list` - 分类列表

### 后端 Controller: `EquipmentMaintenanceController` (`/api/equipment/maintenance`)

**后端独有接口（前端未调用）**：
- `POST /api/equipment/maintenance` - 新增维修记录
- `PUT /api/equipment/maintenance` - 修改维修记录
- `DELETE /api/equipment/maintenance/{id}` - 删除维修记录
- `GET /api/equipment/maintenance/{id}` - 维修记录详情
- `GET /api/equipment/maintenance/page` - 维修记录分页
- `PUT /api/equipment/maintenance/start/{id}` - 开始维修
- `PUT /api/equipment/maintenance/complete/{id}` - 完成维修

---

## 报修管理模块

### 后端 Controller: `RepairRecordController` (`/api/repair/record`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/repair/record/page` | `/api/repair/record/page` | ❌ 不匹配 | 缺少 `/api` |
| `/repair/record/{orderId}` | **后端无单个查询接口** | ❌ 后端缺失 | 前端调用但后端无 GET /{id} |
| `/repair/record` (POST) | `/api/repair/record` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/repair/record` (PUT) | `/api/repair/record` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/repair/record/{orderIds}` (DELETE) | `/api/repair/record/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `PUT /repair/record/{orderId}/status` - 取消工单（status=cancelled）
- `PUT /repair/record/status` - 派单/处理进度/完工确认（三个功能复用同一接口）
- `PUT /repair/evaluate/{evaluateId}/reply` - 评价回复
- `GET /repair/record/export` - 导出工单
- `GET /repair/statistics` - 报修统计
- `GET /community/house/tree` - 房屋树
- `GET /repair/worker/page` - 维修人员列表
- `POST /common/upload` - 图片上传
- `DELETE /common/upload/batchDelete` - 批量删除图片

**后端独有接口（前端未调用）**：
- `PUT /api/repair/record/status` - 更新状态（参数：id, status, handlerId, handleContent）
- `PUT /api/repair/record/rating` - 评价（参数：id, score, content）

---

## 投诉建议模块

### 后端 Controller: `ComplaintSuggestController` (`/api/complaint/suggest`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/complaint/suggest/page` | `/api/complaint/suggest/page` | ❌ 不匹配 | 缺少 `/api` |
| `/complaint/suggest/{id}` | **后端无单个查询接口** | ❌ 后端缺失 | 前端调用但后端无 GET /{id} |
| `/complaint/suggest` (POST) | `/api/complaint/suggest` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/complaint/suggest` (PUT) | `/api/complaint/suggest` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/complaint/suggest/{ids}` (DELETE) | `/api/complaint/suggest/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `PUT /complaint/suggest/status` - 受理/回复/评价（三个功能复用同一接口）
- `PUT /complaint/suggest/{id}/status` - 关闭投诉
- `GET /complaint/suggest/export` - 导出投诉
- `GET /complaint/suggest/statistics` - 投诉统计
- `GET /system/user/page` - 获取处理人列表
- `POST /common/upload` - 图片上传

**后端独有接口（前端未调用）**：
- `PUT /api/complaint/suggest/status` - 更新状态（参数：id, status, handlerId, handleContent）

---

## 巡检管理模块

### 后端 Controller: `InspectionPlanController` (`/api/inspection/plan`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/inspection/plan/page` | `/api/inspection/plan/page` | ❌ 不匹配 | 缺少 `/api` |
| `/inspection/plan/{planId}` | `/api/inspection/plan/{id}` | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |
| `/inspection/plan` (POST) | `/api/inspection/plan` (POST) | ❌ 不匹配 | 缺少 `/api` |
| `/inspection/plan` (PUT) | `/api/inspection/plan` (PUT) | ❌ 不匹配 | 缺少 `/api` |
| `/inspection/plan/{planIds}` (DELETE) | `/api/inspection/plan/{id}` (DELETE) | ❌ 不匹配 | 缺少 `/api`，参数名不一致 |

**前端独有接口（后端缺失）**：
- `PUT /inspection/plan/status` - 修改计划状态
- `GET /inspection/plan/export` - 导出计划
- `GET /inspection/plan/statistics` - 计划统计
- `GET /inspection/plan/records` - 计划执行记录
- `GET /community/building/tree` - 楼栋树
- `GET /equipment/equipment/tree` - 设备树
- `GET /system/user/page` - 用户列表

**后端独有接口（前端未调用）**：
- `PUT /api/inspection/plan/status` - 更新状态
- `POST /api/inspection/plan/generate` - 按周期生成计划

### 后端 Controller: `InspectionRecordController` (`/api/inspection/record`)

**后端独有接口（前端未调用）**：
- `POST /api/inspection/record` - 新增巡检记录
- `PUT /api/inspection/record` - 修改巡检记录
- `GET /api/inspection/record/page` - 巡检记录分页
- `GET /api/inspection/record/{id}` - 巡检记录详情

---

## 公告通知模块

### 后端 Controller: `AnnouncementController` (`/api/announcement`)

| 前端调用路径 | 后端实际路径 | 状态 | 问题详情 |
|------------|-------------|------|---------|
| `/announcement/announcement/page` | `/api/announcement/page` | ❌ 路径层级不同 | 前端多一层 `announcement` |
| `/announcement/announcement/{id}` | `/api/announcement/{id}` | ❌ 路径层级不同 | 前端多一层 `announcement` |
| `/announcement/announcement` (POST) | `/api/announcement` (POST) | ❌ 路径层级不同 | 前端多一层 `announcement` |
| `/announcement/announcement` (PUT) | `/api/announcement` (PUT) | ❌ 路径层级不同 | 前端多一层 `announcement` |
| `/announcement/announcement/{ids}` (DELETE) | `/api/announcement/{id}` (DELETE) | ❌ 路径层级不同 | 前端多一层 `announcement` |

**后端独有接口（前端未调用）**：
- `PUT /api/announcement/status` - 更新状态（发布/下架）
- `PUT /api/announcement/top` - 置顶/取消置顶

---

## 统计分析模块

### 后端 Controller: `StatisticsController`

| 后端实际路径 | 说明 |
|-------------|------|
| `/api/statistics/fee` | 收费统计 |
| `/api/statistics/repair` | 报修统计 |
| `/api/statistics/complaint` | 投诉统计 |
| `/api/statistics/equipment` | 设备统计 |
| `/api/statistics/owner` | 业主统计 |

**前端调用路径（全部不匹配）**：
- `/statistics/overview` - 后端无此接口
- `/statistics/fee/monthly` - 后端无此接口
- `/statistics/dashboard/fee/trend` - 后端无此接口
- `/statistics/fee/byItem` - 后端无此接口
- `/statistics/repair/overview` - 后端无此接口
- `/statistics/dashboard/repair/trend` - 后端无此接口
- `/statistics/repair/byType` - 后端无此接口
- `/statistics/dashboard/device` - 后端无此接口
- `/statistics/device/statusRatio` - 后端无此接口

**结论**：前端统计模块接口设计与后端完全不匹配，需重新对齐。

---

## 通用问题汇总

### 1. 统一的 `/api` 前缀缺失（全模块）
- **现状**: 前端所有 API 调用路径均以 `/module/...` 开头
- **后端**: 所有 Controller `@RequestMapping` 均以 `/api/module/...` 开头
- **影响**: 所有接口调用将返回 404
- **修复**: 前端请求拦截器统一添加 `/api` 前缀，或修改所有 API 文件

### 2. 路径参数命名不一致
| 前端参数名 | 后端参数名 | 出现频率 |
|-----------|-----------|---------|
| `userId` / `userIds` | `id` | 极高 |
| `buildingId` / `buildingIds` | `id` | 高 |
| `houseId` / `houseIds` | `id` | 高 |
| `ownerId` / `ownerIds` | `id` | 高 |
| `parkingId` / `parkingIds` | `id` | 高 |
| `noticeId` / `noticeIds` | `id` | 高 |
| `itemId` / `itemIds` | `id` | 高 |
| `recordId` | `id` | 高 |
| `orderId` / `orderIds` | `id` | 高 |
| `planId` / `planIds` | `id` | 高 |

### 3. 批量删除参数格式差异
- **前端**: 传递字符串如 `"1,2,3"` 或数组 `[1,2,3]` 拼接到 URL
- **后端**: `@PathVariable Long id` 仅接收单个 ID
- **建议**: 统一使用 `@RequestBody List<Long> ids` 或 `@PathVariable String ids` 解析

### 4. 状态更新接口设计差异
- **前端**: 倾向于统一 `/status` 路径，通过 Body 传递操作类型
- **后端**: 多为独立端点（`/publish`、`/top`、`/status` 等）或 Query 参数
- **典型案例**: 报修、投诉模块前端用同一 `/status` 做派单/处理/完工/评价，后端分离

### 5. 导出/导入/树形结构接口后端大量缺失
- 导出: 社区、收费、报修、投诉、巡检模块前端均有导出调用，后端均无实现
- 树形结构: 楼栋树、房屋树、设备树、部门树、菜单树等前端调用，后端多缺失

### 6. 文件上传/通用接口前端调用但后端无对应 Controller
- `/common/upload` - 图片上传
- `/common/upload/batchDelete` - 批量删除图片
- `/system/dict/data/type/{dictType}` - 字典数据

---

## 修复建议

---

### 方案：逐个模块对齐修复（彻底但工作量大）

1. **建立接口契约文档**（OpenAPI/Swagger）
2. **前后端联调会议**确认每个接口的：
   - 完整路径
   - HTTP Method
   - 参数位置
   - 请求/响应数据结构
3. **按模块并行修复**，建议优先级：
   - P0: 系统管理、小区基础（核心基础模块）
   - P1: 收费管理、报修管理（核心业务模块）
   - P2: 设备、巡检、投诉、公告、统计

---

### 后端需补齐的接口清单（优先级 P0）

| 模块 | 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|------|
| 系统-用户 | 导出 | GET | `/api/system/user/export` | 前端已调用 |
| 系统-用户 | 导入 | POST | `/api/system/user/importData` | 前端已调用 |
| 系统-用户 | 导入模板 | GET | `/api/system/user/importTemplate` | 前端已调用 |
| 系统-菜单 | 树形选择 | GET | `/api/system/menu/treeselect` | 前端已调用 |
| 系统-菜单 | 角色菜单树 | GET | `/api/system/menu/roleMenuTreeselect/{roleId}` | 前端已调用 |
| 系统-菜单 | 导出 | GET | `/api/system/menu/export` | 前端已调用 |
| 系统-角色 | 部门树选择 | GET | `/api/system/dept/treeselect` | 前端已调用 |
| 系统-角色 | 导出 | GET | `/api/system/role/export` | 前端已调用 |
| 社区-楼栋 | 状态变更 | PUT | `/api/community/building/{id}/status` | 前端已调用 |
| 社区-楼栋 | 导出 | GET | `/api/community/building/export` | 前端已调用 |
| 社区-楼栋 | 房间列表 | GET | `/api/community/building/rooms` | 前端已调用 |
| 社区-房屋 | 状态变更 | PUT | `/api/community/house/{id}/status` | 前端已调用 |
| 社区-房屋 | 导出 | GET | `/api/community/house/export` | 前端已调用 |
| 社区-房屋 | 业主列表 | GET | `/api/community/house/owners` | 前端已调用 |
| 社区-房屋 | 绑定业主 | POST | `/api/community/house/bindOwner` | 前端已调用 |
| 社区-房屋 | 解绑业主 | POST | `/api/community/house/unbindOwner` | 前端已调用 |
| 社区-房屋 | 房屋树 | GET | `/api/community/house/tree` | 前端已调用 |
| 社区-楼栋树 | 树形列表 | GET | `/api/community/building/tree` | 多模块调用 |
| 社区-业主 | 状态变更 | PUT | `/api/community/owner/{id}/status` | 前端已调用 |
| 社区-业主 | 导出 | GET | `/api/community/owner/export` | 前端已调用 |
| 社区-业主 | 房屋列表 | GET | `/api/community/owner/houses` | 前端已调用 |
| 社区-业主 | 绑定房屋 | POST | `/api/community/owner/bindHouse` | 前端已调用 |
| 社区-业主 | 解绑房屋 | POST | `/api/community/owner/unbindHouse` | 前端已调用 |
| 社区-业主 | 身份证上传 | POST | `/api/community/owner/uploadIdCard` | 前端已调用 |
| 社区-业主 | 身份证信息 | GET | `/api/community/owner/{id}/idCard` | 前端已调用 |
| 社区-业主 | 删除身份证 | DELETE | `/api/community/owner/{id}/idCard/{type}` | 前端已调用 |
| 社区-车位 | 状态变更 | PUT | `/api/community/parking/{id}/status` | 前端已调用 |
| 社区-车位 | 导出 | GET | `/api/community/parking/export` | 前端已调用 |
| 社区-车位 | 绑定业主 | POST | `/api/community/parking/bindOwner` | 前端已调用 |
| 社区-车位 | 解绑业主 | POST | `/api/community/parking/unbindOwner` | 前端已调用 |
| 社区-车位 | 租赁分页 | GET | `/api/community/parking/rent/page` | 前端已调用 |
| 收费-通知 | 发送 | POST | `/api/fee/notice/send` | 前端已调用 |
| 收费-通知 | 发送详情 | GET | `/api/fee/notice/{id}/sendDetail` | 前端已调用 |
| 收费-通知 | 标记已读 | PUT | `/api/fee/notice/markRead` | 前端已调用 |
| 收费-通知 | 导出 | GET | `/api/fee/notice/export` | 前端已调用 |
| 收费-项目 | 状态变更 | PUT | `/api/fee/item/status` | 前端已调用（参数调整为 Query） |
| 收费-项目 | 导出 | GET | `/api/fee/item/export` | 前端已调用 |
| 收费-项目 | 收费标准 | GET | `/api/fee/standard/page` | 前端已调用 |
| 收费-记录 | 退费 | POST | `/api/fee/record/refund` | 前端已调用 |
| 收费-记录 | 导出 | GET | `/api/fee/record/export` | 前端已调用 |
| 收费-记录 | 统计 | GET | `/api/fee/record/statistics` | 前端已调用 |
| 报修 | 单个查询 | GET | `/api/repair/record/{id}` | 前端已调用 |
| 报修 | 状态更新 | PUT | `/api/repair/record/{id}/status` | 前端已调用 |
| 报修 | 导出 | GET | `/api/repair/record/export` | 前端已调用 |
| 报修 | 统计 | GET | `/api/repair/statistics` | 前端已调用 |
| 报修 | 维修人员 | GET | `/api/repair/worker/page` | 前端已调用 |
| 投诉 | 单个查询 | GET | `/api/complaint/suggest/{id}` | 前端已调用 |
| 投诉 | 状态更新 | PUT | `/api/complaint/suggest/{id}/status` | 前端已调用 |
| 投诉 | 导出 | GET | `/api/complaint/suggest/export` | 前端已调用 |
| 投诉 | 统计 | GET | `/api/complaint/suggest/statistics` | 前端已调用 |
| 巡检-计划 | 状态变更 | PUT | `/api/inspection/plan/{id}/status` | 前端已调用 |
| 巡检-计划 | 导出 | GET | `/api/inspection/plan/export` | 前端已调用 |
| 巡检-计划 | 统计 | GET | `/api/inspection/plan/statistics` | 前端已调用 |
| 巡检-计划 | 执行记录 | GET | `/api/inspection/plan/records` | 前端已调用 |
| 通用 | 文件上传 | POST | `/api/common/upload` | 多模块调用 |
| 通用 | 批量删除文件 | DELETE | `/api/common/upload/batchDelete` | 多模块调用 |
| 通用 | 字典数据 | GET | `/api/system/dict/data/type/{dictType}` | 收费模块调用 |

---

### 前端需删除/调整的接口清单（后端无对应实现）

| 模块 | 接口 | 建议 |
|------|------|------|
| 统计 | 所有 `/statistics/...` | 完全重写，对齐后端 5 个统计接口 |
| 公告 | `/announcement/announcement/...` | 去掉多余的 `/announcement` 前缀 |
| 设备 | `/equipment/equipment/tree` | 后端需补充或前端改用分页接口构建树 |

---

## 验证清单（修复后必测）

- [ ] 所有增删改查接口正常返回 200
- [ ] 分页查询参数 `pageNum`/`pageSize` 正确传递
- [ ] 批量删除支持多 ID 传递
- [ ] 导出接口返回 `blob` 类型可下载
- [ ] 文件上传 `multipart/form-data` 正常
- [ ] 树形结构接口返回层级数据正确
- [ ] 统计接口返回数据结构与前端图表组件兼容
- [ ] 权限标识与后端 `@PreAuthorize` 一致

---

*文档维护者：开发团队*  
*下次更新：前后端联调完成后*