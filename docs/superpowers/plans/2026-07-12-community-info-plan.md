# 小区基础信息模块实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现物业管理系统的小区基础信息模块，包括楼栋、房屋、业主、车位的基础数据管理功能

**Architecture:** 采用SpringBoot+MyBatisPlus+MySQL后端架构，Vue3+Element Plus前端架构，前后端分离设计。使用jakarta.validation进行参数校验（兼容Spring Boot 4.x）。

**Tech Stack:** SpringBoot 4.0.8-SNAPSHOT、MyBatis-Plus、MySQL 8.0、JWT、Jakarta Validation、Vue3、Vite、Element Plus

---

## 文件结构规划

### 后端文件结构
```
src/main/java/com/lsy/propertymanagementsystem/
├── entity/
│   ├── CommunityBuilding.java      # 楼栋实体
│   ├── CommunityHouse.java         # 房屋实体
│   ├── CommunityOwner.java         # 业主实体
│   └── CommunityParking.java       # 车位实体
├── mapper/
│   ├── CommunityBuildingMapper.java    # 楼栋Mapper
│   ├── CommunityHouseMapper.java       # 房屋Mapper
│   ├── CommunityOwnerMapper.java       # 业主Mapper
│   └── CommunityParkingMapper.java     # 车位Mapper
├── dto/
│   └── request/
│       ├── BuildingRequest.java     # 楼栋请求
│       ├── HouseRequest.java        # 房屋请求
│       ├── OwnerRequest.java        # 业主请求
│       └── ParkingRequest.java      # 车位请求
├── service/
│   ├── CommunityBuildingService.java     # 楼栋Service接口
│   ├── impl/
│   │   └── CommunityBuildingServiceImpl.java # 楼栋Service实现
│   ├── CommunityHouseService.java        # 房屋Service接口
│   ├── impl/
│   │   └── CommunityHouseServiceImpl.java    # 房屋Service实现
│   ├── CommunityOwnerService.java        # 业主Service接口
│   ├── impl/
│   │   └── CommunityOwnerServiceImpl.java    # 业主Service实现
│   ├── CommunityParkingService.java      # 车位Service接口
│   └── impl/
│       └── CommunityParkingServiceImpl.java  # 车位Service实现
└── controller/
    ├── CommunityBuildingController.java   # 楼栋Controller
    ├── CommunityHouseController.java      # 房屋Controller
    ├── CommunityOwnerController.java      # 业主Controller
    └── CommunityParkingController.java    # 车位Controller
```

### 前端文件结构
```
frontend/
├── src/
│   ├── api/
│   │   ├── building.js              # 楼栋API
│   │   ├── house.js                 # 房屋API
│   │   ├── owner.js                 # 业主API
│   │   └── parking.js               # 车位API
│   └── views/
│       └── community/
│           ├── building/
│           │   └── index.vue        # 楼栋管理
│           ├── house/
│           │   └── index.vue        # 房屋管理
│           ├── owner/
│           │   └── index.vue        # 业主管理
│           └── parking/
│               └── index.vue        # 车位管理
├── src/router/index.js              # 路由配置（需更新）
```

---

## Task 1: 创建数据库表结构

**Files:**
- Create: `sql/community.sql`

- [ ] **Step 1: 创建小区信息数据库脚本**

```sql
-- 小区基础信息模块表结构

-- 创建楼栋表
CREATE TABLE community_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_no VARCHAR(50) NOT NULL COMMENT '楼栋编号',
    floor_count INT COMMENT '总楼层',
    total_house INT COMMENT '总户数',
    build_year INT COMMENT '建成年份',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '楼栋表';

-- 创建房屋表
CREATE TABLE community_house (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_id BIGINT NOT NULL COMMENT '楼栋id',
    room_no VARCHAR(50) NOT NULL COMMENT '房间号',
    area DECIMAL(10,2) COMMENT '房屋面积',
    house_type VARCHAR(50) COMMENT '户型',
    house_status TINYINT NOT NULL DEFAULT 0 COMMENT '房屋状态：0空置 1已入住 2出租',
    owner_id BIGINT COMMENT '业主id',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '房屋表';

-- 创建业主信息表
CREATE TABLE community_owner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '关联系统登录用户id',
    name VARCHAR(50) NOT NULL COMMENT '业主姓名',
    id_card VARCHAR(20) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '联系电话',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    check_in_time DATETIME COMMENT '入住时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '业主信息表';

-- 创建车位表
CREATE TABLE community_parking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parking_no VARCHAR(50) NOT NULL COMMENT '车位编号',
    parking_type TINYINT NOT NULL DEFAULT 0 COMMENT '车位类型：0固定车位 1临时车位',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0空闲 1已租赁',
    owner_id BIGINT COMMENT '所属业主id',
    expire_time DATETIME COMMENT '租赁到期时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '车位表';

-- 插入测试数据：楼栋
INSERT INTO community_building (building_no, floor_count, total_house, build_year, remark) VALUES
('A栋', 18, 72, 2020, 'A栋住宅楼'),
('B栋', 24, 96, 2021, 'B栋住宅楼'),
('C栋', 12, 48, 2019, 'C栋住宅楼');

-- 插入测试数据：房屋
INSERT INTO community_house (building_id, room_no, area, house_type, house_status, remark) VALUES
(1, '101', 89.50, '两室一厅', 0, '首层'),
(1, '102', 120.00, '三室两厅', 1, '首层'),
(1, '201', 89.50, '两室一厅', 0, '二层'),
(2, '101', 95.00, '两室一厅', 1, '首层'),
(2, '102', 130.00, '三室两厅', 2, '首层'),
(3, '101', 75.00, '一室一厅', 0, '首层');

-- 插入测试数据：业主
INSERT INTO community_owner (user_id, name, id_card, phone, emergency_contact, emergency_phone, check_in_time) VALUES
(NULL, '张三', '110101199001011234', '13800138001', '张四', '13800138002', '2021-06-15 10:00:00'),
(NULL, '李四', '110101199202022345', '13900139001', '李五', '13900139002', '2022-03-20 14:30:00'),
(NULL, '王五', '110101199503033456', '13700137001', '王六', '13700137002', '2023-01-10 09:00:00');

-- 更新房屋关联业主
UPDATE community_house SET owner_id = 1 WHERE id = 2;
UPDATE community_house SET owner_id = 2 WHERE id = 4;
UPDATE community_house SET owner_id = 2 WHERE id = 5;

-- 插入测试数据：车位
INSERT INTO community_parking (parking_no, parking_type, status, owner_id, expire_time, remark) VALUES
('A-001', 0, 1, 1, '2025-12-31 23:59:59', 'A栋地下一层'),
('A-002', 0, 0, NULL, NULL, 'A栋地下一层'),
('B-001', 0, 1, 2, '2024-06-30 23:59:59', 'B栋地下一层'),
('B-002', 1, 0, NULL, NULL, 'B栋临时车位');
```

