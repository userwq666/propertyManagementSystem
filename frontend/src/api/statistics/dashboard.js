import request from '@/utils/request'

// 总览统计
export function getOverview() {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

// 月度收费统计
export function getMonthlyFeeStatistics(year) {
  return request({
    url: '/statistics/fee/monthly',
    method: 'get',
    params: { year }
  })
}

// 按项目收费统计
export function getFeeByItem() {
  return request({
    url: '/statistics/fee/byItem',
    method: 'get'
  })
}

// 报修概览统计
export function getRepairOverview() {
  return request({
    url: '/statistics/repair/overview',
    method: 'get'
  })
}

// 按类型报修统计
export function getRepairByType() {
  return request({
    url: '/statistics/repair/byType',
    method: 'get'
  })
}
