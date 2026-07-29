import request from '@/utils/request'

export function login(data) {
  return request({ url: '/auth/login', method: 'post', data })
}

export function logout() {
  return request({ url: '/auth/logout', method: 'post' })
}

export function getCurrentUser() {
  return request({ url: '/auth/me', method: 'get' })
}

