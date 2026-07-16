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

export function deleteMenu(id) {
  return request({
    url: `/system/menu/${id}`,
    method: 'delete'
  })
}
