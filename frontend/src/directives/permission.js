import { usePermissionStore } from '@/store/modules/permission'
import { useUserStore } from '@/store/modules/user'

export default {
  mounted(el, binding) {
    const permissionStore = usePermissionStore()
    const userStore = useUserStore()

    const { value } = binding
    const roles = userStore.roles
    const perms = userStore.permissions

    if (value && value instanceof Array && value.length > 0) {
      const hasPermission = value.some(role => {
        return roles.includes(role) || perms.includes(role)
      })

      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else if (value && typeof value === 'string') {
      const hasPermission = roles.includes(value) || perms.includes(value)
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      console.error('v-permission 需要传入权限标识数组或字符串')
    }
  }
}