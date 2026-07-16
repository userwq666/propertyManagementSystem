const fs = require("fs");
let c = fs.readFileSync("frontend/src/router/routes.js", "utf8");

// Replace the entire system section children to only include user, role, menu
const oldChildren = `    children: [
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
      {
        path: 'dept',
        component: () => import('@/views/system/dept/index.vue'),
        name: 'SystemDept',
        meta: { title: '部门管理', icon: 'OfficeBuilding', roles: ['admin'] }
      },
      {
        path: 'dict',
        component: () => import('@/views/system/dict/index.vue'),
        name: 'SystemDict',
        meta: { title: '字典管理', icon: 'Document', roles: ['admin'] }
      },
      {
        path: 'dict/data/:dictType',
        component: () => import('@/views/system/dict/data.vue'),
        name: 'SystemDictData',
        hidden: true,
        meta: { title: '字典数据', icon: 'Document', roles: ['admin'], activeMenu: '/system/dict' }
      },
      {
        path: 'config',
        component: () => import('@/views/system/config/index.vue'),
        name: 'SystemConfig',
        meta: { title: '参数配置', icon: 'Tools', roles: ['admin'] }
      },
      {
        path: 'log/login',
        component: () => import('@/views/system/log/login.vue'),
        name: 'SystemLogLogin',
        meta: { title: '登录日志', icon: 'List', roles: ['admin'] }
      },
      {
        path: 'log/operation',
        component: () => import('@/views/system/log/operation.vue'),
        name: 'SystemLogOperation',
        meta: { title: '操作日志', icon: 'DocumentCopy', roles: ['admin'] }
      }
    ]`;

const newChildren = `    children: [
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
      }
    ]`;

c = c.replace(oldChildren, newChildren);

fs.writeFileSync("frontend/src/router/routes.js", c, "utf8");
console.log("routes fixed");
