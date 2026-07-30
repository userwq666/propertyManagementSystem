import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo, getPermissions, setPermissions, removePermissions } from '@/utils/auth'
import router from '@/router'
import { login as loginApi, logout as logoutApi, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: getUserInfo() || {},
    permissions: getPermissions(),
    roles: []
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    realName: (state) => state.userInfo.realName || state.userInfo.username || '',
    avatar: (state) => state.userInfo.avatar || ''
  },
  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      const { token, ...userInfo } = res.data
      this.token = token
      this.userInfo = userInfo
      this.permissions = userInfo.permissions || []
      this.roles = userInfo.roles || []
      setToken(token)
      setUserInfo(userInfo)
      setPermissions(this.permissions)
      return res
    },
    async logout() {
      try { await logoutApi() } catch (e) { /* ignore */ }
      this.resetState()
    },
    async refreshUserInfo() {
      try {
        const res = await getCurrentUser()
        this.userInfo = res.data
        this.permissions = res.data.permissions || []
        this.roles = res.data.roles || []
        setUserInfo(res.data)
        setPermissions(this.permissions)
        return res
      } catch (e) {
        this.resetState()
        throw e
      }
    },
    resetState() {
      this.token = ''
      this.userInfo = {}
      this.permissions = []
      this.roles = []
      removeToken()
      removeUserInfo()
      removePermissions()
    },
    hasPermission(perm) {
      return this.permissions.includes(perm)
    }
  }
})