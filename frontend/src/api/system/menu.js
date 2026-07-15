import request from '@/utils/request'

export function getMenuList(params) {
  return request({
    url: '/system/menu/list',
    method: 'get',
    params
  })
}

export function getMenuTree(params) {
  return request({
    url: '/system/menu/tree',
    method: 'get',
    params
  })
}

export function getMenuInfo(menuId) {
  return request({
    url: `/system/menu/${menuId}`,
    method: 'get'
  })
}

export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

export function deleteMenu(menuIds) {
  return request({
    url: `/system/menu/${menuIds}`,
    method: 'delete'
  })
}

export function getMenuTreeselect(params) {
  return request({
    url: '/system/menu/treeselect',
    method: 'get',
    params
  })
}

export function getRoleMenuTreeselect(roleId) {
  return request({
    url: `/system/menu/roleMenuTreeselect/${roleId}`,
    method: 'get'
  })
}

export function exportMenu(params) {
  return request({
    url: '/system/menu/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}