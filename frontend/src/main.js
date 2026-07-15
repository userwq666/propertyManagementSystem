import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import App from './App.vue'
import router from './router'
import { usePermissionStore } from './store/modules/permission'
import { useUserStore } from './store/modules/user'
import directives from './directives'
import plugins from './plugins'
import '@/assets/styles/index.scss'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { size: 'default', zIndex: 3000, locale: zhCn })
app.use(directives)
app.use(plugins)

const permissionStore = usePermissionStore()
const userStore = useUserStore()

permissionStore.generateRoutes().then(() => {
  permissionStore.addRoutes.forEach(route => {
    router.addRoute(route)
  })
  router.addRoute({ path: '/:pathMatch(.*)*', redirect: '/404' })
  app.mount('#app')
})

userStore.initUserInfo()