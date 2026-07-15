import request from '@/utils/request'

export function getParkingList(params) {
  return request({
    url: '/property/parking/list',
    method: 'get',
    params
  })
}

export function getParkingInfo(parkingId) {
  return request({
    url: `/property/parking/${parkingId}`,
    method: 'get'
  })
}

export function addParking(data) {
  return request({
    url: '/property/parking',
    method: 'post',
    data
  })
}

export function updateParking(data) {
  return request({
    url: '/property/parking',
    method: 'put',
    data
  })
}

export function deleteParking(parkingIds) {
  return request({
    url: `/property/parking/${parkingIds}`,
    method: 'delete'
  })
}

export function changeParkingStatus(parkingId, status) {
  return request({
    url: `/property/parking/${parkingId}/status/${status}`,
    method: 'put'
  })
}

export function exportParking(params) {
  return request({
    url: '/property/parking/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function bindParkingOwner(data) {
  return request({
    url: '/property/parking/bindOwner',
    method: 'post',
    data
  })
}

export function unbindParkingOwner(data) {
  return request({
    url: '/property/parking/unbindOwner',
    method: 'post',
    data
  })
}

export function getParkingRentList(params) {
  return request({
    url: '/property/parking/rent/list',
    method: 'get',
    params
  })
}

export function getBuildingTree() {
  return request({
    url: '/community/building/tree',
    method: 'get'
  })
}

export function getHouseTree() {
  return request({
    url: '/community/house/tree',
    method: 'get'
  })
}