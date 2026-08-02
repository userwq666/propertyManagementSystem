import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200 || res.code === 0) {
      const method = (response.config.method || '').toLowerCase()
      if (['post', 'put', 'delete', 'patch'].includes(method)) {
        window.dispatchEvent(new CustomEvent('pms:data-changed'))
      }
      return res
    }
    if (res.code === 401) {
      removeToken()
      router.push('/login')
      return Promise.reject(new Error(res.msg || '认证失败'))
    }
    if (!response.config.silent) {
      ElMessage.error(res.msg || '请求失败')
    }
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  error => {
    if (error.response && error.response.status === 401) {
      removeToken()
      router.push('/login')
    }
    if (!error.config || !error.config.silent) {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
