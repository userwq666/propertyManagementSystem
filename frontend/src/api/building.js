import request from '../utils/request'

export function getBuildingPage(params) {
  return request({
    url: '/api/building/page',
    method: 'get',
    params
  })
}

export function getBuildingList() {
  return request({
    url: '/api/building/list',
    method: 'get'
  })
}

export function getBuildingById(id) {
  return request({
    url: `/api/building/${id}`,
    method: 'get'
  })
}

export function addBuilding(data) {
  return request({
    url: '/api/building',
    method: 'post',
    data
  })
}

export function updateBuilding(data) {
  return request({
    url: '/api/building',
    method: 'put',
    data
  })
}

export function deleteBuilding(id) {
  return request({
    url: `/api/building/${id}`,
    method: 'delete'
  })
}
