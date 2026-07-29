import request from '@/utils/request'

export function getEquipmentPage(params) {
  return request({
    url: '/equipment/page',
    method: 'get',
    params
  })
}

export function getEquipmentInfo(id) {
  return request({
    url: '/equipment/' + id,
    method: 'get'
  })
}

export function addEquipment(data) {
  return request({
    url: '/equipment',
    method: 'post',
    data
  })
}

export function updateEquipment(data) {
  return request({
    url: '/equipment',
    method: 'put',
    data
  })
}

export function deleteEquipment(id) {
  return request({
    url: '/equipment/' + id,
    method: 'delete'
  })
}

export function updateEquipmentStatus(id, status) {
  return request({
    url: '/equipment/status',
    method: 'put',
    params: { id, status }
  })
}
