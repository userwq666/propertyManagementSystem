import request from '../utils/request'

export function getParkingPage(params) {
  return request({
    url: '/api/parking/page',
    method: 'get',
    params
  })
}

export function getParkingList() {
  return request({
    url: '/api/parking/list',
    method: 'get'
  })
}

export function getParkingById(id) {
  return request({
    url: `/api/parking/${id}`,
    method: 'get'
  })
}

export function addParking(data) {
  return request({
    url: '/api/parking',
    method: 'post',
    data
  })
}

export function updateParking(data) {
  return request({
    url: '/api/parking',
    method: 'put',
    data
  })
}

export function deleteParking(id) {
  return request({
    url: `/api/parking/${id}`,
    method: 'delete'
  })
}

export function updateParkingStatus(id, status, ownerId) {
  return request({
    url: '/api/parking/status',
    method: 'put',
    params: { id, status, ownerId }
  })
}
