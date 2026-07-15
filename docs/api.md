# API 接口文档

## 1. 接口规范

### 1.1 基础信息
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **字符编码**: UTF-8
- **认证方式**: JWT Bearer Token

### 1.2 通用请求头
| Header | 必填 | 说明 |
|--------|------|------|
| Authorization | 是(除登录外) | `Bearer <token>` |
| Content-Type | 是 | `application/json` |

### 1.3 通用响应格式

#### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

#### 分页响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

#### 错误响应
```json
{
  "code": 400,
  "message": "参数错误: 用户名不能为空",
  "data": null
}
```

### 1.4 通用错误码
| Code | Message | 说明 |
|------|---------|------|
| 200 | 操作成功 | 成功 |
| 400 | 参数错误 | 请求参数校验失败 |
| 401 | 未授权 | Token 失效/未登录 |
| 403 | 禁止访问 | 无权限 |
| 404 | 资源不存在 | 请求资源不存在 |
| 500 | 服务器内部错误 | 系统异常 |
| 501 | 业务异常 | 业务逻辑错误 |

### 1.5 分页参数
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| current | Integer | 否 | 1 | 当前页码 |
| size | Integer | 否 | 10 | 每页大小，最大100 |

---

## 2. 认证模块

### 2.1 用户登录
> **POST** `/api/auth/login`

**请求参数**
```json
{
  "username": "admin",
  "password": "123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**响应示例**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "超级管理员",
      "phone": "13800138000",
      "avatar": null,
      "userType": 1,
      "status": 1,
      "roles": ["super_admin"],
      "permissions": ["system:user:list", "system:user:add", ...]
    }
  }
}
```

### 2.2 用户退出
> **POST** `/api/auth/logout`

**请求头**
```
Authorization: Bearer <token>
```

**响应示例**
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

### 2.3 获取当前用户信息
> **GET** `/api/auth/me`

**请求头**
```
Authorization: Bearer <token>
```

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "超级管理员",
    "phone": "13800138000",
    "avatar": null,
    "userType": 1,
    "status": 1,
    "roles": ["super_admin"],
    "permissions": ["system:user:list", "system:user:add", ...]
  }
}
```

---

## 3. 系统管理模块

### 3.1 用户管理

#### 3.1.1 分页查询用户
> **GET** `/api/system/users/page`

**权限**: `system:user:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| username | String | 否 | 用户名模糊查询 |
| realName | String | 否 | 真实姓名模糊查询 |
| phone | String | 否 | 手机号模糊查询 |
| userType | Integer | 否 | 用户类型 |
| status | Integer | 否 | 状态 |

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "realName": "超级管理员",
        "phone": "13800138000",
        "avatar": null,
        "userType": 1,
        "status": 1,
        "createTime": "2024-01-01 10:00:00",
        "roleNames": ["超级管理员"]
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 3.1.2 新增用户
> **POST** `/api/system/users`

**权限**: `system:user:add`

**请求参数**
```json
{
  "username": "user001",
  "password": "123456",
  "realName": "张三",
  "phone": "13800138001",
  "userType": 2,
  "status": 1,
  "roleIds": [2]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名，唯一 |
| password | String | 是 | 密码，前端传明文，后端加密 |
| realName | String | 是 | 真实姓名 |
| phone | String | 否 | 手机号 |
| userType | Integer | 是 | 用户类型：1超管 2物业 3业主 |
| status | Integer | 否 | 状态：0禁用 1启用，默认1 |
| roleIds | List<Long> | 否 | 角色ID列表 |

#### 3.1.3 修改用户
> **PUT** `/api/system/users/{id}`

**权限**: `system:user:edit`

**请求参数** (同新增，不含password)

#### 3.1.4 删除用户
> **DELETE** `/api/system/users/{id}`

**权限**: `system:user:delete`

#### 3.1.5 重置密码
> **PUT** `/api/system/users/{id}/reset-pwd`

**权限**: `system:user:resetPwd`

**请求参数**
```json
{
  "newPassword": "123456"
}
```

#### 3.1.6 分配角色
> **PUT** `/api/system/users/{id}/roles`

**权限**: `system:user:role`

**请求参数**
```json
{
  "roleIds": [2, 3]
}
```

---

### 3.2 角色管理

#### 3.2.1 分页查询角色
> **GET** `/api/system/roles/page`

**权限**: `system:role:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| roleName | String | 否 | 角色名称模糊查询 |
| roleKey | String | 否 | 权限标识模糊查询 |
| status | Integer | 否 | 状态 |

