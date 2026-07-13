import request from '@/utils/request'

export function generateBills(data) {
  return request({
    url: '/api/fee/record/generate',
    method: 'post',
    data
  })
}

export function getFeeRecord(id) {
  return request({
    url: `/api/fee/record/${id}`,
    method: 'get'
  })
}

export function pageFeeRecord(params) {
  return request({
    url: '/api/fee/record/page',
    method: 'get',
    params
  })
}

export function confirmPay(id, payWay) {
  return request({
    url: '/api/fee/record/pay',
    method: 'put',
    params: { id, payWay }
  })
}

export function getStatistics(params) {
  return request({
    url: '/api/fee/record/statistics',
    method: 'get',
    params
  })
}