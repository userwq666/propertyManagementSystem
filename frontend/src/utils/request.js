import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200 && res.code !== 0) {
      ElMessage({ message: res.msg || 'Error', type: 'error', duration: 5 * 1000 })
      if (res.code === 401 || res.code === 403) {
        localStorage.clear()
        location.href = '/login'
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res
  },
  error => {
    let message = error.message
    if (error.response) {
      switch (error.response.status) {
        case 400: message = '请求错误'; break
        case 401: message = '未授权，请重新登录'; break
        case 403: message = '拒绝访问'; break
        case 404: message = '请求地址出错'; break
        case 408: message = '请求超时'; break
        case 500: message = '服务器内部错误'; break
        case 501: message = '服务未实现'; break
        case 502: message = '网关错误'; break
        case 503: message = '服务不可用'; break
        case 504: message = '网关超时'; break
        case 505: message = 'HTTP版本不受支持'; break
        default: message = '连接错误'
      }
    } else {
      message = '网络连接异常'
    }
    ElMessage({ message, type: 'error', duration: 5 * 1000 })
    return Promise.reject(error)
  }
)

export default service