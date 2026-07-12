import request from '../utils/request'

export function getRoleList() {
  return request({
    url: '/role/list',
    method: 'get'
  })
}

export function getRoleById(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

export function assignMenus(roleId, menuIds) {
  return request({
    url: '/role/menu',
    method: 'put',
    params: { roleId },
    data: menuIds
  })
}

export function getRoleMenuIds(roleId) {
  return request({
    url: `/role/${roleId}/menus`,
    method: 'get'
  })
}