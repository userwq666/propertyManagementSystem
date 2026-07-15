import request from '@/utils/request'

export function getHouseList(params) {
  return request({
    url: '/property/house/list',
    method: 'get',
    params
  })
}

export function getHouseInfo(houseId) {
  return request({
    url: `/property/house/${houseId}`,
    method: 'get'
  })
}

export function addHouse(data) {
  return request({
    url: '/property/house',
    method: 'post',
    data
  })
}

export function updateHouse(data) {
  return request({
    url: '/property/house',
    method: 'put',
    data
  })
}

export function deleteHouse(houseIds) {
  return request({
    url: `/property/house/${houseIds}`,
    method: 'delete'
  })
}

export function changeHouseStatus(houseId, status) {
  return request({
    url: `/property/house/${houseId}/status/${status}`,
    method: 'put'
  })
}

export function exportHouse(params) {
  return request({
    url: '/property/house/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function getHouseOwnerList(params) {
  return request({
    url: '/property/house/owners',
    method: 'get',
    params
  })
}

export function bindHouseOwner(data) {
  return request({
    url: '/property/house/bindOwner',
    method: 'post',
    data
  })
}

export function unbindHouseOwner(data) {
  return request({
    url: '/property/house/unbindOwner',
    method: 'post',
    data
  })
}

export function getBuildingTree() {
  return request({
    url: '/community/building/tree',
    method: 'get'
  })
}