- [ ] **Step 2: 执行SQL脚本**

Run: `mysql -u root -p123456 < sql/community.sql`
Expected: 表创建成功，测试数据插入成功

- [ ] **Step 3: 提交**

```bash
git add sql/community.sql
git commit -m "feat: 创建小区基础信息模块数据库表结构和测试数据"
```

---

## Task 2: 创建后端实体类和Mapper

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/CommunityBuilding.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/CommunityHouse.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/CommunityOwner.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/entity/CommunityParking.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/CommunityBuildingMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/CommunityHouseMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/CommunityOwnerMapper.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/mapper/CommunityParkingMapper.java`

- [ ] **Step 1: 创建楼栋实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_building")
public class CommunityBuilding {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String buildingNo;
    
    private Integer floorCount;
    
    private Integer totalHouse;
    
    private Integer buildYear;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 2: 创建房屋实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("community_house")
public class CommunityHouse {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long buildingId;
    
    private String roomNo;
    
    private BigDecimal area;
    
    private String houseType;
    
    private Integer houseStatus;
    
    private Long ownerId;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 3: 创建业主实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_owner")
public class CommunityOwner {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String idCard;
    
    private String phone;
    
    private String emergencyContact;
    
    private String emergencyPhone;
    
    private LocalDateTime checkInTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 4: 创建车位实体类**

```java
package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_parking")
public class CommunityParking {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String parkingNo;
    
    private Integer parkingType;
    
    private Integer status;
    
    private Long ownerId;
    
    private LocalDateTime expireTime;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 5: 创建楼栋Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.CommunityBuilding;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommunityBuildingMapper extends BaseMapper<CommunityBuilding> {
}
```

- [ ] **Step 6: 创建房屋Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.CommunityHouse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommunityHouseMapper extends BaseMapper<CommunityHouse> {
}
```

- [ ] **Step 7: 创建业主Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.CommunityOwner;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommunityOwnerMapper extends BaseMapper<CommunityOwner> {
}
```

- [ ] **Step 8: 创建车位Mapper**

```java
package com.lsy.propertymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.entity.CommunityParking;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommunityParkingMapper extends BaseMapper<CommunityParking> {
}
```

- [ ] **Step 9: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/entity/ src/main/java/com/lsy/propertymanagementsystem/mapper/
git commit -m "feat: 创建小区基础信息模块实体类和Mapper"
```

---

## Task 3: 创建后端DTO和Service

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/BuildingRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/HouseRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/OwnerRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/dto/request/ParkingRequest.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/CommunityBuildingService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/CommunityBuildingServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/CommunityHouseService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/CommunityHouseServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/CommunityOwnerService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/CommunityOwnerServiceImpl.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/CommunityParkingService.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/service/impl/CommunityParkingServiceImpl.java`

- [ ] **Step 1: 创建楼栋请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class BuildingRequest {
    private Long id;
    
    @NotBlank(message = "楼栋编号不能为空")
    private String buildingNo;
    
    private Integer floorCount;
    
    private Integer totalHouse;
    
    private Integer buildYear;
    
    private String remark;
}
```

- [ ] **Step 2: 创建房屋请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class HouseRequest {
    private Long id;
    
    @NotNull(message = "楼栋id不能为空")
    private Long buildingId;
    
    @NotBlank(message = "房间号不能为空")
    private String roomNo;
    
    private BigDecimal area;
    
    private String houseType;
    
    private Integer houseStatus;
    
    private Long ownerId;
    
    private String remark;
}
```

- [ ] **Step 3: 创建业主请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class OwnerRequest {
    private Long id;
    
    private Long userId;
    
    @NotBlank(message = "业主姓名不能为空")
    private String name;
    
    private String idCard;
    
    private String phone;
    
    private String emergencyContact;
    
    private String emergencyPhone;
    
    private LocalDateTime checkInTime;
}
```

- [ ] **Step 4: 创建车位请求DTO**

```java
package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ParkingRequest {
    private Long id;
    
    @NotBlank(message = "车位编号不能为空")
    private String parkingNo;
    
    private Integer parkingType;
    
    private Integer status;
    
    private Long ownerId;
    
    private LocalDateTime expireTime;
    
    private String remark;
}
```

- [ ] **Step 5: 创建楼栋Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.BuildingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityBuilding;

import java.util.List;

public interface CommunityBuildingService extends IService<CommunityBuilding> {
    IPage<CommunityBuilding> getBuildingPage(Integer pageNum, Integer pageSize, String buildingNo);
    
