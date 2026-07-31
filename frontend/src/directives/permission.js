import { watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

export default {
  mounted(el, binding) {
    const { value } = binding
    if (!value) return

    const userStore = useUserStore()
    const { permissions } = storeToRefs(userStore)
    const requiredPerms = Array.isArray(value) ? value : [value]

    const checkPermission = () => {
      // 权限尚未加载（空数组），先隐藏按钮等权限加载后再判断
      if (permissions.value.length === 0) {
        el.style.display = 'none'
        return
      }
      const has = requiredPerms.some(p => userStore.hasPermission(p))
      el.style.display = has ? '' : 'none'
    }

    checkPermission()
    watch(permissions, checkPermission)
  }
}
