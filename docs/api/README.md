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

## 五、报修维修模块

### 5.1 报修记录管理

#### 5.1.1 新增报修记录

```
POST /api/repair/record
```

**请求参数：**

```json
{
  "ownerId": 1,
  "houseId": 1,
  "repairType": "水电",
  "content": "水管漏水",
  "imgUrl": ""
}
```

#### 5.1.2 更新报修记录

```
PUT /api/repair/record
```

**请求参数：**

```json
{
  "id": 1,
  "ownerId": 1,
  "houseId": 1,
  "repairType": "水电",
  "content": "水管漏水",
  "imgUrl": ""
}
```

#### 5.1.3 删除报修记录

```
DELETE /api/repair/record/{id}
```

#### 5.1.4 获取报修记录详情

```
GET /api/repair/record/{id}
```

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "ownerId": 1,
    "houseId": 1,
    "repairType": "水电",
    "content": "水管漏水",
    "imgUrl": "",
    "status": 0,
    "handleUser": null,
    "handleResult": null,
    "finishTime": null,
    "rating": null,
    "createTime": "2026-07-13T10:00:00"
  }
}
```

#### 5.1.5 获取报修记录分页列表

```
GET /api/repair/record/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| ownerId | Long | 否 | 业主ID |
| houseId | Long | 否 | 房屋ID |
| status | Integer | 否 | 状态：0待处理 1处理中 2已完成 3驳回 |

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
        "repairType": "水电",
        "content": "水管漏水",
        "status": 0,
        "handleUser": null,
        "rating": null,
        "createTime": "2026-07-13T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 5.1.6 更新报修状态

```
PUT /api/repair/record/status
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 报修记录ID |
| status | Integer | 是 | 状态：0待处理 1处理中 2已完成 3驳回 |
| handleUser | String | 否 | 处理人 |
| handleResult | String | 否 | 处理结果 |

#### 5.1.7 更新报修评分

```
PUT /api/repair/record/rating
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 报修记录ID |
| rating | Integer | 是 | 评分：1-5 |

---

## 六、投诉建议模块

### 6.1 投诉建议管理

#### 6.1.1 新增投诉建议

```
POST /api/complaint/suggest
```

**请求参数：**

```json
{
  "ownerId": 1,
  "houseId": 1,
  "type": "投诉",
  "title": "噪音扰民",
  "content": "楼上住户深夜制造噪音"
}
```

#### 6.1.2 更新投诉建议

```
PUT /api/complaint/suggest
```

**请求参数：**

```json
{
  "id": 1,
  "ownerId": 1,
  "houseId": 1,
  "type": "投诉",
  "title": "噪音扰民",
  "content": "楼上住户深夜制造噪音"
}
```

#### 6.1.3 删除投诉建议

```
DELETE /api/complaint/suggest/{id}
```

#### 6.1.4 获取投诉建议详情

```
GET /api/complaint/suggest/{id}
```

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "ownerId": 1,
    "houseId": 1,
    "type": "投诉",
    "title": "噪音扰民",
    "content": "楼上住户深夜制造噪音",
    "status": 0,
    "handleUser": null,
    "handleResult": null,
    "finishTime": null,
    "rating": null,
    "createTime": "2026-07-13T10:00:00"
  }
}
```

#### 6.1.5 获取投诉建议分页列表

```
GET /api/complaint/suggest/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| ownerId | Long | 否 | 业主ID |
| houseId | Long | 否 | 房屋ID |
| status | Integer | 否 | 状态：0待受理 1已受理 2处理中 3已完成 4已评价 5已驳回 |
| type | String | 否 | 类型：投诉/建议/其他 |

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
        "type": "投诉",
        "title": "噪音扰民",
        "status": 0,
        "handleUser": null,
        "rating": null,
        "createTime": "2026-07-13T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 6.1.6 更新投诉建议状态

```
PUT /api/complaint/suggest/status
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 投诉建议ID |
| status | Integer | 是 | 状态：0待受理 1已受理 2处理中 3已完成 4已评价 5已驳回 |
| handleUser | String | 否 | 处理人 |
| handleResult | String | 否 | 处理结果 |

#### 6.1.7 更新投诉建议评分

```
PUT /api/complaint/suggest/rating
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 投诉建议ID |
| rating | Integer | 是 | 评分：1-5 |

