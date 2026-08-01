import request from '@/utils/request'

export function addComplaint(data) {
  return request({ url: '/complaint/suggest', method: 'post', data })
}

export function updateComplaint(data) {
  return request({ url: '/complaint/suggest', method: 'put', data })
}

export function deleteComplaint(id) {
  return request({ url: `/complaint/suggest/${id}`, method: 'delete' })
}

export function getComplaintPage(params) {
  return request({ url: '/complaint/suggest/page', method: 'get', params })
}

export function getComplaintById(id) {
  return request({ url: `/complaint/suggest/${id}`, method: 'get' })
}

export function updateComplaintStatus(params) {
  return request({ url: '/complaint/suggest/status', method: 'put', params })
}

export function evaluateComplaint(params) {
  return request({ url: '/complaint/suggest/evaluate', method: 'put', params })
}
