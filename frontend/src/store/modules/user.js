import { defineStore } from "pinia"
import { login, logout, getInfo } from "@/api/user"
import { getToken, setToken, removeToken } from "@/utils/auth"
import router from "@/router"
import { usePermissionStore } from "@/store/modules/permission"

export const useUserStore = defineStore("user", {
  state: () => ({
    token: getToken(),
    name: "",
    avatar: "",
    roles: [],
    permissions: [],
    userId: "",
    deptId: "",
    email: "",
    phone: ""
  }),
  getters: {
    hasToken: state => !!state.token,
    isAdmin: state => state.roles.includes("admin")
  },
  actions: {
    async login(userInfo) {
      const { username, password } = userInfo
      const res = await login({ username: username.trim(), password })
      this.token = res.data.token
      setToken(this.token)
    },
    async getInfo() {
      const res = await getInfo()
      const data = res.data
      if (data.roles && data.roles.length > 0) {
        this.roles = data.roles
        this.permissions = data.permissions || []
      } else {
        this.roles = ["default"]
      }
      this.userId = data.userId || ""
      this.deptId = data.deptId || ""
      this.name = data.name || data.username || ""
      this.avatar = data.avatar || ""
      this.email = data.email || ""
      this.phone = data.phone || ""
    },
    async logout() {
      try { await logout() } catch {}
      this.resetState()
      const permissionStore = usePermissionStore()
      permissionStore.resetRouter()
      router.push("/login")
    },
    resetState() {
      this.token = ""
      this.roles = []
      this.permissions = []
      this.name = ""
      this.avatar = ""
      this.userId = ""
      this.deptId = ""
      this.email = ""
      this.phone = ""
      removeToken()
    }
  },
  persist: {
    key: "userStore",
    paths: ["token", "name", "avatar", "roles", "permissions", "userId", "deptId", "email", "phone"]
  }
})
