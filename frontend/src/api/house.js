import request from '../utils/request'

export function getHousePage(params) {
  return request({
    url: '/api/house/page',
    method: 'get',
    params
  })
}

export function getHouseList() {
  return request({
    url: '/api/house/list',
    method: 'get'
  })
}

export function getHouseById(id) {
  return request({
    url: `/api/house/${id}`,
    method: 'get'
  })
}

export function addHouse(data) {
  return request({
    url: '/api/house',
    method: 'post',
    data
  })
}

export function updateHouse(data) {
  return request({
    url: '/api/house',
    method: 'put',
    data
  })
}

export function deleteHouse(id) {
  return request({
    url: `/api/house/${id}`,
    method: 'delete'
  })
}

export function updateHouseStatus(id, houseStatus) {
  return request({
    url: '/api/house/status',
    method: 'put',
    params: { id, houseStatus }
  })
}
