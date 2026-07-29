import request from '@/utils/request'

export function addParking(data) {
  return request({ url: '/community/parking', method: 'post', data })
}

export function updateParking(data) {
  return request({ url: '/community/parking', method: 'put', data })
}

export function deleteParking(id) {
  return request({ url: `/community/parking/${id}`, method: 'delete' })
}

export function getParkingById(id) {
  return request({ url: `/community/parking/${id}`, method: 'get' })
}

export function getParkingPage(params) {
  return request({ url: '/community/parking/page', method: 'get', params })
}