    List<CommunityBuilding> getBuildingList();
    
    void addBuilding(BuildingRequest request);
    
    void updateBuilding(BuildingRequest request);
    
    void deleteBuilding(Long id);
    
    CommunityBuilding getBuildingById(Long id);
}
```

- [ ] **Step 6: 创建楼栋Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.dto.request.BuildingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityBuilding;
import com.lsy.propertymanagementsystem.mapper.CommunityBuildingMapper;
import com.lsy.propertymanagementsystem.service.CommunityBuildingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityBuildingServiceImpl extends ServiceImpl<CommunityBuildingMapper, CommunityBuilding> implements CommunityBuildingService {
    
    @Override
    public IPage<CommunityBuilding> getBuildingPage(Integer pageNum, Integer pageSize, String buildingNo) {
        LambdaQueryWrapper<CommunityBuilding> wrapper = new LambdaQueryWrapper<>();
        if (buildingNo != null && !buildingNo.isEmpty()) {
            wrapper.like(CommunityBuilding::getBuildingNo, buildingNo);
        }
        wrapper.orderByAsc(CommunityBuilding::getBuildingNo);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public List<CommunityBuilding> getBuildingList() {
        LambdaQueryWrapper<CommunityBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CommunityBuilding::getBuildingNo);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addBuilding(BuildingRequest request) {
        CommunityBuilding building = new CommunityBuilding();
        building.setBuildingNo(request.getBuildingNo());
        building.setFloorCount(request.getFloorCount());
        building.setTotalHouse(request.getTotalHouse());
        building.setBuildYear(request.getBuildYear());
        building.setRemark(request.getRemark());
        this.save(building);
    }
    
    @Override
    @Transactional
    public void updateBuilding(BuildingRequest request) {
        CommunityBuilding building = this.getById(request.getId());
        if (building == null) {
            throw new RuntimeException("楼栋不存在");
        }
        
        building.setBuildingNo(request.getBuildingNo());
        building.setFloorCount(request.getFloorCount());
        building.setTotalHouse(request.getTotalHouse());
        building.setBuildYear(request.getBuildYear());
        building.setRemark(request.getRemark());
        this.updateById(building);
    }
    
    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        this.removeById(id);
    }
    
    @Override
    public CommunityBuilding getBuildingById(Long id) {
        return this.getById(id);
    }
}
```

- [ ] **Step 7: 创建房屋Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.HouseRequest;
import com.lsy.propertymanagementsystem.entity.CommunityHouse;

import java.util.List;

public interface CommunityHouseService extends IService<CommunityHouse> {
    IPage<CommunityHouse> getHousePage(Integer pageNum, Integer pageSize, Long buildingId, String roomNo, Integer houseStatus);
    
    List<CommunityHouse> getHouseList();
    
    void addHouse(HouseRequest request);
    
    void updateHouse(HouseRequest request);
    
    void deleteHouse(Long id);
    
    CommunityHouse getHouseById(Long id);
    
    void updateHouseStatus(Long id, Integer houseStatus);
}
```

- [ ] **Step 8: 创建房屋Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.dto.request.HouseRequest;
import com.lsy.propertymanagementsystem.entity.CommunityHouse;
import com.lsy.propertymanagementsystem.mapper.CommunityHouseMapper;
import com.lsy.propertymanagementsystem.service.CommunityHouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityHouseServiceImpl extends ServiceImpl<CommunityHouseMapper, CommunityHouse> implements CommunityHouseService {
    
    @Override
    public IPage<CommunityHouse> getHousePage(Integer pageNum, Integer pageSize, Long buildingId, String roomNo, Integer houseStatus) {
        LambdaQueryWrapper<CommunityHouse> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(CommunityHouse::getBuildingId, buildingId);
        }
        if (roomNo != null && !roomNo.isEmpty()) {
            wrapper.like(CommunityHouse::getRoomNo, roomNo);
        }
        if (houseStatus != null) {
            wrapper.eq(CommunityHouse::getHouseStatus, houseStatus);
        }
        wrapper.orderByAsc(CommunityHouse::getBuildingId, CommunityHouse::getRoomNo);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public List<CommunityHouse> getHouseList() {
        LambdaQueryWrapper<CommunityHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CommunityHouse::getBuildingId, CommunityHouse::getRoomNo);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addHouse(HouseRequest request) {
        CommunityHouse house = new CommunityHouse();
        house.setBuildingId(request.getBuildingId());
        house.setRoomNo(request.getRoomNo());
        house.setArea(request.getArea());
        house.setHouseType(request.getHouseType());
        house.setHouseStatus(request.getHouseStatus() != null ? request.getHouseStatus() : 0);
        house.setOwnerId(request.getOwnerId());
        house.setRemark(request.getRemark());
        this.save(house);
    }
    
    @Override
    @Transactional
    public void updateHouse(HouseRequest request) {
        CommunityHouse house = this.getById(request.getId());
        if (house == null) {
            throw new RuntimeException("房屋不存在");
        }
        
        house.setBuildingId(request.getBuildingId());
        house.setRoomNo(request.getRoomNo());
        house.setArea(request.getArea());
        house.setHouseType(request.getHouseType());
        house.setHouseStatus(request.getHouseStatus());
        house.setOwnerId(request.getOwnerId());
        house.setRemark(request.getRemark());
        this.updateById(house);
    }
    
    @Override
    @Transactional
    public void deleteHouse(Long id) {
        this.removeById(id);
    }
    
    @Override
    public CommunityHouse getHouseById(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Transactional
    public void updateHouseStatus(Long id, Integer houseStatus) {
        CommunityHouse house = this.getById(id);
        if (house == null) {
            throw new RuntimeException("房屋不存在");
        }
        
        house.setHouseStatus(houseStatus);
        this.updateById(house);
    }
}
```

- [ ] **Step 9: 创建业主Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.OwnerRequest;
import com.lsy.propertymanagementsystem.entity.CommunityOwner;

import java.util.List;

public interface CommunityOwnerService extends IService<CommunityOwner> {
    IPage<CommunityOwner> getOwnerPage(Integer pageNum, Integer pageSize, String name, String phone);
    
