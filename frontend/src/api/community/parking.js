import request from '@/utils/request'

export function getParkingList(params) {
  return request({
    url: '/community/parking/page',
    method: 'get',
    params
  })
}

export function getParkingInfo(parkingId) {
  return request({
    url: `/community/parking/${parkingId}`,
    method: 'get'
  })
}

export function addParking(data) {
  return request({
    url: '/community/parking',
    method: 'post',
    data
  })
}

export function updateParking(data) {
  return request({
    url: '/community/parking',
    method: 'put',
    data
  })
}

export function deleteParking(id) {
  return request({
    url: `/community/parking/${parkingId}`,
    method: 'delete'
  })
}

export function bindParkingOwner(data) {
  return request({
    url: '/community/parking/bindOwner',
    method: 'post',
    data
  })
}

export function unbindParkingOwner(data) {
  return request({
    url: '/community/parking/unbindOwner',
    method: 'post',
    data
  })
}

export function getParkingRentList(params) {
  return request({
    url: '/community/parking/rent/page',
    method: 'get',
    params
  })
}
