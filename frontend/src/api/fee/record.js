import request from '@/utils/request'

export function generateFeeRecords(data) {
  return request({ url: '/fee/record/generate', method: 'post', data })
}

export function getFeeRecordById(id) {
  return request({ url: `/fee/record/${id}`, method: 'get' })
}

export function getFeeRecordPage(params) {
  return request({ url: '/fee/record/page', method: 'get', params })
}

export function payFeeRecord(params) {
  return request({ url: '/fee/record/pay', method: 'put', params })
}

export function getFeeStatistics() {
  return request({ url: '/fee/record/statistics', method: 'get' })
}

export function markOverdue() {
  return request({ url: '/fee/record/markOverdue', method: 'put' })
}
