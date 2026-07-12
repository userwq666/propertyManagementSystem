import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo, login as loginApi, logout as logoutApi } from '../api/user'
import { getToken, setToken, removeToken } from '../utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref({})
  const menuList = ref([])
  
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    token.value = res.data.token
    setToken(res.data.token)
    return res.data
  }
  
  async function getInfo() {
    const res = await getUserInfo()
    userInfo.value = res.data
    return res.data
  }
  
  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      userInfo.value = {}
      menuList.value = []
      removeToken()
    }
  }
  
  return {
    token,
    userInfo,
    menuList,
    login,
    getInfo,
    logout
  }
})