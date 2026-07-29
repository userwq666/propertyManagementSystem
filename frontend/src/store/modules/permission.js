import { defineStore } from "pinia"
import { constantRoutes, asyncRoutes } from "@/router/routes"
import router from "@/router"

export const usePermissionStore = defineStore("permission", {
  state: () => ({
    routes: [],
    addRoutes: [],
    sidebarRouters: []
  }),
  getters: {
    allRoutes: (state) => constantRoutes.concat(state.routes),
    getAddRoutes: (state) => state.addRoutes,
    getSidebarRouters: (state) => state.sidebarRouters
  },
  actions: {
    generateRoutes(roles = []) {
      return new Promise(resolve => {
        const accessedRoutes = this.filterAsyncRoutes(asyncRoutes, roles)
        this.routes = accessedRoutes
        this.addRoutes = accessedRoutes
        this.sidebarRouters = constantRoutes.concat(accessedRoutes)
        resolve(accessedRoutes)
      })
    },
    filterAsyncRoutes(routes, roles) {
      const res = []
      routes.forEach(route => {
        const tmp = { ...route }
        if (this.hasPermission(tmp, roles)) {
          if (tmp.children) {
            tmp.children = this.filterAsyncRoutes(tmp.children, roles)
          }
          res.push(tmp)
        }
      })
      return res
    },
    hasPermission(route, roles) {
      if (route.meta && route.meta.roles) {
        return roles.some(role => route.meta.roles.includes(role))
      }
      return true
    },
    resetRouter() {
      // Remove all dynamically added routes from Vue Router
      this.addRoutes.forEach(route => {
        if (route.name && router.hasRoute(route.name)) {
          router.removeRoute(route.name)
        }
      })
      this.routes = []
      this.addRoutes = []
      this.sidebarRouters = []
    }
  }
})
