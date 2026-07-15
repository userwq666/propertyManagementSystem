import request from '@/utils/request'

export function getBuildingList(params) {
  return request({
    url: '/property/building/list',
    method: 'get',
    params
  })
}

export function getBuildingInfo(buildingId) {
  return request({
    url: `/property/building/${buildingId}`,
    method: 'get'
  })
}

export function addBuilding(data) {
  return request({
    url: '/property/building',
    method: 'post',
    data
  })
}

export function updateBuilding(data) {
  return request({
    url: '/property/building',
    method: 'put',
    data
  })
}

export function deleteBuilding(buildingIds) {
  return request({
    url: `/property/building/${buildingIds}`,
    method: 'delete'
  })
}

export function changeBuildingStatus(buildingId, status) {
  return request({
    url: `/property/building/${buildingId}/status/${status}`,
    method: 'put'
  })
}

export function exportBuilding(params) {
  return request({
    url: '/property/building/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function getBuildingRoomList(params) {
  return request({
    url: '/property/building/rooms',
    method: 'get',
    params
  })
}