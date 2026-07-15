import { createRouter, createWebHashHistory } from 'vue-router'
import { constantRoutes } from './routes'

const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

export const resetRouter = () => {
  router.getRoutes().forEach(route => {
    if (route.name !== 'Login' && route.name !== '404' && route.name !== '403') {
      router.removeRoute(route.name)
    }
  })
}

export default router