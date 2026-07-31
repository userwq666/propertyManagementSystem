import request from '@/utils/request'

export function addRepair(data) {
  return request({ url: '/repair/record', method: 'post', data })
}

export function updateRepair(data) {
  return request({ url: '/repair/record', method: 'put', data })
}

export function deleteRepair(id) {
  return request({ url: `/repair/record/${id}`, method: 'delete' })
}

export function getRepairById(id) {
  return request({ url: `/repair/record/${id}`, method: 'get' })
}

export function getRepairPage(params) {
  return request({ url: '/repair/record/page', method: 'get', params })
}

export function updateRepairStatus(params) {
  return request({ url: '/repair/record/status', method: 'put', params })
}

export function updateRepairRating(params) {
  return request({ url: '/repair/record/rating', method: 'put', params })
}

export function getRepairHouses(params) {
  return request({ url: '/repair/record/houses', method: 'get', params })
}
