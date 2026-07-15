import request from '@/utils/request'

export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

export function getRoleInfo(roleId) {
  return request({
    url: `/system/role/${roleId}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

export function deleteRole(roleIds) {
  return request({
    url: `/system/role/${roleIds}`,
    method: 'delete'
  })
}

export function getRoleMenuTreeselect(roleId) {
  return request({
    url: `/system/menu/roleMenuTreeselect/${roleId}`,
    method: 'get'
  })
}

export function exportRole(params) {
  return request({
    url: '/system/role/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}