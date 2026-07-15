# 物业管理系统前端设计文档

**版本**: v1.0
**日期**: 2026-07-15
**状态**: 已批准

---

## 1. 项目概述

为现有 Spring Boot 物业管理系统后端开发 Vue 3 前端，采用 RuoYi-Vue3 风格架构，实现 9 个业务模块的完整前端界面。

**技术栈**：
- Vue 3.4+ (Composition API + `<script setup>`)
- Vite 5.x
- Element Plus 2.7+
- Vue Router 4
- Pinia 2.1+
- Axios + 统一封装
- ECharts 5.5+

---

## 2. 整体架构

### 2.1 目录结构

```
frontend/
├── public/
├── src/
│   ├── api/              # API接口层（按模块分文件夹）
│   ├── assets/           # 静态资源
│   ├── components/       # 通用组件
│   ├── directives/       # 自定义指令
│   ├── hooks/            # 组合式函数
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia状态
│   ├── styles/           # 全局样式
│   ├── utils/            # 工具函数
│   ├── views/            # 页面视图（按模块分文件夹）
│   ├── App.vue
│   └── main.js
├── .env.development
├── .env.production
├── vite.config.js
└── package.json
```

### 2.2 业务模块映射

| 后端模块 | 前端路由前缀 | 页面列表 |
|---------|-------------|---------|
| system | `/system` | 用户、角色、菜单、部门、字典、操作日志、登录日志 |
| community | `/community` | 楼栋、房屋、业主、车位 |
| fee | `/fee` | 收费项目、缴费通知、缴费记录、收费统计 |
| equipment | `/equipment` | 设备分类、设备台账、维修保养计划、维修记录 |
| repair | `/repair` | 报修工单、派单处理、进度跟踪、评价管理 |
| complaint | `/complaint` | 投诉建议、处理回复、满意度统计 |
| inspection | `/inspection` | 巡检计划、巡检记录、异常处理、巡检统计 |
| announcement | `/announcement` | 公告列表、发布公告、阅读统计 |
| statistics | `/statistics` | 仪表盘、费用收缴率、报修响应率、设备完好率 |

---

## 3. API 接口层设计

### 3.1 文件组织

```
src/api/
├── system/
│   ├── user.js
│   ├── role.js
│   ├── menu.js
│   ├── dept.js
│   └── dict.js
├── community/
│   ├── building.js
│   ├── house.js
│   ├── owner.js
│   └── parking.js
├── fee/
│   ├── feeItem.js
│   ├── feeNotice.js
│   └── feeRecord.js
├── equipment/
│   ├── category.js
│   ├── equipment.js
│   └── maintenance.js
├── repair/
│   └── repairRecord.js
├── complaint/
│   └── complaint.js
├── inspection/
│   ├── plan.js
│   └── record.js
├── announcement/
│   └── announcement.js
└── statistics/
    └── statistics.js
```

### 3.2 请求封装规范 (`src/utils/request.js`)

- `baseURL`: `import.meta.env.VITE_APP_BASE_API` (`/dev-api`)
- 请求拦截：自动携带 Token、请求去重
- 响应拦截：统一错误码处理（401 跳转登录、500 提示错误）
- 导出方法：`get, post, put, del, download`

---

## 4. 路由与权限设计

### 4.1 路由结构

```
src/router/
├── index.js
├── routes/
│   ├── constant.js          # 静态路由
│   └── modules/             # 业务路由模块
│       ├── system.js
│       ├── community.js
│       ├── fee.js
│       └── ...
└── guard.js                 # 路由守卫
```

### 4.2 权限控制

- **按钮级**：`v-permission` 指令（`permission: 'system:user:add'`）
- **路由级**：`meta.roles` + 后端动态菜单生成路由
- **页面级**：路由守卫拦截未授权访问

---

## 5. 状态管理设计

```
src/stores/
├── user.js          # 用户信息、Token、权限码
├── tagsView.js      # 标签页缓存、历史记录
├── settings.js      # 系统设置（主题、标签栏、水印）
├── permission.js    # 路由权限、动态菜单
└── app.js           # 全局状态（侧边栏折叠、加载态）
```

---

## 6. 通用组件库

| 组件 | 用途 | 复用场景 |
|------|------|----------|
| `Table/` | 表格封装（分页、查询、导出、选择） | 所有列表页 |
| `Form/` | 表单封装（验证、布局、字典回显） | 新增/编辑弹窗 |
| `Search/` | 查询条件组件（重置、折叠、日期快捷） | 列表页顶部 |
| `DictTag/` | 字典标签展示（颜色、点击跳转） | 状态、类型字段 |
| `FileUpload/` | 文件上传（图片预览、拖拽、进度） | 公告附件、业主证件 |
| `ImagePreview/` | 图片预览（缩放、旋转、下载） | 房屋图片、报修图片 |
| `TreeSelect/` | 树形选择器（部门、楼栋、菜单） | 部门选择、楼栋关联 |
| `Pagination/` | 分页组件 | 列表页底部 |

---

## 7. 环境配置与构建

### 7.1 环境变量

| 文件 | 关键配置 |
|------|---------|
| `.env.development` | `VITE_APP_BASE_API=/dev-api`, `VITE_APP_ENV=development` |
| `.env.production` | `VITE_APP_BASE_API=/api`, `VITE_APP_ENV=production` |

### 7.2 Vite 关键配置

- `@` 别名指向 `src`
- `/dev-api` 代理到 `http://localhost:8080`
- Gzip 压缩、Chunk 分割、SourceMap 关闭

---

## 8. 实施顺序建议

1. **项目初始化** - 脚手架、依赖、配置文件
2. **基础设施** - 请求封装、路由、状态、权限指令、布局组件
3. **通用组件** - Table、Form、Search、DictTag 等
4. **系统管理模块** - 登录、用户/角色/菜单/部门/字典管理（核心权限基础）
5. **业务模块并行开发** - community → fee → equipment → repair → complaint → inspection → announcement
6. **统计大屏** - 仪表盘、ECharts 图表
7. **构建优化与部署** - 环境配置、Nginx 配置

---

## 9. 验收标准

- [ ] 所有 9 个业务模块 CRUD 完整可用
- [ ] RBAC 权限控制生效（菜单、按钮、数据权限）
- [ ] 登录/登出/Token刷新流程正常
- [ ] 标签页缓存、面包屑、多标签操作正常
- [ ] 响应式布局适配（≥1366px）
- [ ] 生产构建产物 < 2MB (gzipped)
- [ ] 无 ESLint/TypeScript 错误