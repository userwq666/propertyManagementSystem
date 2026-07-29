import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/statistics/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'users',
            name: 'SystemUsers',
            component: () => import('@/views/system/user/index.vue'),
            meta: { title: '用户管理', icon: 'User' }
          },
          {
            path: 'roles',
            name: 'SystemRoles',
            component: () => import('@/views/system/role/index.vue'),
            meta: { title: '角色管理', icon: 'Avatar' }
          },
          {
            path: 'menus',
            name: 'SystemMenus',
            component: () => import('@/views/system/menu/index.vue'),
            meta: { title: '菜单管理', icon: 'Menu' }
          },
          {
            path: 'operLogs',
            name: 'SystemOperLogs',
            component: () => import('@/views/system/operLog/index.vue'),
            meta: { title: '操作日志', icon: 'Document' }
          }
        ]
      },
      {
        path: 'community',
        name: 'Community',
        meta: { title: '小区基础', icon: 'OfficeBuilding' },
        children: [
          {
            path: 'buildings',
            name: 'CommunityBuildings',
            component: () => import('@/views/community/building/index.vue'),
            meta: { title: '楼栋管理', icon: 'Building' }
          },
          {
            path: 'houses',
            name: 'CommunityHouses',
            component: () => import('@/views/community/house/index.vue'),
            meta: { title: '房屋管理', icon: 'House' }
          },
          {
            path: 'owners',
            name: 'CommunityOwners',
            component: () => import('@/views/community/owner/index.vue'),
            meta: { title: '业主管理', icon: 'UserFilled' }
          },
          {
            path: 'parkings',
            name: 'CommunityParkings',
            component: () => import('@/views/community/parking/index.vue'),
            meta: { title: '车位管理', icon: 'Van' }
          }
        ]
      },
      {
        path: 'fee',
        name: 'Fee',
        meta: { title: '收费管理', icon: 'Money' },
        children: [
          {
            path: 'items',
            name: 'FeeItems',
            component: () => import('@/views/fee/item/index.vue'),
            meta: { title: '收费项目', icon: 'List' }
          },
          {
            path: 'notices',
            name: 'FeeNotices',
            component: () => import('@/views/fee/notice/index.vue'),
            meta: { title: '收费通知', icon: 'Bell' }
          },
          {
            path: 'records',
            name: 'FeeRecords',
            component: () => import('@/views/fee/record/index.vue'),
            meta: { title: '收费记录', icon: 'Tickets' }
          }
        ]
      },
      {
        path: 'equipment',
        name: 'Equipment',
        meta: { title: '设备管理', icon: 'Monitor' },
        children: [
          {
            path: 'categories',
            name: 'EquipmentCategories',
            component: () => import('@/views/equipment/category/index.vue'),
            meta: { title: '设备分类', icon: 'Collection' }
          },
          {
            path: 'equipments',
            name: 'Equipments',
            component: () => import('@/views/equipment/equipment/index.vue'),
            meta: { title: '设备信息', icon: 'Cpu' }
          },
          {
            path: 'maintenances',
            name: 'EquipmentMaintenances',
            component: () => import('@/views/equipment/maintenance/index.vue'),
            meta: { title: '维保记录', icon: 'Tools' }
          }
        ]
      },
      {
        path: 'repair',
        name: 'Repair',
        component: () => import('@/views/repair/index.vue'),
        meta: { title: '报修管理', icon: 'Wrench' }
      },
      {
        path: 'complaint',
        name: 'Complaint',
        component: () => import('@/views/complaint/index.vue'),
        meta: { title: '投诉建议', icon: 'ChatLineSquare' }
      },
      {
        path: 'announcement',
        name: 'Announcement',
        component: () => import('@/views/announcement/index.vue'),
        meta: { title: '公告通知', icon: 'Notification' }
      },
      {
        path: 'inspection',
        name: 'Inspection',
        meta: { title: '巡检管理', icon: 'Search' },
        children: [
          {
            path: 'plans',
            name: 'InspectionPlans',
            component: () => import('@/views/inspection/plan/index.vue'),
            meta: { title: '巡检计划', icon: 'Calendar' }
          },
          {
            path: 'records',
            name: 'InspectionRecords',
            component: () => import('@/views/inspection/record/index.vue'),
            meta: { title: '巡检记录', icon: 'Finished' }
          }
        ]
      }
    ]
  },
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '无权限' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? to.meta.title + ' - 物业管理系统' : '物业管理系统'
  if (to.path === '/login') {
    if (getToken()) {
      next('/')
    } else {
      next()
    }
  } else {
    if (!getToken()) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
