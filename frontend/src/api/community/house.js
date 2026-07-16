import request from '@/utils/request'

export function getHouseList(params) {
  return request({
    url: '/community/house/page',
    method: 'get',
    params
  })
}

export function getHouseInfo(houseId) {
  return request({
    url: `/community/house/${houseId}`,
    method: 'get'
  })
}

export function addHouse(data) {
  return request({
    url: '/community/house',
    method: 'post',
    data
  })
}

export function updateHouse(data) {
  return request({
    url: '/community/house',
    method: 'put',
    data
  })
}

export function deleteHouse(id) {
  return request({
    url: `/community/house/${houseId}`,
    method: 'delete'
  })
}

export function getHouseOwnerList(params) {
  return request({
    url: '/community/house/owners',
    method: 'get',
    params
  })
}

export function bindHouseOwner(data) {
  return request({
    url: '/community/house/bindOwner',
    method: 'post',
    data
  })
}

export function unbindHouseOwner(data) {
  return request({
    url: '/community/house/unbindOwner',
    method: 'post',
    data
  })
}
