import request from '../utils/request'

export function getOwnerPage(params) {
  return request({
    url: '/api/owner/page',
    method: 'get',
    params
  })
}

export function getOwnerList() {
  return request({
    url: '/api/owner/list',
    method: 'get'
  })
}

export function getOwnerById(id) {
  return request({
    url: `/api/owner/${id}`,
    method: 'get'
  })
}

export function addOwner(data) {
  return request({
    url: '/api/owner',
    method: 'post',
    data
  })
}

export function updateOwner(data) {
  return request({
    url: '/api/owner',
    method: 'put',
    data
  })
}

export function deleteOwner(id) {
  return request({
    url: `/api/owner/${id}`,
    method: 'delete'
  })
}

export function bindUser(ownerId, userId) {
  return request({
    url: '/api/owner/bindUser',
    method: 'post',
    params: { ownerId, userId }
  })
}
