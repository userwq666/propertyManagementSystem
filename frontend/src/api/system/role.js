import request from '@/utils/request'

export function getRolePage(params) {
  return request({ url: '/system/role/page', method: 'get', params })
}

export function getRoleById(id) {
  return request({ url: `/system/role/${id}`, method: 'get' })
}

export function addRole(data) {
  return request({ url: '/system/role', method: 'post', data })
}

export function updateRole(data) {
  return request({ url: '/system/role', method: 'put', data })
}

export function deleteRole(id) {
  return request({ url: `/system/role/${id}`, method: 'delete' })
}

export function getRoleMenus(roleId) {
  return request({ url: `/system/role/${roleId}/menus`, method: 'get' })
}

export function assignRoleMenus(roleId, menuIds) {
  return request({ url: `/system/role/${roleId}/menus`, method: 'put', data: menuIds })
}

export function getAllRoles() {
  return request({ url: '/system/role/all', method: 'get' })
}
