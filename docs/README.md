# 物业管理系统

## 一、项目概述

物业管理系统是一个基于SpringBoot+MyBatisPlus+MySQL后端架构，Vue3+Element Plus前端架构的前后端分离系统。系统提供系统管理、小区基础信息管理、物业收费、报修维修、投诉建议、公告管理、设备巡检、数据统计等功能模块。

### 1.1 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | SpringBoot | 4.0.8-SNAPSHOT |
| ORM框架 | MyBatis-Plus | 3.5.3.1 |
| 数据库 | MySQL | 8.0 |
| 认证授权 | JWT | 0.11.5 |
| 参数校验 | Jakarta Validation | - |
| 前端框架 | Vue3 | 3.x |
| 构建工具 | Vite | - |
| UI组件库 | Element Plus | - |
| 路由 | Vue Router | - |
| 状态管理 | Pinia | - |

### 1.2 项目结构

```
propertyManagementSystem/
├── src/main/java/com/lsy/propertymanagementsystem/
│   ├── common/           # 公共类（Result, BusinessException等）
│   ├── config/           # 配置类（WebMvcConfig, SecurityConfig）
│   ├── entity/           # 实体类
│   ├── mapper/           # Mapper接口
│   ├── dto/              # 数据传输对象
│   ├── service/          # 服务层
│   │   └── impl/         # 服务实现类
│   ├── controller/       # 控制器层
│   └── interceptor/      # 拦截器
├── frontend/             # 前端项目
│   └── src/
│       ├── api/          # API接口
│       ├── views/        # 页面组件
│       ├── router/       # 路由配置
│       ├── store/        # 状态管理
│       └── utils/        # 工具类
├── sql/                  # 数据库脚本
└── docs/                 # 项目文档
    ├── api/              # API文档
    ├── database/         # 数据字典
    ├── architecture/     # 系统架构
    └── progress/         # 开发进度
```

## 二、功能模块

| 模块 | 状态 | 说明 |
|------|------|------|
| 系统管理 | ✅ 已完成 | 用户管理、角色管理、菜单管理、操作日志 |
| 小区基础信息 | ✅ 已完成 | 楼栋管理、房屋管理、业主管理、车位管理 |
| 物业收费 | ✅ 已完成 | 收费项目配置、账单管理、缴费记录、欠费统计 |
| 报修维修 |   待开发 | 报修记录、处理流程、完成评价 |
| 投诉建议 |   待开发 | 投诉建议提交、处理回复 |
| 公告管理 |   待开发 | 公告发布、公告查看 |
| 设备巡检 |   待开发 | 巡检计划、巡检记录 |
| 数据统计 |   待开发 | 收费统计、报修统计、图表展示 |

## 三、快速开始

### 3.1 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 3.2 后端启动

```bash
# 1. 创建数据库
mysql -u root -p123456 < sql/init.sql
mysql -u root -p123456 < sql/community.sql
mysql -u root -p123456 < sql/fee.sql

# 2. 修改数据库配置
# 编辑 src/main/resources/application.properties

# 3. 启动后端服务
mvn spring-boot:run
```

### 3.3 前端启动

```bash
# 1. 安装依赖
cd frontend
npm install

# 2. 启动前端服务
npm run dev
```

### 3.4 默认账号

- 管理员账号：root
- 管理员密码：123456

## 四、文档目录

- [API文档](docs/api/README.md) - 各模块接口说明
- [数据字典](docs/database/README.md) - 数据库表结构说明
- [系统架构](docs/architecture/README.md) - 系统架构设计
- [开发进度](docs/progress/README.md) - 项目开发进度

## 五、开发规范

### 5.1 分支管理

- main: 主分支，稳定版本
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 热修复分支

### 5.2 提交规范

- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关

### 5.3 代码规范

- 后端遵循阿里巴巴Java开发手册
- 前端遵循Vue3 Composition API风格
- 使用ESLint进行代码检查
- 使用Prettier进行代码格式化
