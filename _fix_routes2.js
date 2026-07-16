const fs = require("fs");
let c = fs.readFileSync("frontend/src/router/routes.js", "utf8");

// Define block patterns to remove - from "{path: 'dept'" to the closing "},""
const removeBlocks = [
  "{path: 'dept',\n        component: () => import('@/views/system/dept/index.vue'),\n        name: 'SystemDept',\n        meta: { title: '部门管理', icon: 'OfficeBuilding', roles: ['admin'] }\n      },",
  "{path: 'dict',\n        component: () => import('@/views/system/dict/index.vue'),\n        name: 'SystemDict',\n        meta: { title: '字典管理', icon: 'Document', roles: ['admin'] }\n      },",
  "{path: 'dict/data/:dictType',\n        component: () => import('@/views/system/dict/data.vue'),\n        name: 'SystemDictData',\n        hidden: true,\n        meta: { title: '字典数据', icon: 'Document', roles: ['admin'], activeMenu: '/system/dict' }\n      },",
  "{path: 'config',\n        component: () => import('@/views/system/config/index.vue'),\n        name: 'SystemConfig',\n        meta: { title: '参数配置', icon: 'Tools', roles: ['admin'] }\n      },",
  "{path: 'log/login',\n        component: () => import('@/views/system/log/login.vue'),\n        name: 'SystemLogLogin',\n        meta: { title: '登录日志', icon: 'List', roles: ['admin'] }\n      },",
  "{path: 'log/operation',\n        component: () => import('@/views/system/log/operation.vue'),\n        name: 'SystemLogOperation',\n        meta: { title: '操作日志', icon: 'DocumentCopy', roles: ['admin'] }\n      },"
];

// Normalize whitespace in the file for matching
removeBlocks.forEach(block => {
  const normalizedBlock = block.replace(/\s+/g, "\\s*");
  c = c.replace(new RegExp(normalizedBlock, "g"), "");
});

fs.writeFileSync("frontend/src/router/routes.js", c, "utf8");
console.log("done");
