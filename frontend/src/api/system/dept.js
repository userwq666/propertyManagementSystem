import request from '@/utils/request'

export function getDeptList(params) {
  return request({
    url: '/system/dept/list',
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

export function getDeptInfo(deptId) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'get'
  })
}

export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

export function deleteDept(deptIds) {
  return request({
    url: `/system/dept/${deptIds}`,
    method: 'delete'
  })
}

export function exportDept(params) {
  return request({
    url: '/system/dept/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}