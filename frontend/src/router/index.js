import { createRouter, createWebHashHistory } from 'vue-router'
import { constantRoutes } from './routes'

const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

import { getToken } from '@/utils/auth'

const whiteList = ['/login', '/404', '/403']

router.beforeEach((to, from, next) => {
  if (getToken()) {
    if (to.path === '/login') {
      next({ path: '/dashboard' })
    } else {
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next({ path: '/login' })
    }
  }
})

export const resetRouter = () => {
  router.getRoutes().forEach(route => {
    if (route.name !== 'Login' && route.name !== '404' && route.name !== '403') {
      router.removeRoute(route.name)
    }
  })
}

export default router