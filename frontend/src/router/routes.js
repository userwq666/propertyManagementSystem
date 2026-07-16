export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    hidden: true,
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    hidden: true,
    meta: { title: '404', noAuth: true }
  },
  {
    path: '/403',
    component: () => import('@/views/error/403.vue'),
    hidden: true,
    meta: { title: '403', noAuth: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/login',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 'HomeFilled', affix: true, roles: ['admin', 'property', 'finance', 'owner'] }
      }
    ]
  }
]

export const asyncRoutes = [
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    redirect: '/system/user',
    name: 'System',
    meta: { title: '系统管理', icon: 'Setting', roles: ['admin'] },
    children: [
      {
        path: 'user',
        component: () => import('@/views/system/user/index.vue'),
        name: 'SystemUser',
        meta: { title: '用户管理', icon: 'User', roles: ['admin'] }
      },
      {
        path: 'role',
        component: () => import('@/views/system/role/index.vue'),
        name: 'SystemRole',
        meta: { title: '角色管理', icon: 'Avatar', roles: ['admin'] }
      },
      {
        path: 'menu',
        component: () => import('@/views/system/menu/index.vue'),
        name: 'SystemMenu',
        meta: { title: '菜单管理', icon: 'Menu', roles: ['admin'] }
      },

    ]
  },
  {
    path: '/property',
    component: () => import('@/layout/index.vue'),
    redirect: '/property/house',
    name: 'Property',
    meta: { title: '房屋管理', icon: 'House', roles: ['admin', 'property'] },
    children: [
      {
        path: 'house',
        component: () => import('@/views/property/house/index.vue'),
        name: 'PropertyHouse',
        meta: { title: '房屋信息', icon: 'House', roles: ['admin', 'property'] }
      },
      {
        path: 'building',
        component: () => import('@/views/property/building/index.vue'),
        name: 'PropertyBuilding',
        meta: { title: '楼栋管理', icon: 'OfficeBuilding', roles: ['admin', 'property'] }
      },
      {
        path: 'room',
        component: () => import('@/views/property/room/index.vue'),
        name: 'PropertyRoom',
        meta: { title: '房间管理', icon: 'Grid', roles: ['admin', 'property'] }
      },
      {
        path: 'owner',
        component: () => import('@/views/property/owner/index.vue'),
        name: 'PropertyOwner',
        meta: { title: '业主管理', icon: 'UserFilled', roles: ['admin', 'property'] }
      },
      {
        path: 'tenant',
        component: () => import('@/views/property/tenant/index.vue'),
        name: 'PropertyTenant',
        meta: { title: '租户管理', icon: 'User', roles: ['admin', 'property'] }
      },
      {
        path: 'parking',
        component: () => import('@/views/property/parking/index.vue'),
        name: 'PropertyParking',
        meta: { title: '车位管理', icon: 'OfficeBuilding', roles: ['admin', 'property'] }
      }
    ]
  },
  {
    path: '/fee',
    component: () => import('@/layout/index.vue'),
    redirect: '/fee/bill',
    name: 'Fee',
    meta: { title: '费用管理', icon: 'Money', roles: ['admin', 'property', 'finance'] },
    children: [
      {
        path: 'bill',
        component: () => import('@/views/fee/bill/index.vue'),
        name: 'FeeBill',
        meta: { title: '账单管理', icon: 'DocumentChecked', roles: ['admin', 'property', 'finance'] }
      },
      {
        path: 'payment',
        component: () => import('@/views/fee/payment/index.vue'),
        name: 'FeePayment',
        meta: { title: '缴费记录', icon: 'Coin', roles: ['admin', 'property', 'finance'] }
      },
      {
        path: 'refund',
        component: () => import('@/views/fee/refund/index.vue'),
        name: 'FeeRefund',
        meta: { title: '退费管理', icon: 'Money', roles: ['admin', 'finance'] }
      },
      {
        path: 'item',
        component: () => import('@/views/fee/item/index.vue'),
        name: 'FeeItem',
        meta: { title: '收费项目', icon: 'List', roles: ['admin', 'finance'] }
      },
      {
        path: 'standard',
        component: () => import('@/views/fee/standard/index.vue'),
        name: 'FeeStandard',
        meta: { title: '收费标准', icon: 'ScaleToOriginal', roles: ['admin', 'finance'] }
      },
      {
        path: 'arrears',
        component: () => import('@/views/fee/arrears/index.vue'),
        name: 'FeeArrears',
        meta: { title: '欠费管理', icon: 'WarningFilled', roles: ['admin', 'property', 'finance'] }
      },
      {
        path: 'notice',
        component: () => import('@/views/fee/notice/index.vue'),
        name: 'FeeNotice',
        meta: { title: '缴费通知', icon: 'BellFilled', roles: ['admin', 'property', 'finance'] }
      },
      {
        path: 'record',
        component: () => import('@/views/fee/record/index.vue'),
        name: 'FeeRecord',
        meta: { title: '缴费记录管理', icon: 'Coin', roles: ['admin', 'property', 'finance'] }
      }
    ]
  },
  {
    path: '/repair',
    component: () => import('@/layout/index.vue'),
    redirect: '/repair/order',
    name: 'Repair',
    meta: { title: '报修管理', icon: 'Tools', roles: ['admin', 'property'] },
    children: [
      {
        path: 'order',
        component: () => import('@/views/repair/order/index.vue'),
        name: 'RepairOrder',
        meta: { title: '报修工单', icon: 'Tickets', roles: ['admin', 'property'] }
      },
      {
        path: 'dispatch',
        component: () => import('@/views/repair/dispatch/index.vue'),
        name: 'RepairDispatch',
        meta: { title: '派单管理', icon: 'SwitchButton', roles: ['admin', 'property'] }
      },
      {
        path: 'worker',
        component: () => import('@/views/repair/worker/index.vue'),
        name: 'RepairWorker',
        meta: { title: '维修人员', icon: 'User', roles: ['admin', 'property'] }
      },
      {
        path: 'evaluate',
        component: () => import('@/views/repair/evaluate/index.vue'),
        name: 'RepairEvaluate',
        meta: { title: '评价管理', icon: 'StarFilled', roles: ['admin', 'property'] }
      }
    ]
  },
  {
    path: '/complaint',
    component: () => import('@/layout/index.vue'),
    redirect: '/complaint/list',
    name: 'Complaint',
    meta: { title: '投诉建议', icon: 'ChatLineSquare', roles: ['admin', 'property'] },
    children: [
      {
        path: 'list',
        component: () => import('@/views/complaint/list/index.vue'),
        name: 'ComplaintList',
        meta: { title: '投诉列表', icon: 'Document', roles: ['admin', 'property'] }
      },
      {
        path: 'handle',
        component: () => import('@/views/complaint/handle/index.vue'),
        name: 'ComplaintHandle',
        meta: { title: '处理记录', icon: 'EditPen', roles: ['admin', 'property'] }
      },
      {
        path: 'feedback',
        component: () => import('@/views/complaint/feedback/index.vue'),
        name: 'ComplaintFeedback',
        meta: { title: '满意度反馈', icon: 'Star', roles: ['admin', 'property', 'owner'] }
      }
    ]
  },
  {
    path: '/announcement',
    component: () => import('@/layout/index.vue'),
    redirect: '/announcement/notice',
    name: 'Announcement',
    meta: { title: '公告通知', icon: 'Notification', roles: ['admin', 'property'] },
    children: [
      {
        path: 'notice',
        component: () => import('@/views/announcement/notice/index.vue'),
        name: 'AnnouncementNotice',
        meta: { title: '公告管理', icon: 'Notification', roles: ['admin', 'property'] }
      }
    ]
  },
  {
    path: '/notice',
    component: () => import('@/layout/index.vue'),
    redirect: '/notice/message',
    name: 'Notice',
    meta: { title: '消息通知', icon: 'MessageBox', roles: ['admin', 'property', 'owner'] },
    children: [
      {
        path: 'message',
        component: () => import('@/views/notice/message/index.vue'),
        name: 'NoticeMessage',
        meta: { title: '消息中心', icon: 'MessageBox', roles: ['admin', 'property', 'owner'] }
      },
      {
        path: 'template',
        component: () => import('@/views/notice/template/index.vue'),
        name: 'NoticeTemplate',
        meta: { title: '模板管理', icon: 'DocumentAdd', roles: ['admin', 'property'] }
      }
    ]
  },
  {
    path: '/parking',
    component: () => import('@/layout/index.vue'),
    redirect: '/parking/space',
    name: 'Parking',
    meta: { title: '车位管理', icon: 'OfficeBuilding', roles: ['admin', 'property'] },
    children: [
      {
        path: 'space',
        component: () => import('@/views/parking/space/index.vue'),
        name: 'ParkingSpace',
        meta: { title: '车位信息', icon: 'OfficeBuilding', roles: ['admin', 'property'] }
      },
      {
        path: 'rent',
        component: () => import('@/views/parking/rent/index.vue'),
        name: 'ParkingRent',
        meta: { title: '租赁管理', icon: 'Key', roles: ['admin', 'property'] }
      },
      {
        path: 'vehicle',
        component: () => import('@/views/parking/vehicle/index.vue'),
        name: 'ParkingVehicle',
        meta: { title: '车辆登记', icon: 'Van', roles: ['admin', 'property', 'owner'] }
      },
      {
        path: 'record',
        component: () => import('@/views/parking/record/index.vue'),
        name: 'ParkingRecord',
        meta: { title: '进出记录', icon: 'DocumentCopy', roles: ['admin', 'property'] }
      }
    ]
  },
  {
    path: '/statistics',
    component: () => import('@/layout/index.vue'),
    redirect: '/statistics/dashboard',
    name: 'Statistics',
    meta: { title: '统计分析', icon: 'DataAnalysis', roles: ['admin', 'property'] },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/statistics/dashboard/index.vue'),
        name: 'StatisticsDashboard',
        meta: { title: '统计仪表盘', icon: 'DataBoard', roles: ['admin', 'property'] }
      }
    ]
  },
  {
    path: '/profile',
    component: () => import('@/layout/index.vue'),
    redirect: '/profile/index',
    name: 'Profile',
    hidden: true,
    meta: { title: '个人中心', icon: 'User', roles: ['admin', 'property', 'finance', 'owner'] },
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index.vue'),
        name: 'ProfileIndex',
        meta: { title: '个人中心', icon: 'User', roles: ['admin', 'property', 'finance', 'owner'] }
      },
      {
        path: 'password',
        component: () => import('@/views/profile/password.vue'),
        name: 'ProfilePassword',
        meta: { title: '修改密码', icon: 'Lock', roles: ['admin', 'property', 'finance', 'owner'] }
      }
    ]
  }
]