import request from '@/utils/request'

export function addHouse(data) {
  return request({ url: '/community/house', method: 'post', data })
}

export function updateHouse(data) {
  return request({ url: '/community/house', method: 'put', data })
}

export function deleteHouse(id) {
  return request({ url: `/community/house/${id}`, method: 'delete' })
}

export function getHouseById(id) {
  return request({ url: `/community/house/${id}`, method: 'get' })
}

export function getHousePage(params) {
  return request({ url: '/community/house/page', method: 'get', params })
}
