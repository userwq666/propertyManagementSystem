# SpringBoot+Vue3+MyBatisPlus+MySQL 物业管理系统整体设计方案
## 一、项目概述
### 1.1 技术栈
- 后端：SpringBoot、MyBatis-Plus、MySQL 8.0、JWT、Spring Validation
- 前端：Vue3、Vite、Element Plus、Vue Router、Pinia、ECharts
- 架构：前后端分离架构

### 1.2 系统角色
1. 超级管理员：系统最高权限，负责权限、系统配置
2. 物业管理员：小区日常业务处理（收费、报修、业主、公告、巡检）
3. 业主住户：个人线上服务（查账单、报修、投诉、看公告）

### 1.3 业务目标
实现小区楼栋、房屋、业主、车位、收费、报修、投诉、公告、设备巡检数字化管理，台账电子化，减少人工统计，提供业主线上自助服务。

## 二、数据库设计（MySQL utf8mb4）
统一规范：
1. 所有表包含：主键id、create_time、update_time、deleted（逻辑删除，适配MyBatis-Plus）
2. 关联字段建立索引；状态使用数字枚举；密码加密存储
3. 涉及图片字段存储文件访问路径

### 2.1 系统权限模块表
#### 1. sys_user 系统用户表
存储管理员、业主登录账号
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| username | 登录账号，唯一 |
| password | BCrypt加密密码 |
| real_name | 真实姓名 |
| phone | 手机号 |
| avatar | 头像地址 |
| user_type | 用户类型：1超级管理员 2物业管理员 3业主 |
| status | 账号状态：0禁用 1正常 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 2. sys_role 角色表
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| role_name | 角色名称 |
| role_key | 权限标识 |
| remark | 角色描述 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 3. sys_user_role 用户角色关联表
多对一：一个用户多个角色
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| user_id | 用户id |
| role_id | 角色id |

#### 4. sys_menu 菜单权限表
控制页面菜单、按钮操作权限
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| parent_id | 父菜单id |
| menu_name | 菜单名称 |
| path | 前端路由 |
| component | 前端组件地址 |
| perms | 权限标识 |
| menu_type | 类型：目录/菜单/按钮 |
| sort | 排序号 |
| status | 启用状态 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 5. sys_role_menu 角色菜单关联表
角色绑定菜单权限
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| role_id | 角色id |
| menu_id | 菜单id |

#### 6. sys_oper_log 操作日志表
记录后台所有操作行为
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| user_name | 操作人账号 |
| oper_module | 操作模块 |
| oper_type | 操作类型（新增/编辑/删除） |
| oper_ip | 请求ip |
| oper_desc | 操作描述 |
| create_time | 操作时间 |

### 2.2 小区基础信息模块表
#### 1. community_building 楼栋表
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| building_no | 楼栋编号（1栋、2栋） |
| floor_count | 总楼层 |
| total_house | 总户数 |
| build_year | 建成年份 |
| remark | 备注 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 2. community_house 房屋表
核心基础表，关联楼栋、业主
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| building_id | 楼栋id |
| room_no | 房间号（1-101） |
| area | 房屋面积 |
| house_type | 户型（两室一厅等） |
| house_status | 房屋状态：0空置 1已入住 2出租 |
| owner_id | 业主id |
| remark | 备注 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 3. community_owner 业主信息表
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| user_id | 关联系统登录用户id |
| name | 业主姓名 |
| id_card | 身份证号 |
| phone | 联系电话 |
| emergency_contact | 紧急联系人 |
| emergency_phone | 紧急联系电话 |
| check_in_time | 入住时间 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 4. community_parking 车位表
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| parking_no | 车位编号 |
| parking_type | 0固定车位 1临时车位 |
| status | 0空闲 1已租赁 |
| owner_id | 所属业主id |
| expire_time | 租赁到期时间 |
| remark | 备注 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

### 2.3 物业收费业务表
#### 1. fee_item 收费项目表
物业费、水费、电费、停车费、垃圾清运费等
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| item_name | 收费项目名称 |
| price | 单价 |
| cycle_type | 收费周期：月/季/年 |
| status | 是否启用 |
| remark | 备注 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 2. fee_record 缴费账单记录表
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| owner_id | 业主id |
| house_id | 房屋id |
| item_id | 收费项目id |
| total_money | 应付总金额 |
| bill_cycle | 账单所属周期 |
| pay_status | 0未缴费 1已缴费 2欠费 |
| pay_time | 实际缴费时间 |
| pay_way | 支付方式 |
| create_time | 生成账单时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