    List<CommunityOwner> getOwnerList();
    
    void addOwner(OwnerRequest request);
    
    void updateOwner(OwnerRequest request);
    
    void deleteOwner(Long id);
    
    CommunityOwner getOwnerById(Long id);
    
    void bindUser(Long ownerId, Long userId);
}
```

- [ ] **Step 10: 创建业主Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.dto.request.OwnerRequest;
import com.lsy.propertymanagementsystem.entity.CommunityOwner;
import com.lsy.propertymanagementsystem.mapper.CommunityOwnerMapper;
import com.lsy.propertymanagementsystem.service.CommunityOwnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityOwnerServiceImpl extends ServiceImpl<CommunityOwnerMapper, CommunityOwner> implements CommunityOwnerService {
    
    @Override
    public IPage<CommunityOwner> getOwnerPage(Integer pageNum, Integer pageSize, String name, String phone) {
        LambdaQueryWrapper<CommunityOwner> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(CommunityOwner::getName, name);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(CommunityOwner::getPhone, phone);
        }
        wrapper.orderByDesc(CommunityOwner::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public List<CommunityOwner> getOwnerList() {
        LambdaQueryWrapper<CommunityOwner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CommunityOwner::getName);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addOwner(OwnerRequest request) {
        CommunityOwner owner = new CommunityOwner();
        owner.setUserId(request.getUserId());
        owner.setName(request.getName());
        owner.setIdCard(request.getIdCard());
        owner.setPhone(request.getPhone());
        owner.setEmergencyContact(request.getEmergencyContact());
        owner.setEmergencyPhone(request.getEmergencyPhone());
        owner.setCheckInTime(request.getCheckInTime());
        this.save(owner);
    }
    
    @Override
    @Transactional
    public void updateOwner(OwnerRequest request) {
        CommunityOwner owner = this.getById(request.getId());
        if (owner == null) {
            throw new RuntimeException("业主不存在");
        }
        
        owner.setUserId(request.getUserId());
        owner.setName(request.getName());
        owner.setIdCard(request.getIdCard());
        owner.setPhone(request.getPhone());
        owner.setEmergencyContact(request.getEmergencyContact());
        owner.setEmergencyPhone(request.getEmergencyPhone());
        owner.setCheckInTime(request.getCheckInTime());
        this.updateById(owner);
    }
    
    @Override
    @Transactional
    public void deleteOwner(Long id) {
        this.removeById(id);
    }
    
    @Override
    public CommunityOwner getOwnerById(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Transactional
    public void bindUser(Long ownerId, Long userId) {
        CommunityOwner owner = this.getById(ownerId);
        if (owner == null) {
            throw new RuntimeException("业主不存在");
        }
        
        owner.setUserId(userId);
        this.updateById(owner);
    }
}
```

- [ ] **Step 11: 创建车位Service接口**

```java
package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.ParkingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityParking;

import java.util.List;

public interface CommunityParkingService extends IService<CommunityParking> {
    IPage<CommunityParking> getParkingPage(Integer pageNum, Integer pageSize, String parkingNo, Integer status);
    
    List<CommunityParking> getParkingList();
    
    void addParking(ParkingRequest request);
    
    void updateParking(ParkingRequest request);
    
    void deleteParking(Long id);
    
    CommunityParking getParkingById(Long id);
    
    void updateParkingStatus(Long id, Integer status, Long ownerId);
}
```

- [ ] **Step 12: 创建车位Service实现**

```java
package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.dto.request.ParkingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityParking;
import com.lsy.propertymanagementsystem.mapper.CommunityParkingMapper;
import com.lsy.propertymanagementsystem.service.CommunityParkingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommunityParkingServiceImpl extends ServiceImpl<CommunityParkingMapper, CommunityParking> implements CommunityParkingService {
    
    @Override
    public IPage<CommunityParking> getParkingPage(Integer pageNum, Integer pageSize, String parkingNo, Integer status) {
        LambdaQueryWrapper<CommunityParking> wrapper = new LambdaQueryWrapper<>();
        if (parkingNo != null && !parkingNo.isEmpty()) {
            wrapper.like(CommunityParking::getParkingNo, parkingNo);
        }
        if (status != null) {
            wrapper.eq(CommunityParking::getStatus, status);
        }
        wrapper.orderByAsc(CommunityParking::getParkingNo);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public List<CommunityParking> getParkingList() {
        LambdaQueryWrapper<CommunityParking> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CommunityParking::getParkingNo);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addParking(ParkingRequest request) {
        CommunityParking parking = new CommunityParking();
        parking.setParkingNo(request.getParkingNo());
        parking.setParkingType(request.getParkingType() != null ? request.getParkingType() : 0);
        parking.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        parking.setOwnerId(request.getOwnerId());
        parking.setExpireTime(request.getExpireTime());
        parking.setRemark(request.getRemark());
        this.save(parking);
    }
    
    @Override
    @Transactional
    public void updateParking(ParkingRequest request) {
        CommunityParking parking = this.getById(request.getId());
        if (parking == null) {
            throw new RuntimeException("车位不存在");
        }
        
        parking.setParkingNo(request.getParkingNo());
        parking.setParkingType(request.getParkingType());
        parking.setStatus(request.getStatus());
        parking.setOwnerId(request.getOwnerId());
        parking.setExpireTime(request.getExpireTime());
        parking.setRemark(request.getRemark());
        this.updateById(parking);
    }
    
    @Override
    @Transactional
    public void deleteParking(Long id) {
        this.removeById(id);
    }
    
    @Override
    public CommunityParking getParkingById(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Transactional
    public void updateParkingStatus(Long id, Integer status, Long ownerId) {
        CommunityParking parking = this.getById(id);
        if (parking == null) {
            throw new RuntimeException("车位不存在");
        }
        
        parking.setStatus(status);
        parking.setOwnerId(status == 1 ? ownerId : null);
        parking.setExpireTime(status == 1 ? LocalDateTime.now().plusYears(1) : null);
        this.updateById(parking);
    }
}
```

- [ ] **Step 13: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/dto/ src/main/java/com/lsy/propertymanagementsystem/service/
git commit -m "feat: 创建小区基础信息模块DTO和Service"
```

---

## Task 4: 创建后端Controller

**Files:**
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/CommunityBuildingController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/CommunityHouseController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/CommunityOwnerController.java`
- Create: `src/main/java/com/lsy/propertymanagementsystem/controller/CommunityParkingController.java`

- [ ] **Step 1: 创建楼栋Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.BuildingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityBuilding;
import com.lsy.propertymanagementsystem.service.CommunityBuildingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/building")
public class CommunityBuildingController {
    
