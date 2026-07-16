c = open("src/router/routes.js", "r", encoding="utf-8").read()

route_block = """,
  {
    path: '/equipment',
    component: () => import('@/layout/index.vue'),
    redirect: '/equipment/equipment',
    name: 'Equipment',
    meta: { title: '\u8bbe\u5907\u7ba1\u7406', icon: 'Tools', roles: ['admin', 'property'] },
    children: [
      {
        path: 'category',
        component: () => import('@/views/equipment/category/index.vue'),
        name: 'EquipmentCategory',
        meta: { title: '\u8bbe\u5907\u5206\u7c7b', icon: 'Collection', roles: ['admin', 'property'] }
      },
      {
        path: 'equipment',
        component: () => import('@/views/equipment/equipment/index.vue'),
        name: 'EquipmentList',
        meta: { title: '\u8bbe\u5907\u5217\u8868', icon: 'Monitor', roles: ['admin', 'property'] }
      },
      {
        path: 'maintenance',
        component: () => import('@/views/equipment/maintenance/index.vue'),
        name: 'EquipmentMaintenance',
        meta: { title: '\u7ef4\u4fdd\u8bb0\u5f55', icon: 'Setting', roles: ['admin', 'property'] }
      }
    ]
  }"""

idx = c.rfind("  {\n    path: '/profile'")
c = c[:idx] + route_block + c[idx:]
open("src/router/routes.js", "w", encoding="utf-8").write(c)
print("Route added")
