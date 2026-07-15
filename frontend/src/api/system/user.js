import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/system/user/list',
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

export function deleteUser(userIds) {
  return request({
    url: `/system/user/${userIds}`,
    method: 'delete'
  })
}

export function resetPassword(userId, password) {
  return request({
    url: `/system/user/resetPwd`,
    method: 'put',
    data: { userId, password }
  })
}

export function updateStatus(userId, status) {
  return request({
    url: `/system/user/changeStatus`,
    method: 'put',
    data: { userId, status }
  })
}

export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

export function getDeptTree(params) {
  return request({
    url: '/system/dept/tree',
    method: 'get',
    params
  })
}

export function exportUser(params) {
  return request({
    url: '/system/user/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function importUser(data) {
  return request({
    url: '/system/user/importData',
    method: 'post',
    data
  })
}

export function getImportTemplate() {
  return request({
    url: '/system/user/importTemplate',
    method: 'get',
    responseType: 'blob'
  })
}