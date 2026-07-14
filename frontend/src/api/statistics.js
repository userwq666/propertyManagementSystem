import request from '@/utils/request'

export function getStatisticsOverview() {
  return request({
    url: '/api/statistics/overview',
    method: 'get'
  })
}

export function getFeeMonthlyStatistics(year) {
  return request({
    url: '/api/statistics/fee/monthly',
    method: 'get',
    params: { year }
  })
}

export function getFeeByItemStatistics() {
  return request({
    url: '/api/statistics/fee/byItem',
    method: 'get'
  })
}

export function getRepairOverview() {
  return request({
    url: '/api/statistics/repair/overview',
    method: 'get'
  })
}

export function getRepairByTypeStatistics() {
  return request({
    url: '/api/statistics/repair/byType',
    method: 'get'
  })
}