#### 3.2.2 获取全部角色列表
> **GET** `/api/system/roles/all`

**权限**: `system:role:list`

#### 3.2.3 新增角色
> **POST** `/api/system/roles`

**权限**: `system:role:add`

**请求参数**
```json
{
  "roleName": "测试角色",
  "roleKey": "test_role",
  "remark": "测试用角色",
  "menuIds": [1, 2, 3]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleName | String | 是 | 角色名称 |
| roleKey | String | 是 | 权限标识，唯一 |
| remark | String | 否 | 备注 |
| menuIds | List<Long> | 否 | 菜单ID列表 |

#### 3.2.4 修改角色
> **PUT** `/api/system/roles/{id}`

**权限**: `system:role:edit`

#### 3.2.5 删除角色
> **DELETE** `/api/system/roles/{id}`

**权限**: `system:role:delete`

#### 3.2.6 分配菜单权限
> **PUT** `/api/system/roles/{id}/menus`

**权限**: `system:role:menu`

**请求参数**
```json
{
  "menuIds": [1, 2, 3, 4, 5]
}
```

---

### 3.3 菜单管理

#### 3.3.1 获取菜单树
> **GET** `/api/system/menus/tree`

**权限**: `system:menu:list`

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "menuName": "系统管理",
      "path": "/system",
      "component": "Layout",
      "perms": "",
      "menuType": 0,
      "sort": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "menuName": "用户管理",
          "path": "/system/user",
          "component": "system/user/index",
          "perms": "system:user:list",
          "menuType": 1,
          "sort": 1,
          "status": 1,
          "children": [
            {
              "id": 10,
              "parentId": 2,
              "menuName": "用户新增",
              "path": "",
              "component": "",
              "perms": "system:user:add",
              "menuType": 2,
              "sort": 1,
              "status": 1,
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

#### 3.3.2 新增菜单
> **POST** `/api/system/menus`

**权限**: `system:menu:add`

**请求参数**
```json
{
  "parentId": 1,
  "menuName": "新菜单",
  "path": "/new",
  "component": "new/index",
  "perms": "system:new:list",
  "menuType": 1,
  "sort": 1,
  "status": 1
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| parentId | Long | 是 | 父菜单ID，顶级为0 |
| menuName | String | 是 | 菜单名称 |
| path | String | 否 | 路由路径 |
| component | String | 否 | 组件路径 |
| perms | String | 否 | 权限标识 |
| menuType | Integer | 是 | 类型：0目录 1菜单 2按钮 |
| sort | Integer | 否 | 排序 |
| status | Integer | 否 | 状态：0禁用 1启用 |

#### 3.3.3 修改菜单
> **PUT** `/api/system/menus/{id}`

**权限**: `system:menu:edit`

#### 3.3.4 删除菜单
> **DELETE** `/api/system/menus/{id}`

**权限**: `system:menu:delete`

---

## 4. 小区基础模块

### 4.1 楼栋管理

#### 4.1.1 分页查询楼栋
> **GET** `/api/community/buildings/page`

**权限**: `community:building:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| buildingNo | String | 否 | 楼栋编号模糊查询 |

#### 4.1.2 获取楼栋列表
> **GET** `/api/community/buildings/list`

**权限**: `community:building:list`

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {"id": 1, "buildingNo": "1号楼", "floorCount": 18, "totalHouse": 108},
    {"id": 2, "buildingNo": "2号楼", "floorCount": 18, "totalHouse": 108}
  ]
}
```

#### 4.1.3 新增楼栋
> **POST** `/api/community/buildings`

**权限**: `community:building:add`

**请求参数**
```json
{
  "buildingNo": "3号楼",
  "floorCount": 20,
  "totalHouse": 120,
  "buildYear": 2020,
  "remark": "新建楼栋"
}
```

#### 4.1.4 修改楼栋
> **PUT** `/api/community/buildings/{id}`

**权限**: `community:building:edit`

#### 4.1.5 删除楼栋
> **DELETE** `/api/community/buildings/{id}`

**权限**: `community:building:delete`

#### 4.1.6 楼栋详情
> **GET** `/api/community/buildings/{id}`

**权限**: `community:building:query`

---

### 4.2 房屋管理

#### 4.2.1 分页查询房屋
> **GET** `/api/community/houses/page`

**权限**: `community:house:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| buildingId | Long | 否 | 楼栋ID |
| roomNo | String | 否 | 房间号模糊查询 |
| houseStatus | Integer | 否 | 房屋状态 |
| ownerId | Long | 否 | 业主ID |

#### 4.2.2 根据楼栋查询房屋
> **GET** `/api/community/houses/by-building/{buildingId}`

**权限**: `community:house:list`

#### 4.2.3 新增房屋
> **POST** `/api/community/houses`

**权限**: `community:house:add`

**请求参数**
```json
{
  "buildingId": 1,
  "roomNo": "101",
  "area": 89.5,
  "houseType": "2室1厅",
  "houseStatus": 0,
  "ownerId": null,
  "remark": ""
}
```

#### 4.2.4 修改房屋
> **PUT** `/api/community/houses/{id}`

**权限**: `community:house:edit`

#### 4.2.5 删除房屋
> **DELETE** `/api/community/houses/{id}`

**权限**: `community:house:delete`

---

### 4.3 业主管理

#### 4.3.1 分页查询业主
> **GET** `/api/community/owners/page`

**权限**: `community:owner:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| name | String | 否 | 姓名模糊查询 |
| phone | String | 否 | 电话模糊查询 |
| ownerType | Integer | 否 | 业主类型 |
| status | Integer | 否 | 状态 |

#### 4.3.2 新增业主
> **POST** `/api/community/owners`

**权限**: `community:owner:add`

**请求参数**
```json
{
  "userId": null,
  "name": "李四",
  "phone": "13900139000",
  "idCard": "110101199001011234",
  "idCardFront": "url1",
  "idCardBack": "url2",
  "ownerType": 1,
  "status": 1,
  "remark": ""
}
```

#### 4.3.3 修改业主
> **PUT** `/api/community/owners/{id}`

**权限**: `community:owner:edit`

#### 4.3.4 删除业主
> **DELETE** `/api/community/owners/{id}`

**权限**: `community:owner:delete`

#### 4.3.5 业主关联用户账号
> **PUT** `/api/community/owners/{id}/bind-user`

**权限**: `community:owner:bind`

**请求参数**
```json
{
  "userId": 5
}
```

---

### 4.4 车位管理

#### 4.4.1 分页查询车位
> **GET** `/api/community/parkings/page`

**权限**: `community:parking:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| parkingNo | String | 否 | 车位编号模糊查询 |
| parkingType | Integer | 否 | 车位类型 |
| status | Integer | 否 | 状态 |
| ownerId | Long | 否 | 业主ID |

#### 4.4.2 车位统计
> **GET** `/api/community/parkings/stats`

**权限**: `community:parking:list`

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 200,
    "free": 50,
    "rented": 120,
    "sold": 20,
    "repairing": 10
  }
}
```

#### 4.4.3 新增车位
> **POST** `/api/community/parkings`

**权限**: `community:parking:add`

**请求参数**
```json
{
  "parkingNo": "A-001",
  "parkingType": 2,
  "status": 0,
  "ownerId": null,
  "rentPrice": 300.00,
  "sellPrice": 100000.00,
  "remark": "地下车位"
}
```

#### 4.4.4 修改车位
> **PUT** `/api/community/parkings/{id}`

**权限**: `community:parking:edit`

#### 4.4.5 删除车位
> **DELETE** `/api/community/parkings/{id}`

**权限**: `community:parking:delete`

---

## 5. 收费管理模块

### 5.1 收费项目

#### 5.1.1 分页查询
> **GET** `/api/fee/items/page`

**权限**: `fee:item:list`

#### 5.1.2 列表查询
> **GET** `/api/fee/items/list`

**权限**: `fee:item:list`

#### 5.1.3 新增
> **POST** `/api/fee/items`

**权限**: `fee:item:add`

**请求参数**
```json
{
  "itemName": "物业费",
  "itemType": 1,
  "unitPrice": 2.50,
  "unit": "元/㎡/月",
  "cycleType": 1,
  "description": "住宅物业管理费",
  "status": 1
}
```

#### 5.1.4 修改
> **PUT** `/api/fee/items/{id}`

**权限**: `fee:item:edit`

#### 5.1.5 删除
> **DELETE** `/api/fee/items/{id}`

**权限**: `fee:item:delete`

---

### 5.2 收费通知

#### 5.2.1 分页查询
> **GET** `/api/fee/notices/page`

**权限**: `fee:notice:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| noticeTitle | String | 否 | 标题模糊查询 |
| noticeType | Integer | 否 | 通知类型 |
| sendStatus | Integer | 否 | 发送状态 |

#### 5.2.2 生成收费通知
> **POST** `/api/fee/notices/generate`

**权限**: `fee:notice:generate`

**请求参数**
```json
{
  "itemId": 1,
  "startDate": "2024-02-01",
  "endDate": "2024-02-29",
  "buildingIds": [1, 2],
  "noticeType": 1
}
```

#### 5.2.3 批量发布通知
> **POST** `/api/fee/notices/batch-publish`

**权限**: `fee:notice:publish`

**请求参数**
```json
{
  "ids": [1, 2, 3]
}
```

---

### 5.3 缴费记录

#### 5.3.1 分页查询
> **GET** `/api/fee/records/page`

**权限**: `fee:record:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| ownerId | Long | 否 | 业主ID |
| houseId | Long | 否 | 房屋ID |
| itemId | Long | 否 | 收费项目ID |
| status | Integer | 否 | 缴费状态 |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

#### 5.3.2 缴费
> **POST** `/api/fee/records/pay`

**权限**: `fee:record:pay`

**请求参数**
```json
{
  "recordId": 1,
  "paidAmount": 500.00,
  "payType": 2,
  "discountAmount": 0,
  "remark": "微信支付"
}
```

#### 5.3.3 退费
> **POST** `/api/fee/records/{id}/refund`

**权限**: `fee:record:refund`

**请求参数**
```json
{
  "refundAmount": 100.00,
  "refundReason": "多缴费用"
}
```

#### 5.3.4 导出缴费记录
> **GET** `/api/fee/records/export`

**权限**: `fee:record:export`

---

## 6. 设备管理模块

### 6.1 设备分类

#### 6.1.1 树形列表
> **GET** `/api/equipment/categories/tree`

**权限**: `equipment:category:list`

#### 6.1.2 列表查询
> **GET** `/api/equipment/categories/list`

**权限**: `equipment:category:list`

#### 6.1.3 新增
> **POST** `/api/equipment/categories`

**权限**: `equipment:category:add`

**请求参数**
```json
{
  "categoryName": "水泵设备",
  "parentId": 0,
  "sort": 1,
  "status": 1
}
```

#### 6.1.4 修改
> **PUT** `/api/equipment/categories/{id}`

**权限**: `equipment:category:edit`

#### 6.1.5 删除
> **DELETE** `/api/equipment/categories/{id}`

**权限**: `equipment:category:delete`

---

### 6.2 设备台账

#### 6.2.1 分页查询
> **GET** `/api/equipment/equipments/page`

**权限**: `equipment:equipment:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| equipmentNo | String | 否 | 设备编号模糊查询 |
| equipmentName | String | 否 | 设备名称模糊查询 |
| categoryId | Long | 否 | 分类ID |
| buildingId | Long | 否 | 楼栋ID |
| status | Integer | 否 | 设备状态 |

#### 6.2.2 设备统计
> **GET** `/api/equipment/equipments/stats`

**权限**: `equipment:equipment:list`

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 150,
    "normal": 120,
    "fault": 10,
    "maintaining": 5,
    "stopped": 10,
    "scrapped": 5
  }
}
```

#### 6.2.3 新增设备
> **POST** `/api/equipment/equipments`

**权限**: `equipment:equipment:add`

**请求参数**
```json
{
  "equipmentNo": "SB-001",
  "equipmentName": "一号水泵",
  "categoryId": 1,
  "brand": "南方泵业",
  "model": "CDLF32-20",
  "spec": "流量32m³/h，扬程200m",
  "location": "地下室水泵房",
  "buildingId": 1,
  "floor": "B1",
  "installDate": "2023-01-15",
  "warrantyEndDate": "2026-01-15",
  "status": 1,
  "qrCode": "url",
  "remark": ""
}
```

#### 6.2.4 修改设备
> **PUT** `/api/equipment/equipments/{id}`

**权限**: `equipment:equipment:edit`

#### 6.2.5 删除设备
> **DELETE** `/api/equipment/equipments/{id}`

**权限**: `equipment:equipment:delete`

---

### 6.3 维修记录

#### 6.3.1 分页查询
> **GET** `/api/equipment/maintenances/page`

**权限**: `equipment:maintenance:list`

#### 6.3.2 新增维修记录
> **POST** `/api/equipment/maintenances`

**权限**: `equipment:maintenance:add`

**请求参数**
```json
{
  "equipmentId": 1,
  "maintenanceType": 3,
  "maintenanceContent": "更换水泵密封件",
  "maintenancePersonnelId": 2,
  "startTime": "2024-01-15 09:00:00",
  "endTime": "2024-01-15 11:30:00",
  "cost": 500.00,
  "partsReplaced": "密封圈、O型圈",
  "nextMaintenanceDate": "2024-04-15",
  "status": 2,
  "remark": "维修完成，运行正常"
}
```

#### 6.3.3 完成维修
> **PUT** `/api/equipment/maintenances/{id}/complete`

**权限**: `equipment:maintenance:complete`

#### 6.3.4 修改维修记录
> **PUT** `/api/equipment/maintenances/{id}`

**权限**: `equipment:maintenance:edit`

#### 6.3.5 删除维修记录
> **DELETE** `/api/equipment/maintenances/{id}`

**权限**: `equipment:maintenance:delete`

---

## 7. 报修管理

### 7.1 分页查询报修
> **GET** `/api/repair/records/page`

**权限**: `repair:record:list`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| ownerId | Long | 否 | 业主ID |
| houseId | Long | 否 | 房屋ID |
| status | Integer | 否 | 报修状态 |
| priority | Integer | 否 | 优先级 |
| handlerId | Long | 否 | 处理人ID |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

### 7.2 我的报修 (业主端)
> **GET** `/api/repair/records/my`

**权限**: `repair:record:my`

### 7.3 新增报修
> **POST** `/api/repair/records`

**权限**: `repair:record:add`

**请求参数**
```json
{
  "ownerId": 1,
  "houseId": 1,
  "repairType": "水电",
  "repairContent": "厨房水龙头漏水",
  "repairImages": ["url1", "url2"],
  "priority": 1
}
```

### 7.4 受理报修
> **PUT** `/api/repair/records/{id}/accept`

**权限**: `repair:record:accept`

**请求参数**
```json
{
  "handlerId": 2
}
```

### 7.5 处理完成
> **PUT** `/api/repair/records/{id}/complete`

**权限**: `repair:record:complete`

**请求参数**
```json
{
  "handleContent": "已更换水龙头密封垫，漏水问题解决",
  "handleImages": ["url3"]
}
```

### 7.6 评价报修
> **POST** `/api/repair/records/{id}/evaluate`

**权限**: `repair:record:evaluate`

**请求参数**
```json
{
  "evaluateScore": 5,
  "evaluateContent": "处理及时，服务态度好"
}
```

---

## 8. 投诉建议

### 8.1 分页查询
> **GET** `/api/complaint/suggests/page`

**权限**: `complaint:suggest:list`

### 8.2 我的投诉 (业主端)
> **GET** `/api/complaint/suggests/my`

**权限**: `complaint:suggest:my`

### 8.3 新增投诉
> **POST** `/api/complaint/suggests`

**权限**: `complaint:suggest:add`

**请求参数**
```json
{
  "ownerId": 1,
  "houseId": 1,
  "type": 1,
  "category": "噪音扰民",
  "content": "楼上深夜装修噪音大",
  "images": ["url1"],
  "priority": 2,
  "isAnonymous": 0
}
```

### 8.4 回复处理
> **PUT** `/api/complaint/suggests/{id}/reply`

**权限**: `complaint:suggest:reply`

**请求参数**
```json
{
  "handlerId": 2,
  "handleContent": "已联系楼上业主，要求晚上10点后停止装修"
}
```

---

## 9. 公告通知

### 9.1 分页查询 (管理端)
> **GET** `/api/announcement/announcements/page`

**权限**: `announcement:list`

### 9.2 发布列表 (业主端)
> **GET** `/api/announcement/announcements/published`

**权限**: 无需认证

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 页码 |
| size | Integer | 否 | 每页大小 |
| type | Integer | 否 | 公告类型 |

### 9.3 新增公告
> **POST** `/api/announcement/announcements`

**权限**: `announcement:add`

**请求参数**
```json
{
  "title": "关于春节放假安排的通知",
  "content": "尊敬的业主：春节期间...",
  "type": 1,
  "coverImage": "url",
  "isTop": 0,
  "publishStatus": 0,
  "creatorId": 1
}
```

### 9.4 修改公告
> **PUT** `/api/announcement/announcements/{id}`

**权限**: `announcement:edit`

### 9.5 发布/下架
> **PUT** `/api/announcement/announcements/{id}/publish`

**权限**: `announcement:publish`

**请求参数**
```json
{
  "publishStatus": 1
}
```

### 9.6 置顶/取消置顶
> **PUT** `/api/announcement/announcements/{id}/top`

**权限**: `announcement:top`

**请求参数**
```json
{
  "isTop": 1,
  "topExpireTime": "2024-02-20 23:59:59"
}
```

### 9.7 阅读公告
> **POST** `/api/announcement/announcements/{id}/read`

**权限**: `announcement:read`

### 9.8 公告详情
> **GET** `/api/announcement/announcements/{id}`

**权限**: `announcement:query` (管理端) / 无需认证 (发布状态为1时)

---

## 10. 统计分析

### 10.1 收费统计
> **GET** `/api/statistics/fee`

**权限**: `statistics:fee`

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | String | 否 | 开始日期，默认本月1号 |
| endDate | String | 否 | 结束日期，默认今天 |

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalAmount": 150000.00,
    "paidAmount": 120000.00,
    "unpaidAmount": 30000.00,
    "overdueAmount": 5000.00,
    "collectionRate": 80.0,
    "byItem": [
      {"itemName": "物业费", "totalAmount": 100000, "paidAmount": 85000},
      {"itemName": "车位费", "totalAmount": 30000, "paidAmount": 25000},
      {"itemName": "水费", "totalAmount": 20000, "paidAmount": 10000}
    ],
    "byMonth": [
      {"month": "2024-01", "totalAmount": 50000, "paidAmount": 45000},
      {"month": "2024-02", "totalAmount": 50000, "paidAmount": 40000}
    ]
  }
}
```

### 10.2 报修统计
> **GET** `/api/statistics/repair`

**权限**: `statistics:repair`

### 10.3 投诉统计
> **GET** `/api/statistics/complaint`

**权限**: `statistics:complaint`

### 10.4 设备统计
> **GET** `/api/statistics/equipment`

**权限**: `statistics:equipment`

### 10.5 业主统计
> **GET** `/api/statistics/owner`

**权限**: `statistics:owner`

---

## 11. 权限标识汇总表

| 模块 | 权限标识 | 说明 |
|------|----------|------|
| **系统-用户** | system:user:list | 用户列表 |
| | system:user:add | 用户新增 |
| | system:user:edit | 用户修改 |
| | system:user:delete | 用户删除 |
| | system:user:resetPwd | 重置密码 |
| | system:user:role | 分配角色 |
| **系统-角色** | system:role:list | 角色列表 |
| | system:role:add | 角色新增 |
| | system:role:edit | 角色修改 |
| | system:role:delete | 角色删除 |
| | system:role:menu | 分配菜单 |
| **系统-菜单** | system:menu:list | 菜单列表 |
| | system:menu:add | 菜单新增 |
| | system:menu:edit | 菜单修改 |
| | system:menu:delete | 菜单删除 |
| **小区-楼栋** | community:building:list | 楼栋列表 |
| | community:building:add | 楼栋新增 |
| | community:building:edit | 楼栋修改 |
| | community:building:delete | 楼栋删除 |
| | community:building:query | 楼栋详情 |
| **小区-房屋** | community:house:list | 房屋列表 |
| | community:house:add | 房屋新增 |
| | community:house:edit | 房屋修改 |
| | community:house:delete | 房屋删除 |
| **小区-业主** | community:owner:list | 业主列表 |
| | community:owner:add | 业主新增 |
| | community:owner:edit | 业主修改 |
| | community:owner:delete | 业主删除 |
| | community:owner:bind | 关联用户 |
| **小区-车位** | community:parking:list | 车位列表 |
| | community:parking:add | 车位新增 |
| | community:parking:edit | 车位修改 |
| | community:parking:delete | 车位删除 |
| **收费-项目** | fee:item:list | 项目列表 |
| | fee:item:add | 项目新增 |
| | fee:item:edit | 项目修改 |
| | fee:item:delete | 项目删除 |
| **收费-通知** | fee:notice:list | 通知列表 |
| | fee:notice:generate | 生成通知 |
| | fee:notice:publish | 发布通知 |
| **收费-记录** | fee:record:list | 记录列表 |
| | fee:record:pay | 缴费 |
| | fee:record:refund | 退费 |
| | fee:record:export | 导出 |
| **设备-分类** | equipment:category:list | 分类列表 |
| | equipment:category:add | 分类新增 |
| | equipment:category:edit | 分类修改 |
| | equipment:category:delete | 分类删除 |
| **设备-台账** | equipment:equipment:list | 台账列表 |
| | equipment:equipment:add | 台账新增 |
| | equipment:equipment:edit | 台账修改 |
| | equipment:equipment:delete | 台账删除 |
| **设备-维修** | equipment:maintenance:list | 维修列表 |
| | equipment:maintenance:add | 维修新增 |
| | equipment:maintenance:edit | 维修修改 |
| | equipment:maintenance:complete | 完成维修 |
| | equipment:maintenance:delete | 维修删除 |
| **报修** | repair:record:list | 报修列表 |
| | repair:record:my | 我的报修 |
| | repair:record:add | 新增报修 |
| | repair:record:accept | 受理报修 |
| | repair:record:complete | 完成报修 |
| | repair:record:evaluate | 评价报修 |
| **投诉** | complaint:suggest:list | 投诉列表 |
| | complaint:suggest:my | 我的投诉 |
| | complaint:suggest:add | 新增投诉 |
| | complaint:suggest:reply | 回复投诉 |
| **公告** | announcement:list | 公告列表 |
| | announcement:add | 新增公告 |
| | announcement:edit | 修改公告 |
| | announcement:publish | 发布/下架 |
| | announcement:top | 置顶 |
| | announcement:read | 阅读 |
| | announcement:query | 查看详情 |
| **统计** | statistics:fee | 收费统计 |
| | statistics:repair | 报修统计 |
| | statistics:complaint | 投诉统计 |
| | statistics:equipment | 设备统计 |
| | statistics:owner | 业主统计 |

---

*文档版本: v1.0*
*更新时间: 2024-07-14*