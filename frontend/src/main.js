import { createApp } from "vue"
import ElementPlus from "element-plus"
import "element-plus/dist/index.css"
import * as ElementPlusIconsVue from "@element-plus/icons-vue"
import zhCn from "element-plus/es/locale/lang/zh-cn"
import App from "./App.vue"
import router from "./router"
import directives from "./directives"
import plugins from "./plugins"
import "@/assets/styles/index.scss"
import pinia from "@/store"
import { getToken } from "@/utils/auth"

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus, { size: "default", zIndex: 3000, locale: zhCn })
app.use(directives)
app.use(plugins)

import { useUserStore } from "@/store/modules/user"
import { usePermissionStore } from "@/store/modules/permission"

async function bootstrap() {
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  if (getToken()) {
    try {
      await userStore.getInfo()
    } catch {
      userStore.resetState()
    }
  }

  const roles = userStore.roles.length > 0 ? [...userStore.roles] : []
  permissionStore.resetRouter(); await permissionStore.generateRoutes(roles)

  permissionStore.addRoutes.forEach(route => {
    router.addRoute(route)
  })
  router.addRoute({ path: "/:pathMatch(.*)*", redirect: "/404" })

  app.mount("#app")
}

bootstrap()

