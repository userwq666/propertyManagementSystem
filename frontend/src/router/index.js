import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/login/index.vue'), meta: { title: '登录' } },
  {
    path: '/', component: () => import('@/layout/index.vue'), redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/welcome/index.vue'), meta: { title: '首页', icon: 'HomeFilled' } },
      { path: 'statistics', name: 'Statistics', component: () => import('@/views/statistics/index.vue'), meta: { title: '统计面板', icon: 'PieChart', permission: 'statistics:overview:list' } },
      { path: 'system', name: 'System', meta: { title: '系统管理', icon: 'Setting' },
        children: [
          { path: 'roles', name: 'SystemRoles', component: () => import('@/views/system/role/index.vue'), meta: { title: '角色管理', icon: 'Avatar', permission: 'system:role:list' } },
          { path: 'users', name: 'SystemUsers', component: () => import('@/views/system/user/index.vue'), meta: { title: '用户管理', icon: 'User', permission: 'system:user:list' } },
          { path: 'menus', name: 'SystemMenus', component: () => import('@/views/system/menu/index.vue'), meta: { title: '菜单管理', icon: 'Menu', permission: 'system:menu:list' } },
          { path: 'operLogs', name: 'SystemOperLogs', component: () => import('@/views/system/operLog/index.vue'), meta: { title: '操作日志', icon: 'Document', permission: 'system:operLog:list' } }
        ]
      },
      { path: 'community', name: 'Community', meta: { title: '社区基础', icon: 'OfficeBuilding' },
        children: [
          { path: 'buildings', name: 'CommunityBuildings', component: () => import('@/views/community/building/index.vue'), meta: { title: '楼栋管理', icon: 'HomeFilled', permission: 'community:building:list' } },
          { path: 'houses', name: 'CommunityHouses', component: () => import('@/views/community/house/index.vue'), meta: { title: '房屋管理', icon: 'House', permission: 'community:house:list' } },
          { path: 'owners', name: 'CommunityOwners', component: () => import('@/views/community/owner/index.vue'), meta: { title: '业主管理', icon: 'UserFilled', permission: 'community:owner:list' } },
          { path: 'parkings', name: 'CommunityParkings', component: () => import('@/views/community/parking/index.vue'), meta: { title: '车位管理', icon: 'Van', permission: 'community:parking:list' } }
        ]
      },
      { path: 'fee', name: 'Fee', meta: { title: '收费管理', icon: 'Money' },
        children: [
          { path: 'items', name: 'FeeItems', component: () => import('@/views/fee/item/index.vue'), meta: { title: '收费项目', icon: 'List', permission: 'fee:item:list' } },
          { path: 'notices', name: 'FeeNotices', component: () => import('@/views/fee/notice/index.vue'), meta: { title: '收费通知', icon: 'Bell', permission: 'fee:notice:list' } },
          { path: 'records', name: 'FeeRecords', component: () => import('@/views/fee/record/index.vue'), meta: { title: '收费记录', icon: 'Tickets', permission: 'fee:record:list' } },
          { path: 'expenses', name: 'FeeExpenses', component: () => import('@/views/fee/expense/index.vue'), meta: { title: '消费事项', icon: 'List', permission: 'fee:expense:list' } }
        ]
      },
      { path: 'equipment', name: 'Equipment', meta: { title: '设备管理', icon: 'Monitor' },
        children: [
          { path: 'categories', name: 'EquipmentCategories', component: () => import('@/views/equipment/category/index.vue'), meta: { title: '设备分类', icon: 'Collection', permission: 'equipment:category:list' } },
          { path: 'equipments', name: 'Equipments', component: () => import('@/views/equipment/equipment/index.vue'), meta: { title: '设备信息', icon: 'Cpu', permission: 'equipment:list:list' } },
          { path: 'records', name: 'EquipmentRecords', component: () => import('@/views/equipment/record/index.vue'), meta: { title: '设备记录', icon: 'Tools' } }
        ]
      },
      { path: 'repair', name: 'Repair', meta: { title: '报修管理', icon: 'Tools' },
        children: [
          { path: 'record', name: 'RepairRecords', component: () => import('@/views/repair/index.vue'), meta: { title: '报修工单', icon: 'Tickets', permission: 'repair:record:list' } }
        ]
      },
      { path: 'complaint', name: 'Complaint', component: () => import('@/views/complaint/index.vue'), meta: { title: '投诉建议', icon: 'ChatLineSquare', permission: 'complaint:list:list' } },
      { path: 'announcement', name: 'Announcement', component: () => import('@/views/announcement/index.vue'), meta: { title: '公告通知', icon: 'Notification', permission: 'announcement:list:list' } },
      { path: 'inspection', name: 'Inspection', meta: { title: '巡检管理', icon: 'Search' },
        children: [
          { path: 'plans', name: 'InspectionPlans', component: () => import('@/views/inspection/plan/index.vue'), meta: { title: '巡检计划', icon: 'Calendar', permission: 'inspection:plan:list' } },
          { path: 'records', name: 'InspectionRecords', component: () => import('@/views/inspection/record/index.vue'), meta: { title: '巡检记录', icon: 'Finished', permission: 'inspection:record:list' } }
        ]
      }
    ]
  },
  { path: '/403', name: '403', component: () => import('@/views/error/403.vue'), meta: { title: '无权限' } },
  { path: '/:pathMatch(.*)*', name: '404', component: () => import('@/views/error/404.vue'), meta: { title: '页面不存在' } }
]

const router = createRouter({ history: createWebHistory(), routes })

const whiteList = ['/login', '/403', '/404']

let userInfoReady = false
let userInfoPromise = null

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? to.meta.title + ' - 物业管理系统' : '物业管理系统'

  if (whiteList.includes(to.path)) {
    if (to.path === '/login' && getToken()) { next('/'); return }
    next(); return
  }

  if (!getToken()) {
    next('/login?redirect=' + to.path); return
  }

  const userStore = useUserStore()

  // 首次进入应用时调用 /api/auth/me 刷新用户信息与权限，避免旧权限快照
  if (!userInfoReady) {
    if (!userInfoPromise) {
      userInfoPromise = userStore.getUserInfo().then(() => {
        userInfoReady = true
      }).catch(() => {
        userInfoPromise = null
        next('/login?redirect=' + to.path)
      })
    }
    try {
      await userInfoPromise
    } catch (e) {
      return
    }
  }

  const requiredPermission = to.meta.permission
  if (requiredPermission && !userStore.hasPermission(requiredPermission)) {
    next('/403'); return
  }
  next()
})

export default router
