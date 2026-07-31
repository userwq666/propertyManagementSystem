import request from '@/utils/request'

export function addEquipment(data) {
  return request({ url: '/equipment', method: 'post', data })
}

export function updateEquipment(data) {
  return request({ url: '/equipment', method: 'put', data })
}

export function deleteEquipment(id) {
  return request({ url: `/equipment/${id}`, method: 'delete' })
}

export function getEquipmentById(id) {
  return request({ url: `/equipment/${id}`, method: 'get' })
}

export function getEquipmentPage(params, config = {}) {
  return request({ url: '/equipment/page', method: 'get', params, ...config })
}

export function updateEquipmentStatus(params) {
  return request({ url: '/equipment/status', method: 'put', params })
}