    @Autowired
    private CommunityBuildingService buildingService;
    
    @PostMapping
    public Result<?> addBuilding(@Valid @RequestBody BuildingRequest request) {
        buildingService.addBuilding(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<?> updateBuilding(@Valid @RequestBody BuildingRequest request) {
        buildingService.updateBuilding(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<?> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<CommunityBuilding>> getBuildingList() {
        return Result.success(buildingService.getBuildingList());
    }
    
    @GetMapping("/{id}")
    public Result<CommunityBuilding> getBuildingById(@PathVariable Long id) {
        return Result.success(buildingService.getBuildingById(id));
    }
    
    @GetMapping("/page")
    public Result<IPage<CommunityBuilding>> getBuildingPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String buildingNo) {
        return Result.success(buildingService.getBuildingPage(pageNum, pageSize, buildingNo));
    }
}
```

- [ ] **Step 2: 创建房屋Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.HouseRequest;
import com.lsy.propertymanagementsystem.entity.CommunityHouse;
import com.lsy.propertymanagementsystem.service.CommunityHouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/house")
public class CommunityHouseController {
    
    @Autowired
    private CommunityHouseService houseService;
    
    @PostMapping
    public Result<?> addHouse(@Valid @RequestBody HouseRequest request) {
        houseService.addHouse(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<?> updateHouse(@Valid @RequestBody HouseRequest request) {
        houseService.updateHouse(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<?> deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<CommunityHouse>> getHouseList() {
        return Result.success(houseService.getHouseList());
    }
    
    @GetMapping("/{id}")
    public Result<CommunityHouse> getHouseById(@PathVariable Long id) {
        return Result.success(houseService.getHouseById(id));
    }
    
    @GetMapping("/page")
    public Result<IPage<CommunityHouse>> getHousePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String roomNo,
            @RequestParam(required = false) Integer houseStatus) {
        return Result.success(houseService.getHousePage(pageNum, pageSize, buildingId, roomNo, houseStatus));
    }
    
    @PutMapping("/status")
    public Result<?> updateHouseStatus(@RequestParam Long id, @RequestParam Integer houseStatus) {
        houseService.updateHouseStatus(id, houseStatus);
        return Result.success();
    }
}
```

- [ ] **Step 3: 创建业主Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.OwnerRequest;
import com.lsy.propertymanagementsystem.entity.CommunityOwner;
import com.lsy.propertymanagementsystem.service.CommunityOwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class CommunityOwnerController {
    
    @Autowired
    private CommunityOwnerService ownerService;
    
    @PostMapping
    public Result<?> addOwner(@Valid @RequestBody OwnerRequest request) {
        ownerService.addOwner(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<?> updateOwner(@Valid @RequestBody OwnerRequest request) {
        ownerService.updateOwner(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<?> deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<CommunityOwner>> getOwnerList() {
        return Result.success(ownerService.getOwnerList());
    }
    
    @GetMapping("/{id}")
    public Result<CommunityOwner> getOwnerById(@PathVariable Long id) {
        return Result.success(ownerService.getOwnerById(id));
    }
    
    @GetMapping("/page")
    public Result<IPage<CommunityOwner>> getOwnerPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        return Result.success(ownerService.getOwnerPage(pageNum, pageSize, name, phone));
    }
    
    @PostMapping("/bindUser")
    public Result<?> bindUser(@RequestParam Long ownerId, @RequestParam Long userId) {
        ownerService.bindUser(ownerId, userId);
        return Result.success();
    }
}
```

- [ ] **Step 4: 创建车位Controller**

```java
package com.lsy.propertymanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.ParkingRequest;
import com.lsy.propertymanagementsystem.entity.CommunityParking;
import com.lsy.propertymanagementsystem.service.CommunityParkingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class CommunityParkingController {
    
    @Autowired
    private CommunityParkingService parkingService;
    
    @PostMapping
    public Result<?> addParking(@Valid @RequestBody ParkingRequest request) {
        parkingService.addParking(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<?> updateParking(@Valid @RequestBody ParkingRequest request) {
        parkingService.updateParking(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<?> deleteParking(@PathVariable Long id) {
        parkingService.deleteParking(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<CommunityParking>> getParkingList() {
        return Result.success(parkingService.getParkingList());
    }
    
    @GetMapping("/{id}")
    public Result<CommunityParking> getParkingById(@PathVariable Long id) {
        return Result.success(parkingService.getParkingById(id));
    }
    
    @GetMapping("/page")
    public Result<IPage<CommunityParking>> getParkingPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String parkingNo,
            @RequestParam(required = false) Integer status) {
        return Result.success(parkingService.getParkingPage(pageNum, pageSize, parkingNo, status));
    }
    
    @PutMapping("/status")
    public Result<?> updateParkingStatus(@RequestParam Long id, @RequestParam Integer status, @RequestParam(required = false) Long ownerId) {
        parkingService.updateParkingStatus(id, status, ownerId);
        return Result.success();
    }
}
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/lsy/propertymanagementsystem/controller/
git commit -m "feat: 创建小区基础信息模块Controller"
```

---

## Task 5: 创建前端页面

**Files:**
- Create: `frontend/src/api/building.js`
- Create: `frontend/src/api/house.js`
- Create: `frontend/src/api/owner.js`
- Create: `frontend/src/api/parking.js`
- Create: `frontend/src/views/community/building/index.vue`
- Create: `frontend/src/views/community/house/index.vue`
- Create: `frontend/src/views/community/owner/index.vue`
- Create: `frontend/src/views/community/parking/index.vue`
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: 创建楼栋API**

```javascript
import request from '@/utils/request'

export function getBuildingPage(params) {
  return request({
    url: '/api/building/page',
    method: 'get',
    params
  })
}

export function getBuildingList() {
  return request({
    url: '/api/building/list',
    method: 'get'
  })
}

export function getBuildingById(id) {
  return request({
    url: `/api/building/${id}`,
    method: 'get'
  })
}

export function addBuilding(data) {
  return request({
    url: '/api/building',
    method: 'post',
    data
  })
}

export function updateBuilding(data) {
  return request({
    url: '/api/building',
    method: 'put',
    data
  })
}

export function deleteBuilding(id) {
  return request({
    url: `/api/building/${id}`,
    method: 'delete'
  })
}
```

- [ ] **Step 2: 创建房屋API**

```javascript
import request from '@/utils/request'

export function getHousePage(params) {
  return request({
    url: '/api/house/page',
    method: 'get',
    params
  })
}

export function getHouseList() {
  return request({
    url: '/api/house/list',
    method: 'get'
  })
}

export function getHouseById(id) {
  return request({
    url: `/api/house/${id}`,
    method: 'get'
  })
}

export function addHouse(data) {
  return request({
    url: '/api/house',
    method: 'post',
    data
  })
}

export function updateHouse(data) {
  return request({
    url: '/api/house',
    method: 'put',
    data
  })
}

export function deleteHouse(id) {
  return request({
    url: `/api/house/${id}`,
    method: 'delete'
  })
}

export function updateHouseStatus(id, houseStatus) {
  return request({
    url: '/api/house/status',
    method: 'put',
    params: { id, houseStatus }
  })
}
```

- [ ] **Step 3: 创建业主API**

```javascript
import request from '@/utils/request'

export function getOwnerPage(params) {
  return request({
    url: '/api/owner/page',
    method: 'get',
    params
  })
}

export function getOwnerList() {
  return request({
    url: '/api/owner/list',
    method: 'get'
  })
}

export function getOwnerById(id) {
  return request({
    url: `/api/owner/${id}`,
    method: 'get'
  })
}

export function addOwner(data) {
  return request({
    url: '/api/owner',
    method: 'post',
    data
  })
}

export function updateOwner(data) {
  return request({
    url: '/api/owner',
    method: 'put',
    data
  })
}

export function deleteOwner(id) {
  return request({
    url: `/api/owner/${id}`,
    method: 'delete'
  })
}

export function bindUser(ownerId, userId) {
  return request({
    url: '/api/owner/bindUser',
    method: 'post',
    params: { ownerId, userId }
  })
}
```

- [ ] **Step 4: 创建车位API**

```javascript
import request from '@/utils/request'

export function getParkingPage(params) {
  return request({
    url: '/api/parking/page',
    method: 'get',
    params
  })
}

export function getParkingList() {
  return request({
    url: '/api/parking/list',
    method: 'get'
  })
}

export function getParkingById(id) {
  return request({
    url: `/api/parking/${id}`,
    method: 'get'
  })
}

export function addParking(data) {
  return request({
    url: '/api/parking',
    method: 'post',
    data
  })
}

export function updateParking(data) {
  return request({
    url: '/api/parking',
    method: 'put',
    data
  })
}

export function deleteParking(id) {
  return request({
    url: `/api/parking/${id}`,
    method: 'delete'
  })
}

export function updateParkingStatus(id, status, ownerId) {
  return request({
    url: '/api/parking/status',
    method: 'put',
    params: { id, status, ownerId }
  })
}
```

- [ ] **Step 5: 创建楼栋管理页面**

```vue
<template>
  <div class="building-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>楼栋管理</span>
          <el-button type="primary" @click="handleAdd">新增楼栋</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="楼栋编号">
          <el-input v-model="queryParams.buildingNo" placeholder="请输入楼栋编号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe>
        <el-table-column prop="buildingNo" label="楼栋编号" />
        <el-table-column prop="floorCount" label="总楼层" />
        <el-table-column prop="totalHouse" label="总户数" />
        <el-table-column prop="buildYear" label="建成年份" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="楼栋编号" prop="buildingNo">
          <el-input v-model="form.buildingNo" placeholder="请输入楼栋编号" />
        </el-form-item>
        <el-form-item label="总楼层" prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" />
        </el-form-item>
        <el-form-item label="总户数" prop="totalHouse">
          <el-input-number v-model="form.totalHouse" :min="1" />
        </el-form-item>
        <el-form-item label="建成年份" prop="buildYear">
          <el-input-number v-model="form.buildYear" :min="1900" :max="2100" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuildingPage, addBuilding, updateBuilding, deleteBuilding } from '@/api/building'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  buildingNo: ''
})

const form = reactive({
  id: null,
  buildingNo: '',
  floorCount: null,
  totalHouse: null,
  buildYear: null,
  remark: ''
})

const rules = {
  buildingNo: [{ required: true, message: '请输入楼栋编号', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getBuildingPage(queryParams)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.buildingNo = ''
  handleSearch()
}

const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增楼栋'
  Object.assign(form, { id: null, buildingNo: '', floorCount: null, totalHouse: null, buildYear: null, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑楼栋'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.id) {
    await updateBuilding(form)
    ElMessage.success('编辑成功')
  } else {
    await addBuilding(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该楼栋吗？', '提示', { type: 'warning' })
  await deleteBuilding(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.building-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
```

- [ ] **Step 6: 创建房屋管理页面**

```vue
<template>
  <div class="house-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>房屋管理</span>
          <el-button type="primary" @click="handleAdd">新增房屋</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="楼栋">
          <el-select v-model="queryParams.buildingId" placeholder="请选择楼栋" clearable>
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="queryParams.roomNo" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.houseStatus" placeholder="请选择状态" clearable>
            <el-option label="空置" :value="0" />
            <el-option label="已入住" :value="1" />
            <el-option label="出租" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe>
        <el-table-column prop="buildingId" label="楼栋">
          <template #default="{ row }">
            {{ getBuildingName(row.buildingId) }}
          </template>
        </el-table-column>
        <el-table-column prop="roomNo" label="房间号" />
        <el-table-column prop="area" label="面积(㎡)" />
        <el-table-column prop="houseType" label="户型" />
        <el-table-column prop="houseStatus" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.houseStatus)">{{ getStatusText(row.houseStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleChangeStatus(row)">修改状态</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number v-model="form.area" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="户型" prop="houseType">
          <el-input v-model="form.houseType" placeholder="请输入户型" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="statusDialogVisible" title="修改状态" width="400px">
      <el-form label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="statusForm.houseStatus">
            <el-option label="空置" :value="0" />
            <el-option label="已入住" :value="1" />
            <el-option label="出租" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHousePage, addHouse, updateHouse, deleteHouse, updateHouseStatus } from '@/api/house'
import { getBuildingList } from '@/api/building'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const buildingList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  buildingId: null,
  roomNo: '',
  houseStatus: null
})

const form = reactive({
  id: null,
  buildingId: null,
  roomNo: '',
  area: null,
  houseType: '',
  houseStatus: 0,
  ownerId: null,
  remark: ''
})

const statusForm = reactive({
  id: null,
  houseStatus: 0
})

const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getHousePage(queryParams)
  tableData.value = res.data.records
  total.value = res.data.total
}

const loadBuildings = async () => {
  const res = await getBuildingList()
  buildingList.value = res.data
}

const getBuildingName = (id) => {
  const building = buildingList.value.find(item => item.id === id)
  return building ? building.buildingNo : '-'
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '空置', 1: '已入住', 2: '出租' }
  return texts[status] || '未知'
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.buildingId = null
  queryParams.roomNo = ''
  queryParams.houseStatus = null
  handleSearch()
}

const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增房屋'
  Object.assign(form, { id: null, buildingId: null, roomNo: '', area: null, houseType: '', houseStatus: 0, ownerId: null, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑房屋'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.id) {
    await updateHouse(form)
    ElMessage.success('编辑成功')
  } else {
    await addHouse(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleChangeStatus = (row) => {
  statusForm.id = row.id
  statusForm.houseStatus = row.houseStatus
  statusDialogVisible.value = true
}

const handleStatusSubmit = async () => {
  await updateHouseStatus(statusForm.id, statusForm.houseStatus)
  ElMessage.success('状态修改成功')
  statusDialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该房屋吗？', '提示', { type: 'warning' })
  await deleteHouse(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadBuildings()
  loadData()
})
</script>

<style scoped>
.house-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
```

- [ ] **Step 7: 创建业主管理页面**

```vue
<template>
  <div class="owner-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>业主管理</span>
          <el-button type="primary" @click="handleAdd">新增业主</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="业主姓名">
          <el-input v-model="queryParams.name" placeholder="请输入业主姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe>
        <el-table-column prop="name" label="业主姓名" />
        <el-table-column prop="idCard" label="身份证号" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="emergencyContact" label="紧急联系人" />
        <el-table-column prop="emergencyPhone" label="紧急联系电话" />
        <el-table-column prop="checkInTime" label="入住时间" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleBindUser(row)">绑定用户</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="业主姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入业主姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
        <el-form-item label="紧急联系电话" prop="emergencyPhone">
          <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
        </el-form-item>
        <el-form-item label="入住时间" prop="checkInTime">
          <el-date-picker v-model="form.checkInTime" type="datetime" placeholder="请选择入住时间" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="bindDialogVisible" title="绑定用户" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用户ID">
          <el-input-number v-model="bindForm.userId" :min="1" placeholder="请输入用户ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOwnerPage, addOwner, updateOwner, deleteOwner, bindUser } from '@/api/owner'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const bindDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  phone: ''
})

const form = reactive({
  id: null,
  userId: null,
  name: '',
  idCard: '',
  phone: '',
  emergencyContact: '',
  emergencyPhone: '',
  checkInTime: null
})

const bindForm = reactive({
  ownerId: null,
  userId: null
})

const rules = {
  name: [{ required: true, message: '请输入业主姓名', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getOwnerPage(queryParams)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.name = ''
  queryParams.phone = ''
  handleSearch()
}

const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增业主'
  Object.assign(form, { id: null, userId: null, name: '', idCard: '', phone: '', emergencyContact: '', emergencyPhone: '', checkInTime: null })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑业主'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.id) {
    await updateOwner(form)
    ElMessage.success('编辑成功')
  } else {
    await addOwner(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleBindUser = (row) => {
  bindForm.ownerId = row.id
  bindForm.userId = row.userId
  bindDialogVisible.value = true
}

const handleBindSubmit = async () => {
  await bindUser(bindForm.ownerId, bindForm.userId)
  ElMessage.success('绑定成功')
  bindDialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该业主吗？', '提示', { type: 'warning' })
  await deleteOwner(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.owner-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
```

- [ ] **Step 8: 创建车位管理页面**

```vue
<template>
  <div class="parking-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车位管理</span>
          <el-button type="primary" @click="handleAdd">新增车位</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="车位编号">
          <el-input v-model="queryParams.parkingNo" placeholder="请输入车位编号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="空闲" :value="0" />
            <el-option label="已租赁" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe>
        <el-table-column prop="parkingNo" label="车位编号" />
        <el-table-column prop="parkingType" label="车位类型">
          <template #default="{ row }">
            {{ row.parkingType === 0 ? '固定车位' : '临时车位' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'warning'">{{ row.status === 0 ? '空闲' : '已租赁' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="到期时间" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" type="success" link @click="handleRent(row)">租赁</el-button>
            <el-button v-if="row.status === 1" type="warning" link @click="handleRelease(row)">释放</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="车位编号" prop="parkingNo">
          <el-input v-model="form.parkingNo" placeholder="请输入车位编号" />
        </el-form-item>
        <el-form-item label="车位类型" prop="parkingType">
          <el-select v-model="form.parkingType">
            <el-option label="固定车位" :value="0" />
            <el-option label="临时车位" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="rentDialogVisible" title="租赁车位" width="400px">
      <el-form label-width="100px">
        <el-form-item label="业主ID">
          <el-input-number v-model="rentForm.ownerId" :min="1" placeholder="请输入业主ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRentSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getParkingPage, addParking, updateParking, deleteParking, updateParkingStatus } from '@/api/parking'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const rentDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  parkingNo: '',
  status: null
})

const form = reactive({
  id: null,
  parkingNo: '',
  parkingType: 0,
  status: 0,
  ownerId: null,
  expireTime: null,
  remark: ''
})

const rentForm = reactive({
  parkingId: null,
  ownerId: null
})

const rules = {
  parkingNo: [{ required: true, message: '请输入车位编号', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getParkingPage(queryParams)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.parkingNo = ''
  queryParams.status = null
  handleSearch()
}

const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增车位'
  Object.assign(form, { id: null, parkingNo: '', parkingType: 0, status: 0, ownerId: null, expireTime: null, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑车位'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.id) {
    await updateParking(form)
    ElMessage.success('编辑成功')
  } else {
    await addParking(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleRent = (row) => {
  rentForm.parkingId = row.id
  rentForm.ownerId = null
  rentDialogVisible.value = true
}

const handleRentSubmit = async () => {
  await updateParkingStatus(rentForm.parkingId, 1, rentForm.ownerId)
  ElMessage.success('租赁成功')
  rentDialogVisible.value = false
  loadData()
}

const handleRelease = async (row) => {
  await ElMessageBox.confirm('确认释放该车位吗？', '提示', { type: 'warning' })
  await updateParkingStatus(row.id, 0, null)
  ElMessage.success('释放成功')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该车位吗？', '提示', { type: 'warning' })
  await deleteParking(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.parking-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
```

- [ ] **Step 9: 更新路由配置**

```javascript
// 在 router/index.js 中添加小区管理路由

const routes = [
  // ... 其他路由
  {
    path: '/community',
    component: Layout,
    redirect: '/community/building',
    name: 'Community',
    meta: { title: '小区管理', icon: 'House' },
    children: [
      {
        path: 'building',
        name: 'Building',
        component: () => import('@/views/community/building/index.vue'),
        meta: { title: '楼栋管理' }
      },
      {
        path: 'house',
        name: 'House',
        component: () => import('@/views/community/house/index.vue'),
        meta: { title: '房屋管理' }
      },
      {
        path: 'owner',
        name: 'Owner',
        component: () => import('@/views/community/owner/index.vue'),
        meta: { title: '业主管理' }
      },
      {
        path: 'parking',
        name: 'Parking',
        component: () => import('@/views/community/parking/index.vue'),
        meta: { title: '车位管理' }
      }
    ]
  }
]
```

- [ ] **Step 10: 提交**

```bash
git add frontend/src/api/ frontend/src/views/community/ frontend/src/router/index.js
git commit -m "feat: 创建小区基础信息模块前端页面"
```

---

## 执行选项

**计划已保存到 `docs/superpowers/plans/2026-07-12-community-info-plan.md`。两种执行方式：**

**1. Subagent-Driven（推荐）** - 每个Task分配一个独立子代理执行，任务间进行代码审查，快速迭代

**2. Inline Execution** - 在当前会话中使用executing-plans执行，批量执行并设置检查点

**选择哪种方式？**
