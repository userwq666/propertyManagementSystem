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

// 收费趋势（按时间范围）
export function getFeeTrend(params) {
  return request({ url: '/statistics/fee/trend', method: 'get', params })
}

// 报修趋势（按时间范围）
export function getRepairTrend(params) {
  return request({ url: '/statistics/repair/trend', method: 'get', params })
}

// 报修类型占比（按时间范围）
export function getRepairTypeRatio(params) {
  return request({ url: '/statistics/repair/typeRatio', method: 'get', params })
}

// 设备状态分布
export function getDeviceStatus(params) {
  return request({ url: '/statistics/equipment/status', method: 'get', params })
}

// 维保到期预警
export function getMaintenanceWarning(params) {
  return request({ url: '/statistics/equipment/maintenanceWarning', method: 'get', params })
}

// 满意度评分趋势
export function getSatisfactionTrend(params) {
  return request({ url: '/statistics/complaint/satisfactionTrend', method: 'get', params })
}

// 投诉类型分布
export function getComplaintTypeRatio(params) {
  return request({ url: '/statistics/complaint/typeRatio', method: 'get', params })
}

// 巡检完成率
export function getInspectionCompletion(params) {
  return request({ url: '/statistics/inspection/completion', method: 'get', params })
}

// 巡检异常率
export function getInspectionAbnormal(params) {
  return request({ url: '/statistics/inspection/abnormal', method: 'get', params })
}