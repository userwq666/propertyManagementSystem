import request from '@/utils/request'

export function getOwnerList(params) {
  return request({
    url: '/community/owner/page',
    method: 'get',
    params
  })
}

export function getOwnerInfo(ownerId) {
  return request({
    url: `/community/owner/${ownerId}`,
    method: 'get'
  })
}

export function addOwner(data) {
  return request({
    url: '/community/owner',
    method: 'post',
    data
  })
}

export function updateOwner(data) {
  return request({
    url: '/community/owner',
    method: 'put',
    data
  })
}

export function deleteOwner(id) {
  return request({
    url: `/community/owner/${ownerId}`,
    method: 'delete'
  })
}

export function getOwnerHouseList(params) {
  return request({
    url: '/community/owner/houses',
    method: 'get',
    params
  })
}

export function bindOwnerHouse(data) {
  return request({
    url: '/community/owner/bindHouse',
    method: 'post',
    data
  })
}

export function unbindOwnerHouse(data) {
  return request({
    url: '/community/owner/unbindHouse',
    method: 'post',
    data
  })
}

export function uploadIdCard(file, ownerId, type) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('ownerId', ownerId)
  formData.append('type', type)
  return request({
    url: '/community/owner/uploadIdCard',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getIdCardInfo(ownerId) {
  return request({
    url: \/community/owner/\/idCard\,
    method: 'get'
  })
}

export function deleteIdCard(ownerId, type) {
  return request({
    url: \/community/owner/\/idCard/\\,
    method: 'delete'
  })
}
