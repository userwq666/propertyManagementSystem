import request from '@/utils/request'

export function addEquipmentCategory(data) {
  return request({
    url: '/api/equipment/category',
    method: 'post',
    data
  })
}

export function updateEquipmentCategory(data) {
  return request({
    url: '/api/equipment/category',
    method: 'put',
    data
  })
}

export function deleteEquipmentCategory(id) {
  return request({
    url: `/api/equipment/category/${id}`,
    method: 'delete'
  })
}

export function getEquipmentCategory(id) {
  return request({
    url: `/api/equipment/category/${id}`,
    method: 'get'
  })
}

export function listEquipmentCategory() {
  return request({
    url: '/api/equipment/category/list',
    method: 'get'
  })
}
