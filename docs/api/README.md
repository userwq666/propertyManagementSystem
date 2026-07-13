# API接口文档

## 一、概述

本文档描述物业管理系统的所有API接口。所有接口均采用RESTful风格设计，返回统一的Result格式。

### 1.1 基础路径

```
http://localhost:8080/api
```

### 1.2 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 1.3 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 1.4 认证方式

采用JWT Token认证，需要在请求头中添加：

```
Authorization: Bearer <token>
```

---

## 二、系统管理模块

### 2.1 认证接口

#### 2.1.1 用户登录

```
POST /api/auth/login
```

**请求参数：**

```json
{
  "username": "root",
  "password": "123456"
}
```

**响应参数：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### 2.2 用户管理

#### 2.2.1 获取用户分页列表

```
GET /api/user/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| username | String | 否 | 用户名 |
| realName | String | 否 | 真实姓名 |
| phone | String | 否 | 手机号 |
| status | Integer | 否 | 状态 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "root",
        "realName": "管理员",
        "phone": "13800138000",
        "status": 1,
        "createTime": "2026-07-12T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 2.2.2 新增用户

```
POST /api/user
```

**请求参数：**

```json
{
  "username": "admin",
  "password": "123456",
  "realName": "管理员",
  "phone": "13800138000",
  "email": "admin@example.com",
  "status": 1,
  "roleIds": [1, 2]
}
```

#### 2.2.3 更新用户

```
PUT /api/user
```

**请求参数：**

```json
{
  "id": 1,
  "username": "admin",
  "realName": "管理员",
  "phone": "13800138000",
  "email": "admin@example.com",
  "status": 1,
  "roleIds": [1, 2]
}
```

#### 2.2.4 删除用户

```
DELETE /api/user/{id}
```

#### 2.2.5 获取用户详情

```
GET /api/user/{id}
```

#### 2.2.6 修改密码

```
PUT /api/user/password
```

**请求参数：**

```json
{
  "id": 1,
  "password": "newPassword123"
}
```

### 2.3 角色管理

#### 2.3.1 获取角色分页列表

```
GET /api/role/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| roleName | String | 否 | 角色名称 |
| status | Integer | 否 | 状态 |

#### 2.3.2 新增角色

```
POST /api/role
```

**请求参数：**

```json
{
  "roleName": "管理员",
  "roleCode": "admin",
  "status": 1,
  "menuIds": [1, 2, 3]
}
```

#### 2.3.3 更新角色

```
PUT /api/role
```

#### 2.3.4 删除角色

```
DELETE /api/role/{id}
```

#### 2.3.5 获取角色详情

```
GET /api/role/{id}
```

#### 2.3.6 获取角色列表

```
GET /api/role/list
```

### 2.4 菜单管理

#### 2.4.1 获取菜单列表

```
GET /api/menu/list
```

#### 2.4.2 新增菜单

```
POST /api/menu
```

**请求参数：**

```json
{
  "menuName": "系统管理",
  "parentId": 0,
  "orderNum": 1,
  "path": "system",
  "component": "Layout",
  "menuType": "M",
  "visible": 1,
  "status": 1,
  "perms": "",
  "icon": "Setting"
}
```

#### 2.4.3 更新菜单

```
PUT /api/menu
```

#### 2.4.4 删除菜单

```
DELETE /api/menu/{id}
```

#### 2.4.5 获取菜单详情

```
GET /api/menu/{id}
```

### 2.5 操作日志

#### 2.5.1 获取操作日志分页列表

```
GET /api/operlog/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| title | String | 否 | 操作模块 |
| businessType | Integer | 否 | 业务类型 |
| operName | String | 否 | 操作人 |
| status | Integer | 否 | 状态 |

---

## 三、小区基础信息模块

### 3.1 楼栋管理

#### 3.1.1 获取楼栋分页列表

```
GET /api/building/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| buildingNo | String | 否 | 楼栋编号 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "buildingNo": "A1",
        "floorCount": 18,
        "totalHouse": 72,
        "buildYear": 2020,
        "remark": "",
        "createTime": "2026-07-12T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 3.1.2 新增楼栋

```
POST /api/building
```

**请求参数：**

```json
{
  "buildingNo": "A1",
  "floorCount": 18,
  "totalHouse": 72,
  "buildYear": 2020,
  "remark": ""
}
```

#### 3.1.3 更新楼栋

```
PUT /api/building
```

#### 3.1.4 删除楼栋

```
DELETE /api/building/{id}
```

#### 3.1.5 获取楼栋详情

```
GET /api/building/{id}
```

#### 3.1.6 获取楼栋列表

```
GET /api/building/list
```

### 3.2 房屋管理

#### 3.2.1 获取房屋分页列表

```
GET /api/house/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| buildingId | Long | 否 | 楼栋ID |
| roomNo | String | 否 | 房间号 |
| houseStatus | Integer | 否 | 房屋状态 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "buildingId": 1,
        "buildingNo": "A1",
        "roomNo": "101",
        "area": 89.50,
        "houseType": "两室一厅",
        "houseStatus": 1,
        "ownerId": 1,
        "ownerName": "张三",
        "createTime": "2026-07-12T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 3.2.2 新增房屋

```
POST /api/house
```

**请求参数：**

```json
{
  "buildingId": 1,
  "roomNo": "101",
  "area": 89.50,
  "houseType": "两室一厅",
  "houseStatus": 0,
  "ownerId": null,
  "remark": ""
}
```

#### 3.2.3 更新房屋

```
PUT /api/house
```

#### 3.2.4 删除房屋

```
DELETE /api/house/{id}
```

#### 3.2.5 获取房屋详情

```
GET /api/house/{id}
```

### 3.3 业主管理

#### 3.3.1 获取业主分页列表

```
GET /api/owner/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| name | String | 否 | 业主姓名 |
| phone | String | 否 | 联系电话 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 2,
        "name": "张三",
        "idCard": "310101199001011234",
        "phone": "13800138001",
        "emergencyContact": "李四",
        "emergencyPhone": "13800138002",
        "checkInTime": "2026-01-01T00:00:00",
        "createTime": "2026-07-12T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 3.3.2 新增业主

