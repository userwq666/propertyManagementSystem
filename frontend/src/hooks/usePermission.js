import { useUserStore } from '@/store/modules/user'

export function usePermission() {
  const userStore = useUserStore()

  const hasPermission = (permissions) => {
    if (!permissions || !Array.isArray(permissions) || permissions.length === 0) {
      return true
    }
    const { roles, permissions: perms } = userStore
    if (roles.includes('admin')) {
      return true
    }
    return permissions.some(perm => perms.includes(perm))
  }

  return { hasPermission }
}