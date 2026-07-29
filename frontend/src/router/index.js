import { createRouter, createWebHashHistory } from "vue-router"
import { constantRoutes } from "./routes"

const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

import { getToken } from "@/utils/auth"
import { useUserStore } from "@/store/modules/user"

const whiteList = ["/login", "/404", "/403"]

router.beforeEach((to, from, next) => {
  const token = getToken()

  if (token) {
    if (to.path === "/login") {
      next({ path: "/dashboard" })
    } else if (to.path === "/") {
      const userStore = useUserStore()
      const roles = userStore.roles || []
      if (roles.includes("admin")) next({ path: "/system/user" })
      else if (roles.includes("property")) next({ path: "/repair/order" })
      else if (roles.includes("finance")) next({ path: "/fee/bill" })
      else if (roles.includes("owner")) next({ path: "/fee/arrears" })
      else next({ path: "/dashboard" })
    } else {
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next({ path: "/login", query: { redirect: to.fullPath } })
    }
  }
})

export default router
