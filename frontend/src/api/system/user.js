import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/system/user/page',
    method: 'get',
    params
  })
}

export function getUserInfo(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

export function resetPassword(id, newPassword) {
  return request({
    url: '/system/user/password',
    method: 'put',
    params: { id, newPassword }
  })
}

export function updateStatus(id, status) {
  return request({
    url: '/system/user/status',
    method: 'put',
    params: { id, status }
  })
}

export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}
