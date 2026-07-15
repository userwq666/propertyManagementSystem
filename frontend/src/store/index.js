import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

export default pinia

export { useAppStore } from './modules/app'
export { useUserStore } from './modules/user'
export { usePermissionStore } from './modules/permission'
export { useTagsViewStore } from './modules/tagsView'
export { useSettingsStore } from './modules/settings'