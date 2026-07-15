import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    permissions: [],
    userId: '',
    deptId: '',
    email: '',
    phone: '',
    loginTime: 0
  }),
  getters: {
    hasToken: state => !!state.token,
    isAdmin: state => state.roles.includes('admin')
  },
  actions: {
    async login(userInfo) {
      const { username, password, code, uuid } = userInfo
      const res = await login({ username: username.trim(), password, code, uuid })
      this.token = res.data
      setToken(this.token)
    },
    async getInfo() {
      const res = await getInfo()
      const { roles, permissions, userId, deptId, name, avatar, email, phone, loginTime } = res.data
      if (roles && roles.length > 0) {
        this.roles = roles
        this.permissions = permissions
      } else {
        this.roles = ['default']
      }
      this.userId = userId
      this.deptId = deptId
      this.name = name
      this.avatar = avatar || 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'
      this.email = email
      this.phone = phone
      this.loginTime = loginTime
    },
    async logout() {
      await logout()
      this.resetState()
      router.push('/login')
    },
    resetState() {
      this.token = ''
      this.roles = []
      this.permissions = []
      this.name = ''
      this.avatar = ''
      this.userId = ''
      this.deptId = ''
      this.email = ''
      this.phone = ''
      this.loginTime = 0
      removeToken()
    },
    initUserInfo() {
      const stored = localStorage.getItem('userInfo')
      if (stored) {
        const info = JSON.parse(stored)
        this.name = info.name
        this.avatar = info.avatar
        this.roles = info.roles
        this.permissions = info.permissions
        this.userId = info.userId
        this.deptId = info.deptId
        this.email = info.email
        this.phone = info.phone
      }
    }
  },
  persist: {
    key: 'userStore',
    paths: ['token', 'name', 'avatar', 'roles', 'permissions', 'userId', 'deptId', 'email', 'phone', 'loginTime']
  }
})