import request from '@/utils/request'

export function getBuildingList(params) {
  return request({
    url: '/community/building/page',
    method: 'get',
    params
  })
}

export function getBuildingInfo(buildingId) {
  return request({
    url: `/community/building/${buildingId}`,
    method: 'get'
  })
}

export function addBuilding(data) {
  return request({
    url: '/community/building',
    method: 'post',
    data
  })
}

export function updateBuilding(data) {
  return request({
    url: '/community/building',
    method: 'put',
    data
  })
}

export function deleteBuilding(id) {
  return request({
    url: `/community/building/${buildingId}`,
    method: 'delete'
  })
}

export function getBuildingRoomList(params) {
  return request({
    url: '/community/building/rooms',
    method: 'get',
    params
  })
}
