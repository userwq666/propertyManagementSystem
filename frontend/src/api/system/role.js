import request from '@/utils/request'

export function addRole(data) {
  return request({ url: '/system/role', method: 'post', data })
}

export function updateRole(data) {
  return request({ url: '/system/role', method: 'put', data })
}

export function deleteRole(id) {
  return request({ url: '/system/role/' + id, method: 'delete' })
}

export function getRoleById(id) {
  return request({ url: '/system/role/' + id, method: 'get' })
}

export function getRoleList() {
  return request({ url: '/system/role/list', method: 'get' })
}

export function getRoleOptions() {
  return request({ url: '/system/role/options', method: 'get' })
}

export function assignMenus(roleId, menuIds) {
  return request({ url: '/system/role/assignMenus', method: 'post', params: { roleId }, data: menuIds })
}

export function getRoleMenus(roleId) {
  return request({ url: '/system/role/' + roleId + '/menus', method: 'get' })
}
