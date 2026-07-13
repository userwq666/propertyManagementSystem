import request from '@/utils/request'

export function addComplaintSuggest(data) {
  return request({ url: '/api/complaint/suggest', method: 'post', data })
}

export function updateComplaintSuggest(data) {
  return request({ url: '/api/complaint/suggest', method: 'put', data })
}

export function deleteComplaintSuggest(id) {
  return request({ url: `/api/complaint/suggest/${id}`, method: 'delete' })
}

export function getComplaintSuggest(id) {
  return request({ url: `/api/complaint/suggest/${id}`, method: 'get' })
}

export function pageComplaintSuggest(params) {
  return request({ url: '/api/complaint/suggest/page', method: 'get', params })
}

export function updateComplaintSuggestStatus(params) {
  return request({ url: '/api/complaint/suggest/status', method: 'put', params })
}

export function updateComplaintSuggestRating(params) {
  return request({ url: '/api/complaint/suggest/rating', method: 'put', params })
}
