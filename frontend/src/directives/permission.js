import { useUserStore } from '@/stores/user'

export default {
  mounted(el, binding) {
    const { value } = binding
    if (!value) return
    const userStore = useUserStore()
    // 支持单个权限字符串或数组
    const permissions = Array.isArray(value) ? value : [value]
    const hasPermission = permissions.some(p => userStore.hasPermission(p))
    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}