### 2.4 业主服务业务表
#### 1. repair_record 报修记录表
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| owner_id | 报修业主id |
| house_id | 房屋id |
| repair_type | 报修类型：水电/门窗/公共设备 |
| content | 故障描述 |
| img_url | 故障图片 |
| status | 0待处理 1处理中 2已完成 3驳回 |
| handle_user | 处理物业人员 |
| handle_result | 处理结果 |
| finish_time | 完成时间 |
| create_time | 报修提交时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 2. complaint_suggest 投诉建议表
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| owner_id | 业主id |
| title | 投诉标题 |
| content | 投诉内容 |
| img_url | 佐证图片 |
| status | 0待回复 1已处理完成 |
| reply_content | 物业回复内容 |
| handle_user | 处理人 |
| create_time | 提交时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 3. notice_info 小区公告表
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| title | 公告标题 |
| content | 公告正文 |
| notice_type | 通知类型：缴费通知/活动通知/通用公告 |
| top_flag | 是否置顶 0否1是 |
| publish_user | 发布人 |
| status | 是否展示 |
| create_time | 发布时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

#### 4. device_inspect 公共设备巡检表
电梯、消防、供水设备
| 字段 | 说明 |
| ---- | ---- |
| id | 主键 |
| device_name | 设备名称 |
| location | 设备位置 |
| inspect_user | 巡检人员 |
| inspect_time | 巡检时间 |
| device_status | 0正常 1异常 |
| abnormal_desc | 异常描述 |
| solve_plan | 处理方案 |
| remark | 备注 |
| create_time | 创建时间 |
| update_time | 更新时间 |
| deleted | 逻辑删除 |

## 三、系统模块整体设计
### 3.1 后端分层结构（SpringBoot）
1. entity：数据库实体，适配MyBatis-Plus
2. mapper：数据访问层，继承BaseMapper
3. service：业务逻辑层（Service+ServiceImpl）
4. controller：接口控制层，接收前端请求
5. common：公共工具包（统一返回、异常处理、JWT、分页、文件上传、枚举）

### 3.2 八大核心业务模块
#### 模块1：系统管理模块（仅超级管理员）
- 用户管理：新增、编辑、禁用、分配角色
- 角色管理：新增角色、分配菜单权限
- 菜单权限管理：配置页面与按钮权限
- 操作日志：查询所有后台操作记录
- 系统参数配置

#### 模块2：小区基础信息模块（物业管理员）
- 多小区支持：增加小区表，支持管理多个小区数据隔离
- 楼栋管理：楼栋增删改查
- 房屋管理：绑定楼栋、录入户型面积、关联业主
- 业主管理：业主信息录入、绑定房屋、开通登录账号
- 车位管理：车位新增、租赁分配、到期管理

#### 模块3：收费管理模块
- 收费项目配置：自定义物业费、水电费单价周期
- 账单管理：批量生成月度/年度账单
- 缴费记录：查询、导出缴费订单
- 欠费统计：筛选欠费业主，导出欠费报表

#### 模块4：报修维修模块
- 报修列表：查看业主提交报修单
- 工单处理：接单、更新维修进度、填写处理结果
- 历史报修记录查询、筛选统计

#### 模块5：投诉建议模块
- 投诉列表：查看业主提交投诉/建议
- 回复处理：填写回复内容，完结工单
- 投诉数据统计

#### 模块6：公告通知模块
- 公告新增、编辑、删除
- 置顶/取消置顶、上下架公告
- 公告浏览记录统计

#### 模块7：设备巡检模块
- 设备巡检记录录入
- 异常设备登记、跟进处理
- 巡检记录导出、月度巡检统计


## 四、分角色功能设计
### 4.1 超级管理员
1. 账号权限管理：管理所有后台账号、角色、菜单权限
2. 日志审计：查看全系统操作日志
3. 系统全局参数配置
4. 全部业务数据查看、导出，无数据隔离限制

### 4.2 物业管理员（后台管理核心角色）
1. 基础信息维护：楼栋、房屋、业主、车位录入与维护
2. 收费业务：生成账单、核对缴费、统计欠费、导出报表
3. 工单处理：维修工单接单处理、投诉回复
4. 公告发布、编辑管理
5. 公共设备巡检记录登记
6. 查看各类业务统计图表

### 4.3 业主（前端自助端）
1. 个人中心：修改手机号、密码，查看自有房屋/车位
2. 缴费服务：查看待缴账单、历史缴费记录
3. 报修服务：线上提交报修、查看维修进度
4. 投诉建议：提交意见，查看物业回复
5. 公告中心：浏览小区全部公开公告

## 五、前端架构设计（Vue3 + Element Plus）
1. 路由拦截：JWT token校验，未登录跳转登录页
2. 动态菜单：根据当前用户角色动态渲染侧边栏菜单
3. 公共封装：统一分页组件、文件上传、弹窗、表单校验
4接口统一封装：统一请求头、统一错误提示、统一返回解析

