# 物业管理系统

基于 **Spring Boot + Vue 3** 的前后端分离社区物业管理系统，覆盖小区基础管理、收费管理、报修处理、投诉建议、设备巡检、公告通知与统计分析等核心业务，支持 RBAC 权限控制与 WebSocket 实时消息推送。

## 技术栈

| 端 | 技术 |
|----|------|
| 后端 | Spring Boot 4.0.7、Spring Security、MyBatis-Plus 3.5.15、MySQL 8.0、JWT、WebSocket |
| 前端 | Vue 3、Vite、Element Plus、Pinia、Vue Router、ECharts |
| 构建 | Maven（后端）、npm/Vite（前端） |

## 功能总览

- **系统管理**：用户、角色、菜单/按钮权限分配（RBAC）
- **社区管理**：楼栋、房屋、业主、车位档案（业主按档案数据隔离）
- **收费管理**：收费项目发布自动生成待缴费账单、财务线下收款确认、消费事项审核公示
- **报修管理**：提交/派单/接单/结单/评价全流程，设备状态联动，异常一键报修
- **投诉建议**：提交/受理/处理/回复/确认评价
- **设备管理**：设备分类、设备台账、设备记录（只读汇总）
- **巡检管理**：巡检计划按周期生成任务，二维矩阵打卡，异常转报修并自动标记已处理
- **公告通知**：发布/下架/置顶/定时发布
- **统计分析**：分模块统计卡片 + ECharts 图表，按权限开放
- **首页工作台**：按身份展示待办事项数量与最新公告
- **实时推送**：WebSocket 状态变更通知 + 列表自动刷新

## 快速开始

### 1. 环境准备

- JDK 17+
- MySQL 8.0+
- Node.js 18+（可选，前端开发/重建需要）

### 2. 初始化数据库

```sql
CREATE DATABASE IF NOT EXISTS property_management_system
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `src/main/resources/application.properties` 中数据库账号密码，启动应用后会自动执行 `src/main/resources/sql/` 下的初始化脚本（建表 + 初始账号/角色/权限）。

### 3. 启动后端

```bash
mvn spring-boot:run
```

服务地址：`http://localhost:8080`

### 4. 启动前端（开发模式）

```bash
cd frontend
npm install
npm run dev
```

开发地址：`http://localhost:5173`（已配置 `/api`、`/ws` 代理到 8080）

### 5. 打包部署

```bash
cd frontend && npm run build   # 前端产物输出到 src/main/resources/static
cd .. && mvn clean package -DskipTests
java -jar target/propertyManagementSystem-0.0.1-SNAPSHOT.jar
```

## 默认账号（初始密码 123456）

| 账号 | 姓名 | 角色 |
|------|------|------|
| root | 系统管理员 | 超级管理员 |
| admin | 王经理 | 物业管理员 |
| zhouwei | 周伟 | 业主 |
| chenjie | 陈姐 | 维修工 |
| zhouan | 周安 | 巡检员 |

> 交付前请修改默认密码。

## 项目结构

```
propertyManagementSystem/
├── src/main/java/com/lsy/propertymanagementsystem/
│   ├── common/          # 统一响应、异常、工具类
│   ├── config/          # 安全、JWT、MyBatis-Plus、数据库初始化
│   ├── interceptor/     # JWT 认证过滤器
│   ├── task/            # 定时任务
│   ├── websocket/       # WebSocket 会话与消息推送
│   └── module/          # 业务模块（system/community/fee/repair/complaint/equipment/inspection/announcement/statistics）
├── src/main/resources/
│   ├── sql/             # 数据库初始化脚本（00~09）
│   └── static/          # 前端构建产物
└── frontend/            # Vue 3 前端
    └── src/
        ├── api/         # 接口封装（与后端模块对应）
        ├── views/       # 页面（与路由对应）
        ├── stores/      # Pinia 状态
        ├── composables/ # 组合式函数（列表实时刷新）
        ├── directives/  # v-permission 权限指令
        └── utils/       # 请求封装、WebSocket 客户端
```

## 文档索引

| 文档                                           | 说明                                              |
|------------------------------------------------|---------------------------------------------------|
| [docs/01_项目说明.md](docs/01_项目说明.md)     | 选题背景、需求分析、角色与功能需求                |
| [docs/02_系统设计.md](docs/02_系统设计.md)     | 总体架构、权限设计、WebSocket、定时任务、核心流程 |
| [docs/03_数据库设计.md](docs/03_数据库设计.md) | 表结构、ER 关系、枚举、初始化数据                 |
| [docs/04_接口文档.md](docs/04_接口文档.md)     | 全部 RESTful 接口与 WebSocket 说明                |
| [docs/05_部署指南.md](docs/05_部署指南.md)     | 环境、初始化、构建、运行、常见问题                |
| [docs/06_测试与验收.md](docs/06_测试与验收.md) | 按角色验收清单与核心业务链路测试                  |
| [docs/功能清单.md](docs/功能清单.md)           | 功能模块清单                                      |

