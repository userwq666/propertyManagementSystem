import { useUserStore } from '@/stores/user'

export function usePermission() {
  const userStore = useUserStore()

  function hasPermission(perm) {
    return userStore.hasPermission(perm)
  }

  function hasAnyPermission(...perms) {
    return perms.some(p => userStore.hasPermission(p))
  }

  function hasAllPermissions(...perms) {
    return perms.every(p => userStore.hasPermission(p))
  }

  return { hasPermission, hasAnyPermission, hasAllPermissions }
}
