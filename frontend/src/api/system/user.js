import request from '@/utils/request'

export function addUser(data) {
  return request({ url: '/system/user', method: 'post', data })
}

export function updateUser(data) {
  return request({ url: '/system/user', method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: '/system/user/' + id, method: 'delete' })
}

export function getUserById(id) {
  return request({ url: '/system/user/' + id, method: 'get' })
}

export function getUserPage(params) {
  return request({ url: '/system/user/page', method: 'get', params })
}

export function updateUserStatus(params) {
  return request({ url: '/system/user/status', method: 'put', params })
}

export function resetPassword(data) {
  return request({ url: '/system/user/password', method: 'put', data })
}

export function getOwnerUsers() {
  return request({ url: '/system/user/owners', method: 'get' })
}