---

## 七、公告管理模块

### 7.1 公告管理

#### 7.1.1 新增公告

```
POST /api/announcement
```

**请求参数：**

```json
{
  "title": "小区停水通知",
  "content": "因水管维修，将于明日停水一天",
  "type": "通知",
  "publishTime": "2026-07-14T08:00:00",
  "expireTime": "2026-07-15T18:00:00",
  "isTop": 0
}
```

#### 7.1.2 更新公告

```
PUT /api/announcement
```

**请求参数：**

```json
{
  "id": 1,
  "title": "小区停水通知",
  "content": "因水管维修，将于明日停水一天",
  "type": "通知",
  "publishTime": "2026-07-14T08:00:00",
  "expireTime": "2026-07-15T18:00:00",
  "isTop": 0
}
```

#### 7.1.3 删除公告

```
DELETE /api/announcement/{id}
```

#### 7.1.4 获取公告详情

```
GET /api/announcement/{id}
```

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "小区停水通知",
    "content": "因水管维修，将于明日停水一天",
    "type": "通知",
    "status": 2,
    "isTop": 0,
    "publishTime": "2026-07-14T08:00:00",
    "expireTime": "2026-07-15T18:00:00",
    "createUser": "root",
    "createTime": "2026-07-13T10:00:00",
    "updateTime": "2026-07-13T10:00:00"
  }
}
```

#### 7.1.5 获取公告分页列表

```
GET /api/announcement/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| type | String | 否 | 类型：通知/活动/紧急 |
| status | Integer | 否 | 状态：0草稿 1预发布 2已发布 3已过期 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "小区停水通知",
        "type": "通知",
        "status": 2,
        "isTop": 0,
        "createTime": "2026-07-13T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1
  }
}
```

#### 7.1.6 更新公告状态

```
PUT /api/announcement/status
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 公告ID |
| status | Integer | 是 | 状态：0草稿 1预发布 2已发布 3已过期 |

#### 7.1.7 更新公告置顶状态

```
PUT /api/announcement/top
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 公告ID |
| isTop | Integer | 是 | 是否置顶：0否 1是 |

---

## 八、设备巡检模块

### 8.1 设备分类管理

#### 8.1.1 新增设备分类

```
POST /api/equipment/category
```

**请求参数：**

```json
{
  "categoryName": "电梯",
  "description": "电梯设备",
  "sortOrder": 1
}
```

#### 8.1.2 更新设备分类

```
PUT /api/equipment/category
```

**请求参数：**

```json
{
  "id": 1,
  "categoryName": "电梯",
  "description": "电梯设备",
  "sortOrder": 1
}
```

#### 8.1.3 删除设备分类

```
DELETE /api/equipment/category/{id}
```

#### 8.1.4 获取设备分类详情

```
GET /api/equipment/category/{id}
```

#### 8.1.5 获取设备分类列表

```
GET /api/equipment/category/list
```

### 8.2 设备管理

#### 8.2.1 新增设备

```
POST /api/equipment
```

**请求参数：**

```json
{
  "equipmentName": "1号电梯",
  "equipmentCode": "EL-001",
  "categoryId": 1,
  "location": "A栋1单元",
  "maintenanceUser": "张三",
  "installDate": "2024-01-01",
  "warrantyDate": "2026-01-01"
}
```

#### 8.2.2 更新设备

```
PUT /api/equipment
```

#### 8.2.3 删除设备

```
DELETE /api/equipment/{id}
```

#### 8.2.4 获取设备详情

```
GET /api/equipment/{id}
```

#### 8.2.5 分页查询设备

```
GET /api/equipment/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| categoryId | Long | 否 | 设备分类ID |
| status | Integer | 否 | 状态：0正常 1维修中 2停用 |

#### 8.2.6 更新设备状态

