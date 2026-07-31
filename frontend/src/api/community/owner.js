import request from '@/utils/request'

export function addOwner(data) {
  return request({ url: '/community/owner', method: 'post', data })
}

export function updateOwner(data) {
  return request({ url: '/community/owner', method: 'put', data })
}

export function deleteOwner(id) {
  return request({ url: `/community/owner/${id}`, method: 'delete' })
}

export function getOwnerById(id) {
  return request({ url: `/community/owner/${id}`, method: 'get' })
}

export function getOwnerPage(params, config = {}) {
  return request({ url: '/community/owner/page', method: 'get', params, ...config })
}
