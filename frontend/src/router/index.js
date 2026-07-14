import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '../utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('../views/system/user/index.vue'),
        meta: { title: '用户管理', parent: '系统管理' }
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('../views/system/role/index.vue'),
        meta: { title: '角色管理', parent: '系统管理' }
      },
      {
        path: 'system/menu',
        name: 'SystemMenu',
        component: () => import('../views/system/menu/index.vue'),
        meta: { title: '菜单管理', parent: '系统管理' }
      },
      {
        path: 'system/operlog',
        name: 'SystemOperLog',
        component: () => import('../views/system/operlog/index.vue'),
        meta: { title: '操作日志', parent: '系统管理' }
      },
      {
        path: 'community/building',
        name: 'CommunityBuilding',
        component: () => import('../views/community/building/index.vue'),
        meta: { title: '楼栋管理', parent: '小区管理' }
      },
      {
        path: 'community/house',
        name: 'CommunityHouse',
        component: () => import('../views/community/house/index.vue'),
        meta: { title: '房屋管理', parent: '小区管理' }
      },
      {
        path: 'community/owner',
        name: 'CommunityOwner',
        component: () => import('../views/community/owner/index.vue'),
        meta: { title: '业主管理', parent: '小区管理' }
      },
      {
        path: 'community/parking',
        name: 'CommunityParking',
        component: () => import('../views/community/parking/index.vue'),
        meta: { title: '车位管理', parent: '小区管理' }
      },
      {
        path: 'repair',
        name: 'Repair',
        redirect: '/repair/record',
        meta: { title: '报修管理', icon: 'Tools' },
        children: [
          {
            path: 'record',
            name: 'RepairRecord',
            component: () => import('@/views/repair/record/index.vue'),
            meta: { title: '报修记录' }
          }
        ]
      },
      {
        path: 'fee',
        name: 'Fee',
        redirect: '/fee/item',
        meta: { title: '收费管理', icon: 'Money' },
        children: [
          {
            path: 'item',
            name: 'FeeItem',
            component: () => import('../views/fee/item/index.vue'),
            meta: { title: '收费项目', parent: '收费管理' }
          },
          {
            path: 'record',
            name: 'FeeRecord',
            component: () => import('../views/fee/record/index.vue'),
            meta: { title: '账单管理', parent: '收费管理' }
          },
          {
            path: 'payment',
            name: 'FeePayment',
            component: () => import('../views/fee/payment/index.vue'),
            meta: { title: '缴费记录', parent: '收费管理' }
          },
          {
            path: 'arrears',
            name: 'FeeArrears',
            component: () => import('../views/fee/arrears/index.vue'),
            meta: { title: '欠费统计', parent: '收费管理' }
          }
        ]
      },
      {
        path: 'complaint',
        name: 'Complaint',
        meta: { title: '投诉建议', icon: 'ChatDotRound' },
        children: [
          {
            path: 'suggest',
            name: 'ComplaintSuggest',
            component: () => import('@/views/complaint/suggest/index.vue'),
            meta: { title: '投诉建议列表' }
          },
          {
            path: 'suggest/add',
            name: 'ComplaintSuggestAdd',
            component: () => import('@/views/complaint/suggest/add.vue'),
            meta: { title: '新增投诉建议' }
          },
          {
            path: 'suggest/:id',
            name: 'ComplaintSuggestDetail',
            component: () => import('@/views/complaint/suggest/detail.vue'),
            meta: { title: '投诉建议详情' }
          }
        ]
      },
      {
        path: 'equipment/category',
        name: 'EquipmentCategory',
        component: () => import('../views/equipment/category/index.vue'),
        meta: { title: '设备分类', parent: '设备巡检' }
      },
      {
        path: 'equipment/device',
        name: 'Equipment',
        component: () => import('../views/equipment/device/index.vue'),
        meta: { title: '设备管理', parent: '设备巡检' }
      },
      {
        path: 'inspection/plan',
        name: 'InspectionPlan',
        component: () => import('../views/equipment/inspection/plan/index.vue'),
        meta: { title: '巡检计划', parent: '设备巡检' }
      },
      {
        path: 'inspection/plan/add',
        name: 'InspectionPlanAdd',
        component: () => import('../views/equipment/inspection/plan/add.vue'),
        meta: { title: '新增计划', parent: '巡检计划' }
      },
      {
        path: 'inspection/record',
        name: 'InspectionRecord',
        component: () => import('../views/equipment/inspection/record/index.vue'),
        meta: { title: '巡检记录', parent: '设备巡检' }
      },
      {
        path: 'inspection/record/add',
        name: 'InspectionRecordAdd',
        component: () => import('../views/equipment/inspection/record/add.vue'),
        meta: { title: '新增记录', parent: '巡检记录' }
      },
      {
        path: 'announcement',
        name: 'Announcement',
        component: () => import('../views/announcement/index.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'announcement/add',
        name: 'AnnouncementAdd',
        component: () => import('../views/announcement/add.vue'),
        meta: { title: '新增公告' }
      },
      {
        path: 'announcement/:id',
        name: 'AnnouncementDetail',
        component: () => import('../views/announcement/detail.vue'),
        meta: { title: '公告详情' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('../views/statistics/index.vue'),
        meta: { title: '数据统计', parent: '数据统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  const token = getToken()
  
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router