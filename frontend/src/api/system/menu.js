import request from '@/utils/request'

export function getMenuTree() {
  return request({ url: '/system/menu/tree', method: 'get' })
}

export function getUserMenus() {
  return request({ url: '/system/menu/user-tree', method: 'get' })
}

export function getMenuById(id) {
  return request({ url: `/system/menu/${id}`, method: 'get' })
}

export function addMenu(data) {
  return request({ url: '/system/menu', method: 'post', data })
}

export function updateMenu(data) {
  return request({ url: '/system/menu', method: 'put', data })
}

export function deleteMenu(id) {
  return request({ url: `/system/menu/${id}`, method: 'delete' })
}
