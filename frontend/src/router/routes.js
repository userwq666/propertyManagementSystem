export const constantRoutes = [
  {
    path: "/login",
    component: () => import("@/views/login/index.vue"),
    hidden: true,
    meta: { title: "登录", noAuth: true }
  },
  {
    path: "/404",
    component: () => import("@/views/error/404.vue"),
    hidden: true,
    meta: { title: "404", noAuth: true }
  },
  {
    path: "/403",
    component: () => import("@/views/error/403.vue"),
    hidden: true,
    meta: { title: "403", noAuth: true }
  },
  {
    path: "/",
    component: () => import("@/layout/index.vue"),
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        component: () => import("@/views/dashboard/index.vue"),
        name: "Dashboard",
        meta: { title: "首页", icon: "HomeFilled", affix: true, roles: ["admin", "property", "finance", "owner"] }
      }
    ]
  }
]

export const asyncRoutes = [
  {
    path: "/system",
    component: () => import("@/layout/index.vue"),
    redirect: "/system/user",
    name: "System",
    meta: { title: "系统管理", icon: "Setting", roles: ["admin"] },
    children: [
      { path: "user", component: () => import("@/views/system/user/index.vue"), name: "SystemUser", meta: { title: "用户管理", icon: "User", roles: ["admin"] } },
      { path: "role", component: () => import("@/views/system/role/index.vue"), name: "SystemRole", meta: { title: "角色管理", icon: "Avatar", roles: ["admin"] } },
      { path: "menu", component: () => import("@/views/system/menu/index.vue"), name: "SystemMenu", meta: { title: "菜单管理", icon: "Menu", roles: ["admin"] } }
    ]
  },
  {
    path: "/property",
    component: () => import("@/layout/index.vue"),
    redirect: "/property/house",
    name: "Property",
    meta: { title: "房屋管理", icon: "House", roles: ["admin", "property", "owner"] },
    children: [
      { path: "house", component: () => import("@/views/property/house/index.vue"), name: "PropertyHouse", meta: { title: "房屋信息", icon: "House", roles: ["admin", "property", "owner"] } },
      { path: "building", component: () => import("@/views/property/building/index.vue"), name: "PropertyBuilding", meta: { title: "楼栋管理", icon: "OfficeBuilding", roles: ["admin", "property"] } },
      { path: "owner", component: () => import("@/views/property/owner/index.vue"), name: "PropertyOwner", meta: { title: "业主管理", icon: "UserFilled", roles: ["admin", "property"] } },
      { path: "parking", component: () => import("@/views/property/parking/index.vue"), name: "PropertyParking", meta: { title: "车位管理", icon: "OfficeBuilding", roles: ["admin", "property"] } }
    ]
  },
  {
    path: "/fee",
    component: () => import("@/layout/index.vue"),
    redirect: "/fee/bill",
    name: "Fee",
    meta: { title: "费用管理", icon: "Money", roles: ["admin", "property", "finance", "owner"] },
    children: [
      { path: "bill", component: () => import("@/views/fee/bill/index.vue"), name: "FeeBill", meta: { title: "账单管理", icon: "DocumentChecked", roles: ["admin", "property", "finance", "owner"] } },
      { path: "payment", component: () => import("@/views/fee/payment/index.vue"), name: "FeePayment", meta: { title: "缴费记录", icon: "Coin", roles: ["admin", "property", "finance", "owner"] } },
      { path: "arrears", component: () => import("@/views/fee/arrears/index.vue"), name: "FeeArrears", meta: { title: "欠费管理", icon: "WarningFilled", roles: ["admin", "property", "finance", "owner"] } },
      { path: "item", component: () => import("@/views/fee/item/index.vue"), name: "FeeItem", meta: { title: "收费项目", icon: "List", roles: ["admin", "finance"] } },
      { path: "notice", component: () => import("@/views/fee/notice/index.vue"), name: "FeeNotice", meta: { title: "缴费通知", icon: "BellFilled", roles: ["admin", "property", "finance"] } }
    ]
  },
  {
    path: "/repair",
    component: () => import("@/layout/index.vue"),
    redirect: "/repair/order",
    name: "Repair",
    meta: { title: "报修管理", icon: "Tools", roles: ["admin", "property", "owner"] },
    children: [
      { path: "order", component: () => import("@/views/repair/order/index.vue"), name: "RepairOrder", meta: { title: "报修工单", icon: "Tickets", roles: ["admin", "property", "owner"] } }
    ]
  },
  {
    path: "/complaint",
    component: () => import("@/layout/index.vue"),
    redirect: "/complaint/suggestion",
    name: "Complaint",
    meta: { title: "投诉建议", icon: "ChatLineSquare", roles: ["admin", "property", "owner"] },
    children: [
      { path: "suggestion", component: () => import("@/views/complaint/suggestion/index.vue"), name: "ComplaintSuggestion", meta: { title: "投诉建议", icon: "ChatLineSquare", roles: ["admin", "property", "owner"] } }
    ]
  },
  {
    path: "/announcement",
    component: () => import("@/layout/index.vue"),
    redirect: "/announcement/notice",
    name: "Announcement",
    meta: { title: "公告通知", icon: "Notification", roles: ["admin", "property", "owner"] },
    children: [
      { path: "notice", component: () => import("@/views/announcement/notice/index.vue"), name: "AnnouncementNotice", meta: { title: "公告管理", icon: "Notification", roles: ["admin", "property", "owner"] } }
    ]
  },
  {
    path: "/statistics",
    component: () => import("@/layout/index.vue"),
    redirect: "/statistics/dashboard",
    name: "Statistics",
    meta: { title: "统计分析", icon: "DataAnalysis", roles: ["admin", "property"] },
    children: [
      { path: "dashboard", component: () => import("@/views/statistics/dashboard/index.vue"), name: "StatisticsDashboard", meta: { title: "统计仪表盘", icon: "DataBoard", roles: ["admin", "property"] } }
    ]
  },
  {
    path: "/equipment",
    component: () => import("@/layout/index.vue"),
    redirect: "/equipment/equipment",
    name: "Equipment",
    meta: { title: "设备管理", icon: "Tools", roles: ["admin", "property"] },
    children: [
      { path: "category", component: () => import("@/views/equipment/category/index.vue"), name: "EquipmentCategory", meta: { title: "设备分类", icon: "Collection", roles: ["admin", "property"] } },
      { path: "equipment", component: () => import("@/views/equipment/equipment/index.vue"), name: "EquipmentList", meta: { title: "设备列表", icon: "Monitor", roles: ["admin", "property"] } },
      { path: "maintenance", component: () => import("@/views/equipment/maintenance/index.vue"), name: "EquipmentMaintenance", meta: { title: "维保记录", icon: "Setting", roles: ["admin", "property"] } }
    ]
  },
  {
    path: "/inspection",
    component: () => import("@/layout/index.vue"),
    redirect: "/inspection/plan",
    name: "Inspection",
    meta: { title: "巡检管理", icon: "Monitor", roles: ["admin", "property"] },
    children: [
      { path: "plan", component: () => import("@/views/inspection/plan/index.vue"), name: "InspectionPlan", meta: { title: "巡检计划", icon: "Timer", roles: ["admin", "property"] } },
      { path: "record", component: () => import("@/views/inspection/record/index.vue"), name: "InspectionRecord", meta: { title: "巡检记录", icon: "DocumentCopy", roles: ["admin", "property"] } }
    ]
  },
  {
    path: "/profile",
    component: () => import("@/layout/index.vue"),
    redirect: "/profile/index",
    name: "Profile",
    hidden: true,
    meta: { title: "个人中心", icon: "User", roles: ["admin", "property", "finance", "owner"] },
    children: [
      { path: "index", component: () => import("@/views/profile/index.vue"), name: "ProfileIndex", meta: { title: "个人中心", icon: "User", roles: ["admin", "property", "finance", "owner"] } },
      { path: "password", component: () => import("@/views/profile/password.vue"), name: "ProfilePassword", meta: { title: "修改密码", icon: "Lock", roles: ["admin", "property", "finance", "owner"] } }
    ]
  }
]