```
PUT /api/equipment/status
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 设备ID |
| status | Integer | 是 | 状态：0正常 1维修中 2停用 |

### 8.3 巡检计划管理

#### 8.3.1 新增巡检计划

```
POST /api/inspection/plan
```

**请求参数：**

```json
{
  "planName": "每日电梯巡检",
  "planType": 0,
  "planDate": "2026-07-14",
  "equipmentIds": "1,2,3",
  "inspectorIds": "1,2"
}
```

#### 8.3.2 更新巡检计划

```
PUT /api/inspection/plan
```

#### 8.3.3 删除巡检计划

```
DELETE /api/inspection/plan/{id}
```

#### 8.3.4 获取巡检计划详情

```
GET /api/inspection/plan/{id}
```

#### 8.3.5 分页查询巡检计划

```
GET /api/inspection/plan/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| status | Integer | 否 | 状态：0待执行 1执行中 2已完成 3已取消 |

#### 8.3.6 更新巡检计划状态

```
PUT /api/inspection/plan/status
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 计划ID |
| status | Integer | 是 | 状态：0待执行 1执行中 2已完成 3已取消 |

#### 8.3.7 手动生成周期计划

```
POST /api/inspection/plan/generate
```

### 8.4 巡检记录管理

#### 8.4.1 新增巡检记录

```
POST /api/inspection/record
```

**请求参数：**

```json
{
  "planId": 1,
  "equipmentId": 1,
  "inspectorId": 1,
  "result": 0,
  "faultDesc": "",
  "repairSuggestion": "",
  "budget": 0,
  "duration": ""
}
```

#### 8.4.2 更新巡检记录

```
PUT /api/inspection/record
```

#### 8.4.3 删除巡检记录

```
DELETE /api/inspection/record/{id}
```

#### 8.4.4 获取巡检记录详情

```
GET /api/inspection/record/{id}
```

#### 8.4.5 分页查询巡检记录

```
GET /api/inspection/record/page
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| equipmentId | Long | 否 | 设备ID |
| result | Integer | 否 | 巡检结果：0正常 1一般异常 2严重异常 |

#### 8.4.6 按设备查询历史巡检记录

```
GET /api/inspection/record/equipment/{id}
```

---

## 九、数据统计模块

### 9.1 收费统计

#### 9.1.1 按月统计收费情况

```
GET /api/statistics/fee/monthly
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| year | Integer | 否 | 年份，默认当前年份 |
| month | Integer | 否 | 月份，默认当前月份 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalAmount": 125000.00,
    "paidAmount": 98000.00,
    "unpaidAmount": 27000.00,
    "paidRate": 78.4,
    "details": [
      {
        "itemName": "物业费",
        "amount": 85000.00,
        "paidAmount": 68000.00,
        "unpaidAmount": 17000.00
      }
    ]
  }
}
```

#### 9.1.2 按年统计收费情况

```
GET /api/statistics/fee/yearly
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| year | Integer | 否 | 年份，默认当前年份 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalAmount": 1500000.00,
    "paidAmount": 1200000.00,
    "unpaidAmount": 300000.00,
    "paidRate": 80.0,
    "monthlyData": [
      {
        "month": 1,
        "amount": 125000.00,
        "paidAmount": 100000.00
      }
    ]
  }
}
```

### 9.2 报修统计

#### 9.2.1 按月统计报修情况

```
GET /api/statistics/repair/monthly
```

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| year | Integer | 否 | 年份，默认当前年份 |
| month | Integer | 否 | 月份，默认当前月份 |

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 45,
    "completedCount": 40,
    "pendingCount": 5,
    "completionRate": 88.9,
    "averageHandleTime": "2.5天"
  }
}
```

#### 9.2.2 按类型统计报修情况

```
GET /api/statistics/repair/type
```

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "type": "水电",
      "count": 20,
      "percentage": 44.4
    },
    {
      "type": "电梯",
      "count": 15,
      "percentage": 33.3
    }
  ]
}
```

### 9.3 系统概览

#### 9.3.1 获取系统概览数据

```
GET /api/statistics/overview
```

**响应参数：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalBuildings": 10,
    "totalHouses": 500,
    "totalOwners": 450,
    "totalParking": 200,
    "monthlyFeeCollection": 125000.00,
    "pendingRepairs": 5,
    "pendingComplaints": 3
  }
}
```
