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
