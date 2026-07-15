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

export function getRoomList(params) {
  return request({
    url: '/property/room/list',
    method: 'get',
    params
  })
}

export function getRoomInfo(roomId) {
  return request({
    url: `/property/room/${roomId}`,
    method: 'get'
  })
}

export function addRoom(data) {
  return request({
    url: '/property/room',
    method: 'post',
    data
  })
}

export function updateRoom(data) {
  return request({
    url: '/property/room',
    method: 'put',
    data
  })
}

export function deleteRoom(roomIds) {
  return request({
    url: `/property/room/${roomIds}`,
    method: 'delete'
  })
}

export function getOwnerList(params) {
  return request({
    url: '/property/owner/list',
    method: 'get',
    params
  })
}

export function getOwnerInfo(ownerId) {
  return request({
    url: `/property/owner/${ownerId}`,
    method: 'get'
  })
}

export function addOwner(data) {
  return request({
    url: '/property/owner',
    method: 'post',
    data
  })
}

export function updateOwner(data) {
  return request({
    url: '/property/owner',
    method: 'put',
    data
  })
}

export function deleteOwner(ownerIds) {
  return request({
    url: `/property/owner/${ownerIds}`,
    method: 'delete'
  })
}

export function getTenantList(params) {
  return request({
    url: '/property/tenant/list',
    method: 'get',
    params
  })
}

export function getTenantInfo(tenantId) {
  return request({
    url: `/property/tenant/${tenantId}`,
    method: 'get'
  })
}

export function addTenant(data) {
  return request({
    url: '/property/tenant',
    method: 'post',
    data
  })
}

export function updateTenant(data) {
  return request({
    url: '/property/tenant',
    method: 'put',
    data
  })
}

export function deleteTenant(tenantIds) {
  return request({
    url: `/property/tenant/${tenantIds}`,
    method: 'delete'
  })
}

export function getParkingSpaceList(params) {
  return request({
    url: '/property/parking/list',
    method: 'get',
    params
  })
}

export function getParkingSpaceInfo(spaceId) {
  return request({
    url: `/property/parking/${spaceId}`,
    method: 'get'
  })
}

export function addParkingSpace(data) {
  return request({
    url: '/property/parking',
    method: 'post',
    data
  })
}

export function updateParkingSpace(data) {
  return request({
    url: '/property/parking',
    method: 'put',
    data
  })
}

export function deleteParkingSpace(spaceIds) {
  return request({
    url: `/property/parking/${spaceIds}`,
    method: 'delete'
  })
}