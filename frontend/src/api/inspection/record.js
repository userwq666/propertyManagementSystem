import request from '@/utils/request'

export function addRecord(data) {
  return request({ url: '/inspection/record', method: 'post', data })
}

export function updateRecord(data) {
  return request({ url: '/inspection/record', method: 'put', data })
}

export function deleteRecord(id) {
  return request({ url: `/inspection/record/${id}`, method: 'delete' })
}

export function getRecordById(id) {
  return request({ url: `/inspection/record/${id}`, method: 'get' })
}

export function getRecordPage(params) {
  return request({ url: '/inspection/record/page', method: 'get', params })
}

export function acceptRecord(id) {
  return request({ url: `/inspection/record/${id}/accept`, method: 'put' })
}

export function createRecordRepair(id) {
  return request({ url: `/inspection/record/${id}/repair`, method: 'post' })
}

export function getRecordLogs(id) {
  return request({ url: `/inspection/record/${id}/logs`, method: 'get' })
}