```
POST /api/owner
```

**请求参数：**

```json
{
  "name": "张三",
  "idCard": "310101199001011234",
  "phone": "13800138001",
  "emergencyContact": "李四",
  "emergencyPhone": "13800138002",
  "checkInTime": "2026-01-01T00:00:00",
  "createUser": true,
  "username": "owner1",
  "password": "123456"
}
```

#### 3.3.3 更新业主

```
PUT /api/owner
```

#### 3.3.4 删除业主

```
DELETE /api/owner/{id}
```

#### 3.3.5 获取业主详情

```
GET /api/owner/{id}
```

### 3.4 车位管理

#### 3.4.1 获取车位分页列表

```
GET /api/parking/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| parkingNo | String | 否 | 车位编号 |
| parkingType | Integer | 否 | 车位类型 |
| status | Integer | 否 | 状态 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "parkingNo": "P001",
        "parkingType": 0,
        "status": 1,
        "ownerId": 1,
        "ownerName": "张三",
        "expireTime": "2026-12-31T23:59:59",
        "createTime": "2026-07-12T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 3.4.2 新增车位

```
POST /api/parking
```

**请求参数：**

```json
{
  "parkingNo": "P001",
  "parkingType": 0,
  "status": 0,
  "ownerId": null,
  "expireTime": null,
  "remark": ""
}
```

#### 3.4.3 更新车位

```
PUT /api/parking
```

#### 3.4.4 删除车位

```
DELETE /api/parking/{id}
```

#### 3.4.5 获取车位详情

```
GET /api/parking/{id}
```

---

## 四、物业收费模块

### 4.1 收费项目管理

#### 4.1.1 获取收费项目分页列表

```
GET /api/fee/item/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "itemName": "物业费",
        "price": 2.50,
        "cycleType": 1,
        "status": 1,
        "remark": "",
        "createTime": "2026-07-12T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 4.1.2 新增收费项目

```
POST /api/fee/item
```

**请求参数：**

```json
{
  "itemName": "物业费",
  "price": 2.50,
  "cycleType": 1,
  "status": 1,
  "remark": ""
}
```

#### 4.1.3 更新收费项目

```
PUT /api/fee/item
```

#### 4.1.4 删除收费项目

```
DELETE /api/fee/item/{id}
```

#### 4.1.5 获取收费项目详情

```
GET /api/fee/item/{id}
```

#### 4.1.6 获取收费项目列表

```
GET /api/fee/item/list
```

#### 4.1.7 更新收费项目状态

```
PUT /api/fee/item/status
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 收费项目ID |
| status | Integer | 是 | 状态：0禁用 1启用 |

### 4.2 账单管理

#### 4.2.1 批量生成账单

```
POST /api/fee/record/generate
```

**请求参数：**

```json
[
  {
    "ownerId": 1,
    "houseId": 1,
    "itemId": 1,
    "totalMoney": 225.00,
    "billCycle": "2026-07"
  }
]
```

#### 4.2.2 获取账单分页列表

```
GET /api/fee/record/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| ownerId | Long | 否 | 业主ID |
| houseId | Long | 否 | 房屋ID |
| payStatus | Integer | 否 | 缴费状态：0未缴费 1已缴费 2欠费 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "ownerId": 1,
        "houseId": 1,
        "itemId": 1,
        "itemName": "物业费",
        "totalMoney": 225.00,
        "billCycle": "2026-07",
        "payStatus": 0,
        "payTime": null,
        "payWay": null,
        "createTime": "2026-07-12T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 4.2.3 获取账单详情

```
GET /api/fee/record/{id}
```

#### 4.2.4 确认缴费

```
PUT /api/fee/record/pay
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 账单ID |
| payWay | String | 是 | 支付方式：现金/转账/微信/支付宝 |

#### 4.2.5 欠费统计

```
GET /api/fee/record/statistics
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ownerId | Long | 否 | 业主ID |
| houseId | Long | 否 | 房屋ID |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "arrearsList": [
      {
        "id": 1,
        "ownerId": 1,
        "houseId": 1,
        "itemId": 1,
        "totalMoney": 225.00,
        "billCycle": "2026-07",
        "payStatus": 2,
        "createTime": "2026-07-12T10:00:00"
      }
    ],
    "totalArrears": 225.00,
    "count": 1
  }
}
```

---

## 五、待开发模块

### 5.1 报修维修模块（待开发）

- 报修记录管理
- 报修处理流程
- 完成评价

### 5.2 投诉建议模块（待开发）

- 投诉建议提交
- 处理回复

### 5.3 公告管理模块（待开发）

- 公告发布
- 公告查看

### 5.4 设备巡检模块（待开发）

- 巡检计划
- 巡检记录

### 5.5 数据统计模块（待开发）

- 收费统计
- 报修统计
- 图表展示
