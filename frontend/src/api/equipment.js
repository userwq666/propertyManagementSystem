import request from '@/utils/request'

export function addEquipment(data) {
  return request({
    url: '/api/equipment',
    method: 'post',
    data
  })
}

export function updateEquipment(data) {
  return request({
    url: '/api/equipment',
    method: 'put',
    data
  })
}

export function deleteEquipment(id) {
  return request({
    url: `/api/equipment/${id}`,
    method: 'delete'
  })
}

export function getEquipment(id) {
  return request({
    url: `/api/equipment/${id}`,
    method: 'get'
  })
}

export function pageEquipment(params) {
  return request({
    url: '/api/equipment/page',
    method: 'get',
    params
  })
}

export function updateEquipmentStatus(params) {
  return request({
    url: '/api/equipment/status',
    method: 'put',
    params
  })
}
