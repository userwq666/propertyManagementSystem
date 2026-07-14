# 设备巡检模块实体类设计

## 概述

创建物业管理系统设备巡检模块的4个实体类，对应已创建的数据库表。

## 实体类列表

1. **EquipmentCategory** - 设备分类实体类
2. **Equipment** - 设备实体类
3. **InspectionPlan** - 巡检计划实体类
4. **InspectionRecord** - 巡检记录实体类

## 设计决策

### 1. 使用方案1：严格按照任务描述

**选择理由：**
- 与现有实体类风格一致（如Announcement.java, SysUser.java）
- 符合任务描述的要求
- 简单直接，易于维护
- 校验逻辑在服务层统一处理

### 2. 不添加校验注解

**原因：**
- 现有实体类没有使用jakarta.validation注解
- 保持代码风格一致性
- 校验逻辑可以在服务层或DTO中实现

### 3. 使用MyBatis-Plus注解

**使用注解：**
- `@TableName` - 映射数据库表名
- `@TableId(type = IdType.AUTO)` - 主键自增
- `@TableLogic` - 逻辑删除字段

### 4. 使用Lombok注解

**使用注解：**
- `@Data` - 自动生成getter/setter/toString/equals/hashCode

## 实体类详情

### 1. EquipmentCategory（设备分类）

**对应表：** `equipment_category`

**字段：**
- `id` - 主键，自增
- `categoryName` - 分类名称
- `description` - 分类描述
- `sortOrder` - 排序
- `isDefault` - 是否预设分类
- `createTime` - 创建时间
- `updateTime` - 更新时间
- `deleted` - 逻辑删除

### 2. Equipment（设备）

**对应表：** `equipment`

**字段：**
- `id` - 主键，自增
- `equipmentName` - 设备名称
- `equipmentCode` - 设备编号（唯一）
- `categoryId` - 分类ID
- `location` - 安装位置
- `status` - 状态（0正常 1维修中 2停用）
- `maintenanceUser` - 维护人员
- `installDate` - 安装日期
- `warrantyDate` - 保修到期
- `remark` - 备注
- `createTime` - 创建时间
- `updateTime` - 更新时间
- `deleted` - 逻辑删除

### 3. InspectionPlan（巡检计划）

**对应表：** `inspection_plan`

**字段：**
- `id` - 主键，自增
- `planName` - 计划名称
- `planType` - 计划类型（0手动创建 1周期生成）
- `cycleType` - 周期类型（0每天 1每周 2每月 3自定义）
- `cycleValue` - 自定义周期值
- `planDate` - 计划日期
- `startDate` - 周期开始日期
- `endDate` - 周期结束日期
- `equipmentIds` - 设备ID列表（逗号分隔）
- `inspectorIds` - 巡检人员ID列表（逗号分隔）
- `status` - 状态（0待执行 1执行中 2已完成 3已取消）
- `createTime` - 创建时间
- `updateTime` - 更新时间
- `deleted` - 逻辑删除

### 4. InspectionRecord（巡检记录）

**对应表：** `inspection_record`

**字段：**
- `id` - 主键，自增
- `planId` - 关联计划ID（可为空）
- `equipmentId` - 设备ID
- `inspectorId` - 巡检人员ID
- `inspectTime` - 巡检时间
- `result` - 巡检结果（0正常 1一般异常 2严重异常）
- `faultDesc` - 功能异常描述
- `repairSuggestion` - 维修建议
- `budget` - 预估费用
- `duration` - 预估工时
- `createTime` - 创建时间
- `updateTime` - 更新时间
- `deleted` - 逻辑删除

## 数据库表结构

所有实体类字段与数据库表结构完全匹配，已通过SQL脚本验证。

## 依赖关系

- SpringBoot 4.x
- MyBatis-Plus
- Lombok
- MySQL

## 验证

1. 实体类字段与数据库表结构匹配
2. 与现有实体类代码风格一致
3. 使用正确的MyBatis-Plus注解
4. 支持逻辑删除
5. 支持自动填充 createTime 和 updateTime